package dev.raidez.handlers;

import java.util.List;

import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;

public class CraftSpellHandler {

    private static List<String> spellList = List.of(
            "Spell_Fireball",
            "Spell_IceShard",
            "Spell_LightningBolt",
            "Spell_HealingWave",
            "Spell_Shield",
            "Spell_Teleportation",
            "Spell_SummonCreature",
            "Spell_Invisibility",
            "Spell_Fear",
            "Spell_Levitation");

    public static void onCraftSpell(PlayerCraftEvent event) {
        // Check if the crafted item is in the spell list
        var output = event.getCraftedRecipe().getPrimaryOutput();
        if (!spellList.contains(output.getItemId()))
            return;

        // Get player inventory
        var ref = event.getPlayerRef();
        var store = event.getPlayerRef().getStore();
        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        if (ref == null || store == null || inventory == null)
            return;

        // Add metadata to the crafted spell item
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            var is = inventory.getItemStack(slot);
            if (is == null || is.isEmpty())
                continue;

            if (!output.getItemId().equals(is.getItemId()))
                continue;

            // var newIs = ensureMetadata(is);
            // inventory.replaceItemStackInSlot(slot, is, newIs);
        }
    }

    private static ItemStack ensureMetadata(ItemStack is) {
        return ItemStack.EMPTY;
    }

}
