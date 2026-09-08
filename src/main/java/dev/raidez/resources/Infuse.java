package dev.raidez.resources;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;

public class Infuse {

    public enum Action {
        Cancel, // Cancel button, close the UI
        Infuse, // Validate infuse, close the UI, update grimoire metadata
        Picker, // Open the item picker with available scrolls
        Slot,   // Update a specific slot with the selected item
    }

    private Action action;
    private int slot;
    private String itemId;

    public static final BuilderCodec<Infuse> CODEC = BuilderCodec
            .builder(Infuse.class, Infuse::new)
            .append(new KeyedCodec<>("Action", new EnumCodec<>(Action.class)), Infuse::setAction, Infuse::getAction)
            .add()
            .append(new KeyedCodec<>("Slot", Codec.STRING), (c, v) -> c.setSlot(Integer.parseInt(v)),
                    c -> String.valueOf(c.getSlot()))
            .add()
            .append(new KeyedCodec<>("ItemId", Codec.STRING), Infuse::setItemId, Infuse::getItemId)
            .add()
            .build();

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
