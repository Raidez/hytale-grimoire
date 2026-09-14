package dev.raidez.resources;

import java.util.Arrays;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Grimoire metadata
 */
public class Grimoire {

    private String[] spellList;
    private int spellSlot;

    public static final int SLOT_COUNT = 12;
    public static final String KEY = "Grimoire";

    public static final BuilderCodec<Grimoire> CODEC = BuilderCodec
            .builder(Grimoire.class, Grimoire::new)
            .append(new KeyedCodec<>("SpellList", Codec.STRING_ARRAY),
                    (c, v) -> c.spellList = toFixedSize(v),
                    c -> c.spellList)
            .add()
            .append(new KeyedCodec<>("SpellSlot", Codec.INTEGER), (c, v) -> c.spellSlot = v, c -> c.spellSlot)
            .add()
            .build();

    public static final KeyedCodec<Grimoire> KEYED_CODEC = new KeyedCodec<>(KEY, CODEC);

    private static String[] toFixedSize(String[] source) {
        var fixed = new String[SLOT_COUNT];
        if (source != null) {
            System.arraycopy(source, 0, fixed, 0, Math.min(source.length, SLOT_COUNT));
        }
        return fixed;
    }

    public Grimoire() {
        this.spellList = new String[SLOT_COUNT];
        this.spellSlot = 0;
    }

    public void changeSpellSlot(int slot) {
        this.spellSlot = (slot + this.spellList.length) % this.spellList.length;
    }

    public String[] getSpellList() {
        return this.spellList;
    }

    public String[] getScrollList() {
        var assetMap = Spell.getAssetMap();
        return Arrays.stream(this.spellList)
                .map(spell -> spell == null ? null : assetMap.getAsset(spell).getItemId())
                .toArray(String[]::new);
    }

    public int getCurrentSlot() {
        return this.spellSlot;
    }

    public String getCurrentSpell() {
        return this.spellList[spellSlot];
    }

    public void clearSpells() {
        this.spellList = new String[SLOT_COUNT];
        this.spellSlot = 0;
    }

    public void addSpell(String spell) {
        this.spellList[this.spellSlot] = spell;
    }

    public void addSpell(int slot, String spell) {
        this.spellList[slot] = spell;
    }

    public void addSpells(String... spells) {
        for (int i = 0; i < spells.length && i < this.spellList.length; i++) {
            this.spellList[i] = spells[i];
        }
    }

    public void removeSpell(int slot) {
        this.spellList[slot] = null;
    }

    public void removeSpell(String spell) {
        for (int i = 0; i < this.spellList.length; i++) {
            if (spell.equals(this.spellList[i])) {
                this.spellList[i] = null;
                break;
            }
        }
    }

}
