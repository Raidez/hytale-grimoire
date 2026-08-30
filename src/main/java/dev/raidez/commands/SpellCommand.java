package dev.raidez.commands;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
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
import dev.raidez.resources.Spell;

public class SpellCommand extends AbstractCommandCollection {

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
            spellArg = withRequiredArg("spell", "Spell to give", Spell.SPELL_ASSET);
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
                commandContext.sendMessage(Message.raw("Invalid spell!"));
                return;
            }

            // Get the item linked to the spell
            var item = spell.getItem();
            if (item == null) {
                commandContext.sendMessage(Message.raw("Spell has no linked scroll!"));
                return;
            }

            // Give the item to the player
            var is = new ItemStack(item.getId());
            Player.giveItem(is, ref, store);
            commandContext.sendMessage(Message.raw("You have been given the spell: " + spell.getName()));
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
                commandContext.sendMessage(Message.raw("You must hold a scroll!"));
                return;
            }

            // Check if the item has spell tag and get the spell
            var spell = checkPrerequisites(is);
            if (spell == null) {
                commandContext.sendMessage(Message.raw("This scroll has no spell linked to it!"));
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

        private final DefaultArg<Which> whichArg;
        private final OptionalArg<Spell> spellArg;

        enum Which {
            HAND, // From the item in hand
            ARGUMENT, // From the spell argument
        }

        public CastCommand() {
            super("cast", "Cast a spell from the scroll in hand or from the spell argument");
            whichArg = withDefaultArg("which", "Which spell to cast",
                    ArgTypes.forEnum("which", Which.class),
                    Which.HAND, "hand");
            spellArg = withOptionalArg("spell", "Spell to cast", Spell.SPELL_ASSET);
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var spellInteractionId = "";

            var which = whichArg.get(commandContext);
            if (Which.ARGUMENT.equals(which)) {

                // Get the spell from the command argument
                var spell = spellArg.get(commandContext);
                if (spell == null) {
                    commandContext.sendMessage(Message.raw("You must specify a spell to cast!"));
                    return;
                }

                spellInteractionId = spell.getInteractionId();

            } else if (Which.HAND.equals(which)) {

                // Check if the player is holding an item
                var is = InventoryComponent.getItemInHand(store, ref);
                if (is == null) {
                    commandContext.sendMessage(Message.raw("You must hold a scroll!"));
                    return;
                }

                // Check if the item has spell tag and get the spell
                var spell = checkPrerequisites(is);
                if (spell == null) {
                    commandContext.sendMessage(Message.raw("This scroll has no spell linked to it!"));
                    return;
                }

                spellInteractionId = spell.getInteractionId();
            }

            // Execute the interaction for the spell
            Utils.executeInteraction(spellInteractionId, store, ref);
            commandContext.sendMessage(Message.raw("You have cast the spell!"));
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
        var tagIndex = AssetRegistry.getOrCreateTagIndex("Scroll");
        var tags = is.getItem().getData().getTags();
        if (!tags.containsKey(tagIndex)) {
            return null;
        }

        // Get the spell
        var spell = Spell.getFromItem(is.getItem());
        return spell;
    }

}
