package dev.raidez.metadata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Represents a spell in the game with various attributes such as name,
 * description, level, cast time, cooldown, and mana cost.
 * 
 * Spell.builder().name("MySpell").level(1).build();
 */
public class Spell {

    private String name;
    private String description;
    private int level;
    private double castTime;
    private double cooldown;
    private double manaCost;

    public static final BuilderCodec<Spell> CODEC = BuilderCodec
            .builder(Spell.class, Spell::new)
            .append(new KeyedCodec<>("Name", Codec.STRING), (c, v) -> c.name = v, (c) -> c.name).add()
            .append(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, (c) -> c.description).add()
            .append(new KeyedCodec<>("Level", Codec.INTEGER), (c, v) -> c.level = v, (c) -> c.level).add()
            .append(new KeyedCodec<>("CastTime", Codec.DOUBLE), (c, v) -> c.castTime = v, (c) -> c.castTime).add()
            .append(new KeyedCodec<>("Cooldown", Codec.DOUBLE), (c, v) -> c.cooldown = v, (c) -> c.cooldown).add()
            .append(new KeyedCodec<>("ManaCost", Codec.DOUBLE), (c, v) -> c.manaCost = v, (c) -> c.manaCost).add()
            .build();

    public static final KeyedCodec<Spell> KEYED_CODEC = new KeyedCodec<>("Spell", CODEC);

    public static final String METADATA_KEY = "Spell";

    protected Spell() {
        this.name = "";
        this.description = "";
        this.level = 1;
        this.castTime = 0.0;
        this.cooldown = 0.0;
        this.manaCost = 0.0;
    }

    /**
     * Creates a new instance of the Spell class using the provided builder.
     * 
     * @return a new instance of the Spell class
     */
    public static Builder builder() {
        return new BuilderImpl();
    }

    /**
     * Creates a new instance of the Spell class using the provided builder and
     * copies the values from the given Spell instance.
     * 
     * @param copy the Spell instance to copy values from
     * @return a new Builder instance initialized with the values from the given
     *         Spell instance
     */
    public static Builder builder(Spell copy) {
        return new BuilderImpl(copy);
    }

    /**
     * Private constructor for the Spell class.
     * Use the builder to create instances of Spell.
     * 
     * @param builder
     */
    private Spell(BuilderImpl builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.level = builder.level;
        this.castTime = builder.castTime;
        this.cooldown = builder.cooldown;
        this.manaCost = builder.manaCost;
    }

    /**
     * Builder interface for creating instances of the Spell class.
     */
    public static interface Builder {

        Builder name(String name);

        Builder description(String description);

        Builder level(int level);

        Builder castTime(double castTime);

        Builder cooldown(double cooldown);

        Builder manaCost(double manaCost);

        Spell build();
    }

    /**
     * Private builder implementation for creating instances of the Spell class.
     */
    private static class BuilderImpl implements Builder {

        private String name;
        private String description;
        private int level;
        private double castTime;
        private double cooldown;
        private double manaCost;

        private BuilderImpl() {
            this.name = "";
            this.description = "";
            this.level = 1;
            this.castTime = 0.0;
            this.cooldown = 0.0;
            this.manaCost = 0.0;
        }

        private BuilderImpl(Spell copy) {
            this.name = copy.name;
            this.description = copy.description;
            this.level = copy.level;
            this.castTime = copy.castTime;
            this.cooldown = copy.cooldown;
            this.manaCost = copy.manaCost;
        }

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public Builder castTime(double castTime) {
            this.castTime = castTime;
            return this;
        }

        @Override
        public Builder cooldown(double cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        @Override
        public Builder manaCost(double manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        @Override
        public Spell build() {
            return new Spell(this);
        }
    }

    /******************************** ACCESSORS ********************************/

    public String getName() {
        return name;
    }

    public Spell withName(String name) {
        return Spell.builder(this).name(name).build();
    }

    public String getDescription() {
        return description;
    }

    public Spell withDescription(String description) {
        return Spell.builder(this).description(description).build();
    }

    public int getLevel() {
        return level;
    }

    public Spell withLevel(int level) {
        return Spell.builder(this).level(level).build();
    }

    public double getCastTime() {
        return castTime;
    }

    public Spell withCastTime(double castTime) {
        return Spell.builder(this).castTime(castTime).build();
    }

    public double getCooldown() {
        return cooldown;
    }

    public Spell withCooldown(double cooldown) {
        return Spell.builder(this).cooldown(cooldown).build();
    }

    public double getManaCost() {
        return manaCost;
    }

    public Spell withManaCost(double manaCost) {
        return Spell.builder(this).manaCost(manaCost).build();
    }

}
