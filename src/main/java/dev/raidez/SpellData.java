package dev.raidez;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SpellData {

    private String name;
    private String description;
    private float manaCost;
    private float cooldown;
    private float castTime;
    private String interaction;
    private String texture;

    public static final BuilderCodec<SpellData> CODEC = BuilderCodec
            .builder(SpellData.class, SpellData::new)
            .append(new KeyedCodec<>("Name", Codec.STRING), SpellData::setName, SpellData::getName)
            .add()
            .append(new KeyedCodec<>("Description", Codec.STRING), SpellData::setDescription, SpellData::getDescription)
            .add()
            .append(new KeyedCodec<>("ManaCost", Codec.FLOAT), SpellData::setManaCost, SpellData::getManaCost)
            .add()
            .append(new KeyedCodec<>("Cooldown", Codec.FLOAT), SpellData::setCooldown, SpellData::getCooldown)
            .add()
            .append(new KeyedCodec<>("CastTime", Codec.FLOAT), SpellData::setCastTime, SpellData::getCastTime)
            .add()
            .append(new KeyedCodec<>("Interaction", Codec.STRING), SpellData::setInteraction, SpellData::getInteraction)
            .add()
            .append(new KeyedCodec<>("Texture", Codec.STRING), SpellData::setTexture, SpellData::getTexture)
            .add()
            .build();

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

    public float getManaCost() {
        return manaCost;
    }

    public void setManaCost(float manaCost) {
        this.manaCost = manaCost;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getCastTime() {
        return castTime;
    }

    public void setCastTime(float castTime) {
        this.castTime = castTime;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public static BuilderCodec<SpellData> getCodec() {
        return CODEC;
    }

}
