package dev.raidez;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;

public class Spell implements JsonAssetWithMap<String, DefaultAssetMap<String, Spell>> {

    private String id;
    private AssetExtraInfo.Data data;

    private SpellData spellData;

    public static final AssetBuilderCodec<String, Spell> CODEC = AssetBuilderCodec
            .<String, Spell>builder(Spell.class, Spell::new, Codec.STRING,
                    (c, v) -> c.id = v, c -> c.id,
                    (c, v) -> c.data = v, c -> c.data)
            .append(new KeyedCodec<>("Spell", SpellData.CODEC), (c, v) -> c.spellData = v, c -> c.spellData)
            .add()
            .build();

    @Override
    public String getId() {
        return id;
    }
}
