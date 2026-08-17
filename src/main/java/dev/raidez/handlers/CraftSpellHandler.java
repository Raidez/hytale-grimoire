package dev.raidez.handlers;

import java.util.Map;

import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import dev.raidez.metadata.SpellMetadata;

public class CraftSpellHandler {

    private static Map<String, SpellMetadata> spellMap = Map.of(
            "Spell_Fireball",
            SpellMetadata.builder()
                    .name("items.spell_fireball.shortName")
                    .description("items.spell_fireball.desc")
                    .level(1)
                    .castTime(1.0)
                    .cooldown(3.0)
                    .manaCost(5.0)
                    .interaction("Cast_Spell_Fireball")
                    .build(),
            "Spell_Firebomb",
            SpellMetadata.builder()
                    .name("items.spell_firebomb.shortName")
                    .description("items.spell_firebomb.desc")
                    .level(2)
                    .castTime(3.0)
                    .cooldown(8.0)
                    .manaCost(15.0)
                    .interaction("Cast_Spell_Firebomb")
                    .build());

    public static void onCraftSpell(PlayerCraftEvent event) {
        // Check if the crafted item is in the spell list
        var output = event.getCraftedRecipe().getPrimaryOutput();
        if (!spellMap.containsKey(output.getItemId()))
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

            var newIs = ensureMetadata(is);
            inventory.replaceItemStackInSlot(slot, is, newIs);
        }
    }

    private static ItemStack ensureMetadata(ItemStack is) {
        var meta = is.getFromMetadataOrDefault(SpellMetadata.METADATA_KEY, SpellMetadata.CODEC);
        meta = spellMap.get(is.getItemId());
        return is.withMetadata(SpellMetadata.KEYED_CODEC, meta);
    }

}
