package dev.raidez;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        // Register Grimoire command
        getCommandRegistry()
                .registerCommand(new GrimoireCommand());

        // Register Spell asset type
        getAssetRegistry().register(
                HytaleAssetStore.builder(Spell.class, new DefaultAssetMap<>())
                        .setPath("Item/Spells")
                        .loadsAfter(Item.class)
                        .setCodec(Spell.CODEC)
                        .setKeyFunction(Spell::getId)
                        .build());

        getCommandRegistry()
                .registerCommand(new SpellCommand());
    }

    public static GrimoirePlugin get() {
        return instance;
    }

}
