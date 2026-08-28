package dev.raidez;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;

public class Spell implements JsonAssetWithMap<String, DefaultAssetMap<String, Spell>> {

    public static final SingleArgumentType<Spell> SPELL_ASSET = new AssetArgumentType<>("", Spell.class, "");

    private static AssetStore<String, Spell, DefaultAssetMap<String, Spell>> ASSET_STORE;

    public static final CommonAssetValidator ICON_SPELL = new CommonAssetValidator("png", "Icons/Spells");

    private String id;
    private AssetExtraInfo.Data data;

    private String itemId;
    private String name;
    private String description;
    private float manaCost;
    private float cooldown;
    private float castTime;
    private String interactionId;
    private String texture;

    public static final AssetBuilderCodec<String, Spell> CODEC = AssetBuilderCodec
            .builder(Spell.class, Spell::new, Codec.STRING,
                    (c, v) -> c.id = v, c -> c.id,
                    (c, v) -> c.data = v, c -> c.data)
            .append(new KeyedCodec<>("Item", Codec.STRING), Spell::setItemId, Spell::getItemId)
            .addValidator(Item.VALIDATOR_CACHE.getValidator())
            .add()
            .append(new KeyedCodec<>("Name", Codec.STRING), Spell::setName, Spell::getName)
            .add()
            .append(new KeyedCodec<>("Description", Codec.STRING), Spell::setDescription, Spell::getDescription)
            .add()
            .append(new KeyedCodec<>("ManaCost", Codec.FLOAT), Spell::setManaCost, Spell::getManaCost)
            .add()
            .append(new KeyedCodec<>("Cooldown", Codec.FLOAT), Spell::setCooldown, Spell::getCooldown)
            .add()
            .append(new KeyedCodec<>("CastTime", Codec.FLOAT), Spell::setCastTime, Spell::getCastTime)
            .add()
            .append(new KeyedCodec<>("Interaction", Codec.STRING), Spell::setInteractionId, Spell::getInteractionId)
            .addValidator(RootInteraction.VALIDATOR_CACHE.getValidator())
            .add()
            .append(new KeyedCodec<>("Texture", Codec.STRING), Spell::setTexture, Spell::getTexture)
            .addValidator(ICON_SPELL)
            .add()
            .build();

    public static AssetStore<String, Spell, DefaultAssetMap<String, Spell>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Spell.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, Spell> getAssetMap() {
        return (DefaultAssetMap<String, Spell>) getAssetStore().getAssetMap();
    }

    public static Spell getFromItem(Item item) {
        for (Spell spell : Spell.getAssetMap().getAssetMap().values()) {
            if (spell.getItemId().equals(item.getId())) {
                return spell;
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    public Item getItem() {
        return Item.getAssetMap().getAsset(itemId);
    }

    public Interaction getInteraction() {
        return Interaction.getAssetMap().getAsset(interactionId);
    }

    // #region Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
    // #endregion

}
