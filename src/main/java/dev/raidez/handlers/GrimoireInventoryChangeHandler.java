package dev.raidez.handlers;

import java.util.ArrayList;
import java.util.List;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;
import dev.raidez.resources.GrimoireMetadata;

public class GrimoireInventoryChangeHandler extends EntityEventSystem<EntityStore, InventoryChangeEvent> {

    private final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    private final Query<EntityStore> query;

    public GrimoireInventoryChangeHandler() {
        super(InventoryChangeEvent.class);
        query = Archetype.of(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    @Override
    public void handle(
            int index,
            ArchetypeChunk<EntityStore> archetypeChunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            InventoryChangeEvent event) {

        for (var slotTransaction : extractSlotTransactions(event.getTransaction())) {

            // Check if the slot transaction succeeded
            if (!slotTransaction.succeeded())
                continue;

            // Check if the item stack is a grimoire
            var is = slotTransaction.getSlotAfter();
            if (is == null || is.isEmpty() || !"Weapon_Grimoire".equals(is.getItemId()))
                continue;

            // Check if the item stack already has grimoire metadata
            if (is.getFromMetadataOrNull(GrimoireMetadata.KEYED_CODEC) != null)
                continue;

            // Replace the grimoire with metadata
            var slot = slotTransaction.getSlot();
            event.getItemContainer().replaceItemStackInSlot(slot, is, ensureMetadata(is));
            LOGGER.atInfo().log("Replaced grimoire with metadata in slot: " + slot);
        }
    }

    private static ItemStack ensureMetadata(ItemStack is) {
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        return is.withMetadata(GrimoireMetadata.KEYED_CODEC, meta);
    }

    /**
     * Flattens any Transaction wrapper
     * (SlotTransaction, ItemStackTransaction, ListTransaction)
     * into its leaf slot transactions.
     * 
     * @param transaction
     * @return
     */
    private static List<SlotTransaction> extractSlotTransactions(Transaction transaction) {
        if (transaction instanceof SlotTransaction slotTransaction) {
            return List.of(slotTransaction);
        }

        if (transaction instanceof ItemStackTransaction itemStackTransaction) {
            return List.copyOf(itemStackTransaction.getSlotTransactions());
        }

        if (transaction instanceof ListTransaction<?> listTransaction) {
            var result = new ArrayList<SlotTransaction>();
            for (var inner : listTransaction.getList())
                result.addAll(extractSlotTransactions(inner));
            return result;
        }

        return List.of();
    }
}
