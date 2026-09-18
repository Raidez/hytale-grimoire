package dev.raidez.resources;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

/**
 * Lectern component
 */
public class Lectern implements Component<ChunkStore> {

    private ItemStack grimoire;

    public static final BuilderCodec<Lectern> CODEC = BuilderCodec.builder(
            Lectern.class, Lectern::new)
            .append(new KeyedCodec<>("Grimoire", ItemStack.CODEC),
                    (b, v) -> b.grimoire = v, b -> b.grimoire)
            .add()
            .build();

    @Override
    public Lectern clone() {
        Lectern copy = new Lectern();
        copy.grimoire = this.grimoire;
        return copy;
    }

}
