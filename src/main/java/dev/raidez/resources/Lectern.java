package dev.raidez.resources;

/**
 * Lectern component
 */
public class Lectern implements Component<ChunkStore> {

    private ItemStack grimoire;

    public static final BuilderCodec<Lectern> CODEC = BuilderCodec.builder(
            Lectern.class, Lectern::new
        )
        .append(new KeyedCodec<>("Grimoire", Codec.ITEM_STACK),
            (b, v) -> b.grimoire = v, b -> b.grimoire)
        .add()
        .build();

}