package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.GrimoireMetadata;
import dev.raidez.resources.Spell;

public class GrimoireCastInteraction extends SimpleInstantInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

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
            LOGGER.atWarning().log("GrimoireCastInteraction: Entity is not holding a grimoire");
            return;
        }

        // Get the grimoire metadata and the current spell
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        var spellId = grimoire.getCurrentSpell();
        if (spellId == null) {
            LOGGER.atWarning().log("GrimoireCastInteraction: No spell selected in the grimoire");
            return;
        }

        // Check if the spell exists
        var spell = Spell.getAssetMap().getAsset(spellId);
        if (spell == null) {
            LOGGER.atWarning().log("GrimoireCastInteraction: Spell not found: %s", spellId);
            return;
        }

        // Cast the spell
        Utils.executeInteraction(spell.getInteractionId(), context);
        LOGGER.atInfo().log("GrimoireCastInteraction: Casting spell: %s", spellId);

        // var i = Interaction.getAssetMap().getAsset("Charging");

        // var rootInteraction = new RootInteraction();

        // var sound =
        // SoundEvent.getAssetMap().getAsset("SFX_Skeleton_Mage_Spellbook_Charge");

        // var effects = new InteractionEffects(null, null, );

        // var charging = new ChargingInteraction(
        // WaitForDataFrom.Client, effects, 0f, 0f, true, null, null, new int[0], null,
        // 0, true, true, true, true,
        // 0f, 0f, null, null, null);

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
