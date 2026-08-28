package dev.raidez;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class GrimoireCommand extends AbstractCommandCollection {

    public GrimoireCommand() {
        super("grimoire", "Grimoire commande");
        addAliases("grim");
        addSubCommand(new GiveCommand());
        addSubCommand(new SlotCommand());
        addSubCommand(new CastCommand());
        addSubCommand(new InfuseCommand());
        addSubCommand(new CheckCommand());
    }

    class GiveCommand extends AbstractPlayerCommand {

        public GiveCommand() {
            super("give", "Give a grimoire to the player");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Create a new grimoire item stack and add metadata
            var is = new ItemStack("Weapon_Grimoire");
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);

            // Add some spells to the grimoire
            grimoire.clearSpells();
            grimoire.addSpells("Spell1", "Spell2", "Spell3");

            // Update the item stack with the new metadata
            is = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);

            // Give the item stack to the player
            Player.giveItem(is, ref, store);

            // Send a message to the player
            commandContext.sendMessage(Message.raw("Given grimoire with predefined spells to player."));
        }
    }

    class SlotCommand extends AbstractPlayerCommand {

        private final DefaultArg<Operation> operationArg;

        enum Operation {
            Next, // Change to the next spell slot
            Previous, // Change to the previous spell slot
        }

        public SlotCommand() {
            super("slot", "Change the spell slot of the grimoire in the player's hand");
            operationArg = withDefaultArg("operation", "Operation to perform",
                    ArgTypes.forEnum("operation", Operation.class), Operation.Next, "next");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var operation = commandContext.get(operationArg);

            // Check if the player is holding a grimoire
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
            var is = inventory.getActiveItem();
            if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to change the slot."));
                return;
            }

            // Change the spell slot
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
            var delta = operation == Operation.Next ? 1 : -1;
            grimoire.changeSpellSlot(delta);

            // Update the item stack with the new metadata
            var newIs = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);
            inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);

            // Send a message to the player
            commandContext.sendMessage(Message.raw("Changed spell slot to: " + grimoire.getCurrentSpell()));
        }
    }

    class CastCommand extends AbstractPlayerCommand {

        public CastCommand() {
            super("cast", "Cast the current spell of the grimoire in the player's hand");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Check if the player is holding a grimoire
            var is = InventoryComponent.getItemInHand(store, ref);
            if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to cast a spell."));
                return;
            }

            // Get the grimoire metadata and the current spell
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
            var spellId = grimoire.getCurrentSpell();
            if (spellId == null) {
                commandContext.sendMessage(Message.raw("The grimoire has no spells."));
                return;
            }

            // Cast the spell (for now, just send a message)
            var spell = Spell.getAssetMap().getAsset(spellId);
            executeInteraction(spell.getInteractionId(), store, ref);
            commandContext.sendMessage(Message.raw("Casting spell: " + spell.getName()));
        }
    }

    class InfuseCommand extends AbstractPlayerCommand {

        private final DefaultArg<Operation> operationArg;

        private final DefaultArg<Spell> spellArg;

        enum Operation {
            Add, // Infuse a spell into the grimoire in the player's hand
            Remove, // Remove a spell from the grimoire in the player's hand
            Purge, // Remove all spells from the grimoire in the player's hand
        }

        public InfuseCommand() {
            super("infuse", "Infuse a spell into the grimoire in the player's hand");
            operationArg = withDefaultArg("operation", "Operation to perform",
                    ArgTypes.forEnum("operation", Operation.class), Operation.Add, "add");
            spellArg = withDefaultArg("spell", "Spell to infuse", Spell.SPELL_ASSET, null, "");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var operation = commandContext.get(operationArg);
            var spell2 = commandContext.get(spellArg);
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());

            // Check if the player is holding a grimoire
            var is = InventoryComponent.getItemInHand(store, ref);
            if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to cast a spell."));
                return;
            }

            // Get the grimoire metadata and perform the operation
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
            switch (operation) {
                case Add -> grimoire.addSpell(spell2.getId());
                case Remove -> grimoire.removeSpell(spell2.getId());
                case Purge -> grimoire.clearSpells();
            }

            // Update the item stack with the new metadata
            var newIs = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);
            inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);

            // Send a message to the player
            commandContext.sendMessage(Message.raw("Performed operation: " + operation
                    + " on grimoire. Current spells: " + String.join(", ", grimoire.getSpellList())));
        }
    }

    class CheckCommand extends AbstractPlayerCommand {

        public CheckCommand() {
            super("check", "Check the spells in the grimoire in the player's hand");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Check if the player is holding a grimoire
            var is = InventoryComponent.getItemInHand(store, ref);
            if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to check its spells."));
                return;
            }

            // Get the grimoire metadata and send the list of spells to the player
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
            var spells = grimoire.getSpellList();
            commandContext.sendMessage(Message.raw("Current spells in grimoire: " + String.join(", ", spells)));
        }
    }

    private void executeInteraction(
            String interaction,
            Store<EntityStore> store,
            Ref<EntityStore> ref) {

        var interactionManager = store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
        if (interactionManager == null) {
            return;
        }

        var interactionType = InteractionType.Primary;
        var context = InteractionContext.forInteraction(interactionManager, ref, interactionType, store);
        var rootInteraction = RootInteraction.getRootInteractionOrUnknown(interaction);
        if (rootInteraction == null) {
            return;
        }

        InteractionChain chain = interactionManager.initChain(interactionType, context, rootInteraction, true);
        interactionManager.queueExecuteChain(chain);
    }

}
