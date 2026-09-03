package dev.raidez.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bson.Document;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;

import dev.raidez.GrimoirePlugin;
import dev.raidez.resources.Spell;

public class GenerateSpellChainHandler {

    private static final String CAST_CHAIN_TEMPLATE = readResource("/spell_cast_chain.json");

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    private static String readResource(String path) {
        try (InputStream stream = GrimoirePlugin.class.getResourceAsStream(path)) {
            if (stream == null)
                throw new IllegalStateException("Missing resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Handles the event when spells assets are loaded and generates their cast
     * interactions.
     * 
     * @param event
     */
    public static void onSpellLoad(LoadedAssetsEvent<String, Spell, DefaultAssetMap<String, Spell>> event) {

        var store = RootInteraction.getAssetStore();
        var chains = new ArrayList<RootInteraction>(event.getLoadedAssets().size());

        for (var spell : event.getLoadedAssets().values()) {
            var interactionId = Spell.getCastInteractionId(spell.getId());
            LOGGER.atInfo().log("GenerateSpellChainHandler: Building cast interaction '%s'", interactionId);

            // Decode document into a RootInteraction instance
            var document = buildCastInteractionDocument(spell).toBsonDocument();
            var interaction = store.decode(DefaultAssetMap.DEFAULT_PACK_KEY, interactionId, document);

            // Add interaction to the chains
            chains.add(interaction);
        }

        // Load all generated cast interactions into the asset store
        if (!chains.isEmpty()) {
            var result = store.loadAssets(DefaultAssetMap.DEFAULT_PACK_KEY, chains);
            LOGGER.atInfo().log("GenerateSpellChainHandler: Loaded %d cast interaction(s), %d failed: %s",
                    result.getLoadedAssets().size(), result.getFailedToLoadKeys().size(),
                    result.getFailedToLoadKeys());
        }
    }

    /**
     * Handles the event when spells assets are removed and removes their
     * corresponding cast interactions.
     * 
     * @param event
     */
    public static void onSpellRemove(RemovedAssetsEvent<String, Spell, DefaultAssetMap<String, Spell>> event) {
        var chainIds = event.getRemovedAssets().stream().map(Spell::getCastInteractionId).toList();
        var removed = RootInteraction.getAssetStore().removeAssets(chainIds);
        LOGGER.atInfo().log("GenerateSpellChainHandler: Removed %d/%d cast interaction(s) for removed spell(s): %s",
                removed.size(), chainIds.size(), chainIds);
    }

    /**
     * Builds RootInteraction document for a spell's cast interaction.
     * 
     * @param spell
     */
    private static Document buildCastInteractionDocument(Spell spell) {
        var json = CAST_CHAIN_TEMPLATE.formatted(
                spell.getCastTime(),
                spell.getManaCost(),
                -spell.getManaCost(),
                spell.getInteractionId(),
                spell.getId(),
                spell.getCooldown());

        return Document.parse(json);
    }

}
