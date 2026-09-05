package dev.raidez.handlers;

import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import dev.raidez.resources.GrimoireMetadata;

/**
 * @deprecated Use GrimoireInventoryChangeHandler instead.
 */
public class CraftGrimoireHandler {

    @SuppressWarnings("removal")
    public static void onCraftGrimoire(PlayerCraftEvent event) {
        // Check if the crafted item is a grimoire
        var output = event.getCraftedRecipe().getPrimaryOutput();
        if (!output.getItemId().equals("Weapon_Grimoire"))
            return;

        // Get player inventory
        var ref = event.getPlayerRef();
        var store = event.getPlayerRef().getStore();
        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        if (ref == null || store == null || inventory == null)
            return;

        // Add metadata to the crafted grimoire
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            var is = inventory.getItemStack(slot);
            if (is == null || is.isEmpty())
                continue;

            if (!output.getItemId().equals(is.getItemId()))
                continue;

            var newIs = ensureMetadata(is);
            inventory.replaceItemStackInSlot(slot, is, newIs);
        }
    }

    private static ItemStack ensureMetadata(ItemStack is) {
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        return is.withMetadata(GrimoireMetadata.KEYED_CODEC, meta);
    }

}
