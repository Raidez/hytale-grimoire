package dev.raidez;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import dev.raidez.commands.GrimoireCommand;
import dev.raidez.commands.SpellCommand;
import dev.raidez.handlers.GenerateSpellChainHandler;
import dev.raidez.interactions.CastInteraction;
import dev.raidez.interactions.GrimoireCastInteraction;
import dev.raidez.interactions.GrimoireSlotInteraction;
import dev.raidez.resources.GrimoireConfig;
import dev.raidez.resources.Spell;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    private final Config<GrimoireConfig> config;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        config = withConfig("config", GrimoireConfig.CODEC);
    }

    @Override
    protected void setup() {
        config.save();
        var isDisableCommands = config.get().isDisableCommands();

        // Register assets
        getAssetRegistry().register(
                HytaleAssetStore.builder(Spell.class, new DefaultAssetMap<>())
                        .setPath("Item/Spells")
                        .loadsAfter(Item.class)
                        .setCodec(Spell.CODEC)
                        .setKeyFunction(Spell::getId)
                        .build());

        // Register handlers
        getEventRegistry().register(LoadedAssetsEvent.class, Spell.class, GenerateSpellChainHandler::onSpellLoad);
        getEventRegistry().register(RemovedAssetsEvent.class, Spell.class, GenerateSpellChainHandler::onSpellRemove);

        // Register commands
        if (!isDisableCommands) {
            getCommandRegistry()
                    .registerCommand(new GrimoireCommand());
            getCommandRegistry()
                    .registerCommand(new SpellCommand());
        }

        // Register interactions
        getCodecRegistry(Interaction.CODEC).register("Cast",
                CastInteraction.class, CastInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("GrimoireSlot",
                GrimoireSlotInteraction.class, GrimoireSlotInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("GrimoireCast",
                GrimoireCastInteraction.class, GrimoireCastInteraction.CODEC);
    }

    public static GrimoirePlugin get() {
        return instance;
    }

}
