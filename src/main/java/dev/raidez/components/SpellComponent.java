package dev.raidez.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;

public class SpellComponent implements Component<EntityStore> {

    private String name;
    private String description;
    private int level;
    private double castTime;
    private double cooldown;
    private double manaCost;

    public static final BuilderCodec<SpellComponent> CODEC = BuilderCodec
            .builder(SpellComponent.class, SpellComponent::new)
            .append(new KeyedCodec<>("Name", Codec.STRING), (c, v) -> c.name = v, (c) -> c.name).add()
            .append(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, (c) -> c.description)
            .add()
            .append(new KeyedCodec<>("Level", Codec.INTEGER), (c, v) -> c.level = v, (c) -> c.level).add()
            .append(new KeyedCodec<>("CastTime", Codec.DOUBLE), (c, v) -> c.castTime = v, (c) -> c.castTime).add()
            .append(new KeyedCodec<>("Cooldown", Codec.DOUBLE), (c, v) -> c.cooldown = v, (c) -> c.cooldown).add()
            .append(new KeyedCodec<>("ManaCost", Codec.DOUBLE), (c, v) -> c.manaCost = v, (c) -> c.manaCost).add()
            .build();

    public static final KeyedCodec<SpellComponent> KEYED_CODEC = new KeyedCodec<>("Spell", CODEC);

    public static final String METADATA_KEY = "Spell";

    public static ComponentType<EntityStore, SpellComponent> getComponentType() {
        return GrimoirePlugin.get().getSpellComponentType();
    }

    public SpellComponent() {
        this(
                "Default Spell",
                "This is a default spell.",
                1,
                1.0,
                5.0,
                10.0);
    }

    public SpellComponent(
            String name,
            String description,
            int level,
            double castTime,
            double cooldown,
            double manaCost) {

        this.name = name;
        this.description = description;
        this.level = level;
        this.castTime = castTime;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
    }

    public SpellComponent(SpellComponent other) {
        this.name = other.name;
        this.description = other.description;
        this.level = other.level;
        this.castTime = other.castTime;
        this.cooldown = other.cooldown;
        this.manaCost = other.manaCost;
    }

    @Override
    public Component<EntityStore> clone() {
        return new SpellComponent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getCastTime() {
        return castTime;
    }

    public void setCastTime(double castTime) {
        this.castTime = castTime;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public double getManaCost() {
        return manaCost;
    }

    public void setManaCost(double manaCost) {
        this.manaCost = manaCost;
    }

}
