package dev.raidez.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.Spell;

public class CastInteraction extends SimpleInstantInteraction {

    private String spellId;

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public static final BuilderCodec<CastInteraction> CODEC = BuilderCodec
            .builder(CastInteraction.class, CastInteraction::new, SimpleInstantInteraction.CODEC)
            .append(new KeyedCodec<>("Spell", Codec.STRING), (c, v) -> c.spellId = v, c -> c.spellId)
            .addValidator(Spell.VALIDATOR_CACHE.getValidator())
            .add()
            .build();

    @Override
    protected void firstRun(
            InteractionType interactionType,
            InteractionContext context,
            CooldownHandler cooldownHandler) {

        // Check if the spellId is valid
        var spell = Spell.getAssetMap().getAsset(spellId);
        if (spell == null) {
            LOGGER.atWarning().log("CastInteraction: Spell not found: %s", spellId);
            return;
        }

        // Cast the spell
        Utils.executeInteraction(spell.getInteractionId(), context);
        LOGGER.atInfo().log("CastInteraction: Casting spell: %s", spellId);

    }

}
