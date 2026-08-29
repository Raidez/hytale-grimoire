package dev.raidez.resources;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GrimoireConfig {

    private boolean disableCommands = true;

    public static final BuilderCodec<GrimoireConfig> CODEC = BuilderCodec
            .builder(GrimoireConfig.class, GrimoireConfig::new)
            .append(new KeyedCodec<>("DisableCommands", Codec.BOOLEAN), (c, v) -> c.disableCommands = v, c -> c.disableCommands)
            .add()
            .build();

    public boolean isDisableCommands() {
        return disableCommands;
    }

}
