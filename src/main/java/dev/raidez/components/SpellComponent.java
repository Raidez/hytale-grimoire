package dev.raidez.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;
import dev.raidez.metadata.Spell;

public class SpellComponent extends Spell implements Component<EntityStore> {

    public static ComponentType<EntityStore, SpellComponent> getComponentType() {
        return GrimoirePlugin.get().getSpellComponentType();
    }

    private SpellComponent() {
        super();
    }

    private SpellComponent(Spell spell) {
        super.builder(spell).build();
    }

    @Override
    public Component<EntityStore> clone() {
        return new SpellComponent(this);

    }
}
