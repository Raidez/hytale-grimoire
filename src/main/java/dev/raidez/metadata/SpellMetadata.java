package dev.raidez.metadata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SpellMetadata {

    private String name;
    private String description;
    private int level;
    private double castTime;
    private double cooldown;
    private double manaCost;

    public static final BuilderCodec<SpellMetadata> CODEC = BuilderCodec
            .builder(SpellMetadata.class, SpellMetadata::new)
            .append(new KeyedCodec<>("Name", Codec.STRING), (c, v) -> c.name = v, (c) -> c.name).add()
            .append(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, (c) -> c.description)
            .add()
            .append(new KeyedCodec<>("Level", Codec.INTEGER), (c, v) -> c.level = v, (c) -> c.level).add()
            .append(new KeyedCodec<>("CastTime", Codec.DOUBLE), (c, v) -> c.castTime = v, (c) -> c.castTime).add()
            .append(new KeyedCodec<>("Cooldown", Codec.DOUBLE), (c, v) -> c.cooldown = v, (c) -> c.cooldown).add()
            .append(new KeyedCodec<>("ManaCost", Codec.DOUBLE), (c, v) -> c.manaCost = v, (c) -> c.manaCost).add()
            .build();

    public static final KeyedCodec<SpellMetadata> KEYED_CODEC = new KeyedCodec<>("Spell", CODEC);

    public static final String METADATA_KEY = "Spell";

    public SpellMetadata() {
        this.name = "Default Spell";
        this.description = "This is a default spell.";
        this.level = 1;
        this.castTime = 1.0;
        this.cooldown = 5.0;
        this.manaCost = 10.0;
    }

    public SpellMetadata(
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
