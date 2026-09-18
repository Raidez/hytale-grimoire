package dev.raidez.handlers;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import dev.raidez.GrimoirePlugin;
import dev.raidez.resources.Grimoire;

/**
 * @deprecated Use GrimoireInventoryChangeHandler instead.
 */
public class CraftGrimoireHandler {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    @SuppressWarnings("removal")
    public static void onCraftGrimoire(PlayerCraftEvent event) {
        // Check if the crafted item is a grimoire
        var output = event.getCraftedRecipe().getPrimaryOutput();
        if (!output.getItemId().equals("Weapon_Grimoire")) {
            LOGGER.atWarning().log("CraftGrimoireHandler: Crafted item is not a grimoire.");
            return;
        }

        // Get player inventory
        var ref = event.getPlayerRef();
        var store = event.getPlayerRef().getStore();
        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        if (ref == null || store == null || inventory == null) {
            LOGGER.atWarning().log("CraftGrimoireHandler: Player inventory is not accessible.");
            return;
        }

        // Add metadata to the crafted grimoire
        var wasModified = false;
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            var is = inventory.getItemStack(slot);
            if (is == null || is.isEmpty())
                continue;

            if (!output.getItemId().equals(is.getItemId()))
                continue;

            var newIs = ensureMetadata(is);
            inventory.replaceItemStackInSlot(slot, is, newIs);
            wasModified = true;
        }

        if (!wasModified) {
            LOGGER.atWarning().log("CraftGrimoireHandler: No grimoire was modified in the inventory.");
        }
    }

    private static ItemStack ensureMetadata(ItemStack is) {
        var meta = is.getFromMetadataOrDefault(Grimoire.KEY, Grimoire.CODEC);
        return is.withMetadata(Grimoire.KEYED_CODEC, meta);
    }

}
