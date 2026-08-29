package dev.raidez.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GrimoireMetadata {

    private List<String> spellList;
    private int spellSlot;

    public static final String KEY = "Grimoire";

    public static final BuilderCodec<GrimoireMetadata> CODEC = BuilderCodec
            .builder(GrimoireMetadata.class, GrimoireMetadata::new)
            .append(new KeyedCodec<>("SpellList", Codec.STRING_ARRAY),
                    (c, v) -> c.spellList = new ArrayList<>(Arrays.asList(v)),
                    c -> c.spellList.toArray(new String[0]))
            .add()
            .append(new KeyedCodec<>("SpellSlot", Codec.INTEGER), (c, v) -> c.spellSlot = v, c -> c.spellSlot)
            .add()
            .build();

    public static final KeyedCodec<GrimoireMetadata> KEYED_CODEC = new KeyedCodec<>(KEY, CODEC);

    public GrimoireMetadata() {
        this.spellList = new ArrayList<>();
        this.spellSlot = 0;
    }

    public void changeSpellSlot() {
        changeSpellSlot(1);
    }

    public void changeSpellSlot(int delta) {
        if (this.spellList.isEmpty()) {
            return;
        }
        this.spellSlot = (this.spellSlot + delta + this.spellList.size()) % this.spellList.size();
    }

    public List<String> getSpellList() {
        return List.copyOf(this.spellList);
    }

    public String getCurrentSpell() {
        if (this.spellList.isEmpty()) {
            return null;
        }
        return this.spellList.get(spellSlot);
    }

    public void clearSpells() {
        this.spellList = new ArrayList<>();
        this.spellSlot = 0;
    }

    public void addSpell(String spell) {
        this.spellList.add(spell);
    }

    public void addSpells(String... spells) {
        this.spellList.addAll(List.of(spells));
    }

    public void removeSpell(String spell) {
        if (!this.spellList.remove(spell)) {
            return;
        }

        if (this.spellSlot >= this.spellList.size()) {
            this.spellSlot = Math.max(0, this.spellList.size() - 1);
        }
    }

}
