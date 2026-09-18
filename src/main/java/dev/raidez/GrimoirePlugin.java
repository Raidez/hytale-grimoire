package dev.raidez;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.raidez.commands.GrimoireCommand;
import dev.raidez.commands.SpellCommand;
import dev.raidez.handlers.LoadSpellHandler;
import dev.raidez.handlers.PickupGrimoireHandler;
import dev.raidez.handlers.PlaceLecternHandler;
import dev.raidez.interactions.CastInteraction;
import dev.raidez.interactions.GrimoireCastInteraction;
import dev.raidez.interactions.GrimoireSlotInteraction;
import dev.raidez.interactions.LecternHoldInteraction;
import dev.raidez.resources.Lectern;
import dev.raidez.resources.Spell;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        // Register assets
        getAssetRegistry().register(
                HytaleAssetStore.builder(Spell.class, new DefaultAssetMap<>())
                        .setPath("Item/Spells")
                        .loadsAfter(Item.class)
                        .setCodec(Spell.CODEC)
                        .setKeyFunction(Spell::getId)
                        .build());

        // Register handlers
        getEventRegistry().register(LoadedAssetsEvent.class, Spell.class, LoadSpellHandler::onSpellLoad);
        getEventRegistry().register(RemovedAssetsEvent.class, Spell.class, LoadSpellHandler::onSpellRemove);
        getEntityStoreRegistry().registerSystem(new PickupGrimoireHandler());

        // Register commands
        getCommandRegistry().registerCommand(new GrimoireCommand());
        getCommandRegistry().registerCommand(new SpellCommand());

        // Register interactions
        getCodecRegistry(Interaction.CODEC).register("Cast",
                CastInteraction.class, CastInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("GrimoireSlot",
                GrimoireSlotInteraction.class, GrimoireSlotInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("GrimoireCast",
                GrimoireCastInteraction.class, GrimoireCastInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("LecternHold",
                LecternHoldInteraction.class, LecternHoldInteraction.CODEC);

        // Register components
        getChunkStoreRegistry().registerComponent(Lectern.class, Lectern::new);
    }

    public static GrimoirePlugin get() {
        return instance;
    }

}
