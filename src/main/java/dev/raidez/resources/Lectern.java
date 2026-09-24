package dev.raidez.resources;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import dev.raidez.GrimoirePlugin;

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

    public static ComponentType<ChunkStore, Lectern> getComponentType() {
        return GrimoirePlugin.get().getLecternComponentType();
    }

    @Override
    public Lectern clone() {
        Lectern copy = new Lectern();
        copy.grimoire = this.grimoire;
        return copy;
    }

    public void deposit(ItemStack grimoire) {
        this.grimoire = grimoire;
    }

    public ItemStack pickup() {
        ItemStack temp = this.grimoire;
        this.grimoire = null;
        return temp;
    }

}
