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
public class SpellMetadata {

    private String name;
    private String description;
    private int level;
    private double castTime;
    private double cooldown;
    private double manaCost;
    private String interaction;

    /**** CODEC ****/

    public static final BuilderCodec<SpellMetadata> CODEC = BuilderCodec
            .builder(SpellMetadata.class, SpellMetadata::new)
            .append(new KeyedCodec<>("Name", Codec.STRING), (c, v) -> c.name = v, (c) -> c.name).add()
            .append(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, (c) -> c.description).add()
            .append(new KeyedCodec<>("Level", Codec.INTEGER), (c, v) -> c.level = v, (c) -> c.level).add()
            .append(new KeyedCodec<>("CastTime", Codec.DOUBLE), (c, v) -> c.castTime = v, (c) -> c.castTime).add()
            .append(new KeyedCodec<>("Cooldown", Codec.DOUBLE), (c, v) -> c.cooldown = v, (c) -> c.cooldown).add()
            .append(new KeyedCodec<>("ManaCost", Codec.DOUBLE), (c, v) -> c.manaCost = v, (c) -> c.manaCost).add()
            .append(new KeyedCodec<>("Interaction", Codec.STRING), (c, v) -> c.interaction = v, (c) -> c.interaction).add()
            .build();

    public static final KeyedCodec<SpellMetadata> KEYED_CODEC = new KeyedCodec<>(SpellMetadata.METADATA_KEY, CODEC);

    public static final String METADATA_KEY = "Spell";

    private SpellMetadata() {
        this.name = "";
        this.description = "";
        this.level = 1;
        this.castTime = 0.0;
        this.cooldown = 0.0;
        this.manaCost = 0.0;
        this.interaction = "";
    }

    /**** BUILDER ****/

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
    public static Builder builder(SpellMetadata copy) {
        return new BuilderImpl(copy);
    }

    /**
     * Private constructor for the Spell class.
     * Use the builder to create instances of Spell.
     * 
     * @param builder
     */
    private SpellMetadata(BuilderImpl builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.level = builder.level;
        this.castTime = builder.castTime;
        this.cooldown = builder.cooldown;
        this.manaCost = builder.manaCost;
        this.interaction = builder.interaction;
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

        Builder interaction(String interaction);

        SpellMetadata build();
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
        private String interaction;

        private BuilderImpl() {
            this.name = "";
            this.description = "";
            this.level = 1;
            this.castTime = 0.0;
            this.cooldown = 0.0;
            this.manaCost = 0.0;
            this.interaction = "";
        }

        private BuilderImpl(SpellMetadata copy) {
            this.name = copy.name;
            this.description = copy.description;
            this.level = copy.level;
            this.castTime = copy.castTime;
            this.cooldown = copy.cooldown;
            this.manaCost = copy.manaCost;
            this.interaction = copy.interaction;
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
        public Builder interaction(String interaction) {
            this.interaction = interaction;
            return this;
        }

        @Override
        public SpellMetadata build() {
            return new SpellMetadata(this);
        }
    }

    /**** ACCESSORS ****/

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

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

}
