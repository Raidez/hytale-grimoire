package dev.raidez.systems;

import java.util.List;
import java.util.Set;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.components.SpellComponent;

public class SpellSystems {

    public static class EnsureSpellComponents extends HolderSystem<EntityStore> {

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

        private static final ComponentType<EntityStore, ItemComponent> ITEM_COMPONENT_TYPE = ItemComponent
                .getComponentType();
        private static final ComponentType<EntityStore, SpellComponent> SPELL_COMPONENT_TYPE = SpellComponent
                .getComponentType();

        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of();
        }

        @Override
        public Query<EntityStore> getQuery() {
            // return Query.and(ITEM_COMPONENT_TYPE, Query.not(SPELL_COMPONENT_TYPE));
            return Query.not(SPELL_COMPONENT_TYPE);
        }

        @Override
        public void onEntityAdd(
                Holder<EntityStore> holder,
                AddReason reason,
                Store<EntityStore> store) {

            // Get the item component from the holder
            var item = holder.getComponent(ITEM_COMPONENT_TYPE);
            if (item == null)
                return;

            // Check if the item is a spell
            var is = item.getItemStack();
            if (!spellList.contains(is.getItemId()))
                return;

            // Add the spell component to the holder
            var spell = holder.ensureAndGetComponent(SPELL_COMPONENT_TYPE);
            spell.setDescription("TESTTTTTT");
        }

        @Override
        public void onEntityRemoved(
                Holder<EntityStore> holder,
                RemoveReason reason,
                Store<EntityStore> store) {
        }

    }

}
