package dev.raidez.metadata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GrimoireMetadata {

    private String[] spellList;
    private short spellIndex;

    public static final BuilderCodec<GrimoireMetadata> CODEC = BuilderCodec
            .builder(GrimoireMetadata.class, GrimoireMetadata::new)
            .append(new KeyedCodec<>("SpellList", Codec.STRING_ARRAY), (c, v) -> c.spellList = v, (c) -> c.spellList)
            .add()
            .append(new KeyedCodec<>("SpellIndex", Codec.SHORT), (c, v) -> c.spellIndex = v, (c) -> c.spellIndex)
            .add()
            .build();

    public static final KeyedCodec<GrimoireMetadata> KEYED_CODEC = new KeyedCodec<>(GrimoireMetadata.METADATA_KEY,
            CODEC);

    public static final String METADATA_KEY = "Grimoire";

    public String[] getSpellList() {
        return spellList;
    }

    public void setSpellList(String[] spellList) {
        this.spellList = spellList;
    }

    public short getSpellIndex() {
        return spellIndex;
    }

    public void setSpellIndex(short spellIndex) {
        this.spellIndex = spellIndex;
    }

}
