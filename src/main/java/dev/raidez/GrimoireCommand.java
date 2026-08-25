package dev.raidez;

import com.hypixel.hytale.builtin.portals.commands.utils.CursedHeldItemCommand;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class GrimoireCommand extends AbstractPlayerCommand {

    private final RequiredArg<Action> actionArg;

    enum Action {
        Give, // Give a grimoire to the player
        Slot, // Change the spell slot of the grimoire in the player's hand
        Cast, // Cast the current spell of the grimoire in the player's hand
        Infuse, // Infuse a spell into the grimoire in the player's hand
        Remove, // Remove a spell from the grimoire in the player's hand
        Purge, // Remove all spells from the grimoire in the player's hand
    }

    public GrimoireCommand() {
        super("grimoire", "Grimoire commande");
        actionArg = withRequiredArg("action", "Action to perform", ArgTypes.forEnum("action", Action.class));
    }

    @Override
    protected void execute(
            CommandContext commandContext,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef playerRef,
            World world) {

        var action = commandContext.get(actionArg);
        switch (action) {
            case Give -> giveGrimoire(commandContext, store, ref);
            case Slot -> changeSlot(commandContext, store, ref);
            case Cast -> castSpell(commandContext, store, ref);
            case Infuse -> infuseSpell(commandContext, store, ref);
            case Remove -> removeSpell(commandContext, store, ref);
            case Purge -> purgeSpells(commandContext, store, ref);
            default -> commandContext.sendMessage(Message.raw("Unknown action: " + action));
        }
    }

    /**
     * Give grimoire with metadata to player
     * 
     * @see CursedHeldItemCommand
     * @param commandContext
     * @param store
     * @param ref
     */
    private void giveGrimoire(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // Create a new grimoire item stack and add metadata
        var is = new ItemStack("Weapon_Grimoire");
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);

        // Add some spells to the grimoire
        grimoire.clearSpells();
        grimoire.addSpells(new String[] { "Spell1", "Spell2", "Spell3" });

        // Update the item stack with the new metadata
        is = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);

        // Give the item stack to the player
        Player.giveItem(is, ref, store);

        // Send a message to the player
        commandContext.sendMessage(Message.raw("Given grimoire with predefined spells to player."));
    }

    /**
     * Change the spell slot of the grimoire in the player's hand
     * 
     * @param commandContext
     * @param store
     * @param ref
     */
    private void changeSlot(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // Check if the player is holding a grimoire
        var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        var is = inventory.getActiveItem();
        if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
            commandContext.sendMessage(Message.raw("You must hold a grimoire to change the slot."));
            return;
        }

        // Change the spell slot
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        grimoire.changeSpellSlot();

        // Update the item stack with the new metadata
        var newIs = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);
        inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);

        // Send a message to the player
        commandContext.sendMessage(Message.raw("Changed spell slot to: " + grimoire.getCurrentSpell()));
    }

    /**
     * Cast the current spell of the grimoire in the player's hand
     * 
     * @param commandContext
     * @param store
     * @param ref
     */
    private void castSpell(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // Check if the player is holding a grimoire
        var is = InventoryComponent.getItemInHand(store, ref);
        if (is == null || !is.getItemId().equals("Weapon_Grimoire")) {
            commandContext.sendMessage(Message.raw("You must hold a grimoire to cast a spell."));
            return;
        }

        // Get the grimoire metadata and the current spell
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        var spell = grimoire.getCurrentSpell();
        if (spell == null) {
            commandContext.sendMessage(Message.raw("The grimoire has no spells."));
            return;
        }

        // Cast the spell (for now, just send a message)
        commandContext.sendMessage(Message.raw("Casting spell: " + spell));
    }

    /**
     * Infuse a spell into the grimoire in the player's hand
     * 
     * @param commandContext
     * @param store
     * @param ref
     */
    private void infuseSpell(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'infuseSpell'");
    }

    /**
     * Remove a spell from the grimoire in the player's hand
     * 
     * @param commandContext
     * @param store
     * @param ref
     */
    private void removeSpell(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeSpell'");
    }

    /**
     * Remove all spells from the grimoire in the player's hand
     * 
     * @param commandContext
     * @param store
     * @param ref
     */
    private void purgeSpells(CommandContext commandContext, Store<EntityStore> store, Ref<EntityStore> ref) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'purgeSpells'");
    }
}
