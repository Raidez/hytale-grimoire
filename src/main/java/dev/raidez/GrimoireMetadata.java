package dev.raidez;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GrimoireMetadata {

    private String[] spellList;
    private int spellSlot;

    public static final String KEY = "Grimoire";

    public static final BuilderCodec<GrimoireMetadata> CODEC = BuilderCodec
            .builder(GrimoireMetadata.class, GrimoireMetadata::new)
            .append(new KeyedCodec<>("SpellList", Codec.STRING_ARRAY), (c, v) -> c.spellList = v, c -> c.spellList)
            .add()
            .append(new KeyedCodec<>("SpellSlot", Codec.INTEGER), (c, v) -> c.spellSlot = v, c -> c.spellSlot)
            .add()
            .build();

    public static final KeyedCodec<GrimoireMetadata> KEYED_CODEC = new KeyedCodec<>(KEY, CODEC);

    public GrimoireMetadata() {
        this.spellList = new String[0];
        this.spellSlot = 0;
    }

    public void changeSpellSlot() {
        this.spellSlot = (this.spellSlot + 1) % this.spellList.length;
    }

    public String getCurrentSpell() {
        if (this.spellList.length == 0) {
            return null;
        }
        return this.spellList[this.spellSlot];
    }

    public void clearSpells() {
        this.spellList = new String[0];
        this.spellSlot = 0;
    }

    public void addSpell(String spell) {
        String[] newSpellList = new String[this.spellList.length + 1];
        System.arraycopy(this.spellList, 0, newSpellList, 0, this.spellList.length);
        newSpellList[this.spellList.length] = spell;
        this.spellList = newSpellList;
    }

    public void addSpells(String[] spells) {
        String[] newSpellList = new String[this.spellList.length + spells.length];
        System.arraycopy(this.spellList, 0, newSpellList, 0, this.spellList.length);
        System.arraycopy(spells, 0, newSpellList, this.spellList.length, spells.length);
        this.spellList = newSpellList;
    }

}
