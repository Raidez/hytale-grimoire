package dev.raidez.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.Utils;
import dev.raidez.resources.Grimoire;
import dev.raidez.resources.Spell;

public class GrimoireCommand extends AbstractCommandCollection {

    public GrimoireCommand() {
        super("grimoire", "Grimoire command");
        addSubCommand(new GiveCommand());
        addSubCommand(new SlotCommand());
        addSubCommand(new CastCommand());
        addSubCommand(new InfuseCommand());
        addSubCommand(new CheckCommand());
        addSubCommand(new UICommand());
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
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);

            // Add some spells to the grimoire
            grimoire.addSpell("Spell_Fireball");

            // Update the item stack with the new metadata
            is = is.withMetadata(Grimoire.KEYED_CODEC, grimoire);

            // Give the item stack to the player
            Player.giveItem(is, ref, store);

            // Send a message to the player
            commandContext.sendMessage(Message.raw("Given grimoire with predefined spells to player."));
        }
    }

    class SlotCommand extends AbstractPlayerCommand {

        private final DefaultArg<Operation> operationArg;

        private final DefaultArg<Integer> slotArg;

        enum Operation {
            /** Set the spell slot to a specific value */
            SET,
            /** Change to the next spell slot */
            NEXT,
            /** Change to the previous spell slot */
            PREVIOUS,
        }

        public SlotCommand() {
            super("slot", "Change the spell slot of the grimoire in the player's hand");
            operationArg = withDefaultArg("operation", "Operation to perform",
                    ArgTypes.forEnum("operation", Operation.class), Operation.NEXT, "next");
            slotArg = withDefaultArg("slot", "Slot number to set", ArgTypes.INTEGER, 0, "0");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var operation = commandContext.get(operationArg);
            var slot = commandContext.get(slotArg);

            // Check if the player is holding a grimoire
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
            var is = inventory.getActiveItem();
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to change the slot!"));
                return;
            }

            // Change the spell slot
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);
            var currentSlot = grimoire.getCurrentSlot();
            switch (operation) {
                case SET -> grimoire.changeSpellSlot(slot);
                case NEXT -> grimoire.changeSpellSlot(currentSlot + 1);
                case PREVIOUS -> grimoire.changeSpellSlot(currentSlot - 1);
            }

            // Update the item stack with the new metadata
            var newIs = is.withMetadata(Grimoire.KEYED_CODEC, grimoire);
            inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);

            // Send a message to the player
            commandContext.sendMessage(Message.raw("Changed spell slot to: " + grimoire.getCurrentSlot()));
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
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to cast a spell!"));
                return;
            }

            // Get the grimoire metadata and the current spell
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);
            var spellId = grimoire.getCurrentSpell();
            if (spellId == null) {
                commandContext.sendMessage(Message.raw("The grimoire has no spells!"));
                return;
            }

            // Cast the spell
            var spell = Spell.getAssetMap().getAsset(spellId);
            Utils.executeInteraction(spell.getInteractionId(), store, ref);
            commandContext.sendMessage(Message.raw("Casting spell: " + spell.getName()));
        }
    }

    class InfuseCommand extends AbstractPlayerCommand {

        private final DefaultArg<Operation> operationArg;

        private final DefaultArg<Spell> spellArg;

        private final OptionalArg<Integer> slotArg;

        enum Operation {
            /** Infuse a spell into the grimoire in the player's hand */
            ADD,
            /** Remove a spell from the grimoire in the player's hand */
            REMOVE,
            /** Remove all spells from the grimoire in the player's hand */
            PURGE,
        }

        public InfuseCommand() {
            super("infuse", "Infuse a spell into the grimoire in the player's hand");
            operationArg = withDefaultArg("operation", "Operation to perform",
                    ArgTypes.forEnum("operation", Operation.class), Operation.ADD, "add");
            spellArg = withDefaultArg("spell", "Spell to infuse", Spell.SPELL_ASSET, null, "");
            slotArg = withOptionalArg("slot", "Slot to infuse the spell into", ArgTypes.INTEGER);
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var operation = commandContext.get(operationArg);
            var spell = commandContext.get(spellArg);
            var slot = commandContext.get(slotArg);
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());

            // Check if the player is holding a grimoire
            var is = InventoryComponent.getItemInHand(store, ref);
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to cast a spell!"));
                return;
            }

            // Get the grimoire metadata and perform the operation
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);
            switch (operation) {
                case ADD -> {
                    if (slot != null) {
                        // Add the spell to the specified slot
                        grimoire.addSpell(slot, spell.getId());
                    } else {
                        // Add to the current slot
                        grimoire.addSpell(spell.getId());
                    }
                }
                case REMOVE -> {
                    if (slot != null) {
                        // Remove the spell from the specified slot
                        grimoire.removeSpell(slot);
                    } else {
                        // Remove from the current slot
                        grimoire.removeSpell(spell.getId());
                    }
                }
                case PURGE -> grimoire.clearSpells();
            }

            // Update the item stack with the new metadata
            var newIs = is.withMetadata(Grimoire.KEYED_CODEC, grimoire);
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
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to check its spells!"));
                return;
            }

            // Get the grimoire metadata and send the list of spells to the player
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);
            var spells = grimoire.getSpellList();
            commandContext.sendMessage(Message.raw("Current spells in grimoire: " + String.join(", ", spells)));
        }
    }

    class UICommand extends AbstractPlayerCommand {

        public UICommand() {
            super("ui", "Open the slot UI page");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var player = store.getComponent(ref, Player.getComponentType());

            // Check if the player is holding a grimoire
            var is = InventoryComponent.getItemInHand(store, ref);
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to open the slot UI page!"));
                return;
            }

            // Get the grimoire metadata from the item in hand
            var grimoire = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);

            // Open the slot UI page with the current spells from the grimoire
            // var page = new InfusePage(playerRef, grimoire.getScrollList());
            // player.getPageManager().openCustomPage(ref, store, page);
        }

    }

}
