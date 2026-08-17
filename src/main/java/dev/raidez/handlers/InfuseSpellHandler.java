package dev.raidez.handlers;

import java.util.ArrayList;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.ecs.InventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;
import dev.raidez.metadata.GrimoireMetadata;
import dev.raidez.metadata.SpellMetadata;

public class InfuseSpellHandler extends EntityEventSystem<EntityStore, InventoryChangeEvent> {

    public static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public InfuseSpellHandler() {
        super(InventoryChangeEvent.class);
    }

    public static void onInfuseSpell(InventoryChangeEvent event) {
        // var container = event.container();

        // for (short slot = 0; slot < container.getCapacity(); slot++) {
        // var is = container.getItemStack(slot);
        // if (is == null || is.isEmpty())
        // continue;

        // if (!SpellMetadata.spellMap.containsKey(is.getItemId()))
        // continue;
        // }
    }

    @Override
    public Query<EntityStore> getQuery() {
        // @see ItemStackItemContainer
        // @see OpenItemStackContainerInteraction
        return Archetype.empty();
    }

    @Override
    public void handle(
            int index,
            ArchetypeChunk<EntityStore> archetypeChunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            InventoryChangeEvent event) {

        // on start 4 events (Clear creative tool, List creative tool, List armor, List
        // inventory)
        // on craft 2 events (ItemStackTransaction, ItemStackSlotTransaction)
        // on put (ItemStackSlotTransaction, MoveTransaction)
        // on take (MoveTransaction, ItemStackSlotTransaction, MoveTransaction)

        switch (event.getTransaction()) {
            case MoveTransaction<?> transaction:
                LOGGER.atInfo().log("MoveTransaction: %s", transaction);
                break;
            case ItemStackSlotTransaction transaction:
                LOGGER.atInfo().log("ItemStackSlotTransaction: %s", transaction);
                break;
            default:
                LOGGER.atInfo().log("Unhandled transaction type: %s", event.getTransaction().getClass().getName());
                break;
        }

        // Update the spell list in the grimoire if a spell item was moved into it
        // ItemStack updatedIs = null;
        // if (event.getTransaction() instanceof MoveTransaction<?>) {
        // updatedIs = updateSpellList((MoveTransaction<?>) event.getTransaction());
        // } else {
        // LOGGER.atInfo().log("Unhandled transaction type: %s",
        // event.getTransaction().getClass().getName());
        // }

        // if (updatedIs == null)
        // return;

        // Update the grimoire item stack in the container if it was modified
        // LOGGER.atInfo().log("Updated grimoire item stack: %s", updatedIs);

        // var container = event.getItemContainer();
        // for (short slot = 0; slot < container.getCapacity(); slot++) {
        // var is = container.getItemStack(slot);
        // if (is == null || is.isEmpty())
        // continue;

        // if (is.getItemId().equals("Weapon_Grimoire")) {
        // container.setItemStackForSlot(slot, updatedIs);
        // LOGGER.atInfo().log("Updated grimoire in container at slot %d", slot);
        // break;
        // }
        // }
    }

    private ItemStack updateSpellList(MoveTransaction<?> transaction) {

        if (transaction.getOtherContainer() instanceof ItemStackItemContainer == false)
            return null;

        // Get the destination container of the transaction
        var dest = (ItemStackItemContainer) transaction.getOtherContainer();
        if (dest == null)
            return null;

        // Check if the destination container is a grimoire
        var is = dest.getOriginalItemStack();
        if (is == null || is.isEmpty() || !is.getItemId().equals("Weapon_Grimoire"))
            return null;

        LOGGER.atInfo().log("Get grimoire item stack: %s", is);

        // Get the spell list from the grimoire container
        var spellList = new ArrayList<String>();
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.METADATA_KEY, GrimoireMetadata.CODEC);
        for (short slot = 0; slot < dest.getCapacity(); slot++) {
            var itemStack = dest.getItemStack(slot);
            if (itemStack == null || itemStack.isEmpty())
                continue;

            if (SpellMetadata.spellMap.containsKey(itemStack.getItemId()))
                spellList.add(itemStack.getItemId());
        }

        // Update the grimoire metadata with the new spell list
        LOGGER.atInfo().log("Updated spell list: %s", spellList);
        meta.setSpellList(spellList.toArray(new String[0]));

        return is.withMetadata(GrimoireMetadata.KEYED_CODEC, meta);
    }
}
