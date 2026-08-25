package dev.raidez;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
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

public class SpellCommand extends AbstractCommandCollection {

    public static final SingleArgumentType<Spell> SPELL_ASSET = new AssetArgumentType<>("", Spell.class, "");

    public SpellCommand() {
        super("spell", "Spell command");
        addAliases("sp");
        addSubCommand(new GiveCommand());
        addSubCommand(new CheckCommand());
        addSubCommand(new CastCommand());
    }

    class GiveCommand extends AbstractPlayerCommand {

        private final RequiredArg<Spell> spellArg;

        public GiveCommand() {
            super("give", "Give a spell to a player from the spell list");
            spellArg = withRequiredArg("spell", "Spell to give", SPELL_ASSET);
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Get the spell from the command argument
            var spell = spellArg.get(commandContext);
            if (spell == null) {
                commandContext.sendMessage(Message.raw("Invalid spell"));
                return;
            }

            // Get the item linked to the spell
            var item = spell.getItem();
            if (item == null) {
                commandContext.sendMessage(Message.raw("Spell has no linked item"));
                return;
            }

            // Give the item to the player
            var is = new ItemStack(item.getId());
            Player.giveItem(is, ref, store);
        }

    }

    class CheckCommand extends AbstractPlayerCommand {

        public CheckCommand() {
            super("check", "Check if holding item has spell linked to it and give info");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Check if the player is holding an item
            var is = InventoryComponent.getItemInHand(store, ref);
            if (is == null) {
                commandContext.sendMessage(Message.raw("You must hold an item."));
                return;
            }

            // Check if the item has spell tag and get the spell
            var spell = checkPrerequisites(is);
            if (spell == null) {
                commandContext.sendMessage(Message.raw("This item has no spell linked to it."));
                return;
            }

            // Send the spell info to the player
            var fields = new LinkedHashMap<String, Object>();
            fields.put("Name", spell.getName());
            fields.put("Description", spell.getDescription());
            fields.put("Mana Cost", spell.getManaCost());
            fields.put("Cooldown", spell.getCooldown());
            fields.put("Cast Time", spell.getCastTime());
            fields.put("Interaction ID", spell.getInteractionId());
            fields.put("Texture path", spell.getTexture());
            var content = fields.entrySet()
                    .stream()
                    .filter(f -> f.getValue() != null)
                    .map(f -> f.getKey() + ": " + f.getValue().toString())
                    .collect(Collectors.joining("\n\t-"));
            commandContext
                    .sendMessage(Message.raw("This item is linked to spell:\n\t-" + content));
        }

    }

    class CastCommand extends AbstractPlayerCommand {

        private final OptionalArg<Spell> spellArg;

        public CastCommand() {
            super("cast", "Cast a spell from the spell list or from the item in hand");
            spellArg = withOptionalArg("spell", "Spell to cast", SPELL_ASSET);
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Get the spell from the command argument
            var spell = spellArg.get(commandContext);
            if (spell == null) {
                // Check if the player is holding an item
                var is = InventoryComponent.getItemInHand(store, ref);
                if (is == null) {
                    commandContext.sendMessage(Message.raw("You must hold an item."));
                    return;
                }

                // Check if the item has spell tag and get the spell
                spell = checkPrerequisites(is);
                if (spell == null) {
                    commandContext.sendMessage(Message.raw("This item has no spell linked to it."));
                    return;
                }
            }

            // Execute the interaction for the spell
            executeInteraction(spell.getInteractionId(), store, ref);
        }

    }

    /**
     * Check if the item has spell tag and return the spell if it does
     * 
     * @param is
     * @return
     */
    private Spell checkPrerequisites(ItemStack is) {
        // Check if the item has spell tag
        var tagIndex = AssetRegistry.getOrCreateTagIndex("Spell");
        var tags = is.getItem().getData().getTags();
        if (!tags.containsKey(tagIndex)) {
            return null;
        }

        // Get the spell
        var spell = Spell.getFromItem(is.getItem());
        return spell;
    }

    /**
     * Execute the interaction for the spell
     * 
     * @param interaction
     * @param store
     * @param ref
     */
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
