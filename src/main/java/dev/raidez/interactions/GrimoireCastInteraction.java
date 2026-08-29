package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.Utils;
import dev.raidez.resources.GrimoireMetadata;
import dev.raidez.resources.Spell;

public class GrimoireCastInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<GrimoireCastInteraction> CODEC = BuilderCodec
            .builder(GrimoireCastInteraction.class, GrimoireCastInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType interactionType,
            InteractionContext context,
            CooldownHandler cooldownHandler) {

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (!Utils.isGrimoire(is)) {
            return;
        }

        // Get the grimoire metadata and the current spell
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        var spellId = grimoire.getCurrentSpell();
        if (spellId == null) {
            return;
        }

        // Cast the spell
        var spell = Spell.getAssetMap().getAsset(spellId);
        Utils.executeInteraction(spell.getInteractionId(), context);

        /**
         * TODO: compose root interaction
         * - charging animation (castTime)
         * - set cooldown (cooldown)
         * - call cast interaction
         * - check mana condition (manaCost)
         * - change mana stat (manaCost)
         * - if failed, play failed interaction
         */
    }

}
