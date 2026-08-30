package dev.raidez.interactions;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
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
        // Utils.executeInteraction(spell.getInteractionId(), context);
        // LOGGER.atInfo().log("GrimoireCastInteraction: Casting spell: %s", spellId);

        var codec = RootInteraction.CODEC;

        // var bdoc = generateBdoc(spell);
        // var interaction = codec.decode(bdoc, ExtraInfo.THREAD_LOCAL.get());
        // LOGGER.atInfo().log("GrimoireCastInteraction: Composed interaction: %s",
        // interaction);

        var bdoc2 = generateBdoc2().toBsonDocument();
        var interaction2 = codec.decode(bdoc2, ExtraInfo.THREAD_LOCAL.get());
        LOGGER.atInfo().log("GrimoireCastInteraction: Composed interaction: %s", interaction2);

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

    private Document generateBdoc2() {
        return Document.parse("""
                {
                    "Interactions": [
                        {
                            "Type": "Charging",
                            "Effects": {
                                "WorldSoundEventId": "SFX_Skeleton_Mage_Spellbook_Charge",
                                "ItemAnimationId": "CastHurlCharging",
                                "ClearAnimationOnFinish": true,
                                "ClearSoundEventOnFinish": true
                            },
                            "AllowIndefiniteHold": true,
                            "Next": {
                                "1": {
                                    "$Comment": "Check mana cost",
                                    "Type": "StatsCondition",
                                    "RunTime": 0.167,
                                    "Costs": {
                                        "Mana": 20
                                    },
                                    "Next": {
                                        "Type": "Parallel",
                                        "Interactions": [
                                            {
                                                "$Comment": "Change mana stat",
                                                "Interactions": [
                                                    {
                                                        "Type": "ChangeStat",
                                                        "StatModifiers": {
                                                            "Mana": -20
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                "$Comment": "Cast magic spell",
                                                "Type": "Cast",
                                                "Spell": "Spell_Fireball"
                                            }
                                        ]
                                    },
                                    "Failed": "Spell_Failed"
                                }
                            }
                        }
                    ]
                }
                """);
    }

    private BsonDocument generateBdoc(Spell spell) {
        LOGGER.atInfo().log("GrimoireCastInteraction: Try constructing interaction for spell: %s", spell.getId());
        var spellEffect = new BsonDocument();
        spellEffect.put("Type", new BsonString("Cast"));
        spellEffect.put("Spell", new BsonString(spell.getInteractionId()));

        var manaChange = new BsonDocument();
        manaChange.put("Type", new BsonString("ChangeStat"));
        manaChange.put("StatModifiers",
                new BsonDocument().append("Mana", new BsonString(String.valueOf(-spell.getManaCost()))));

        var chains = new BsonArray();
        chains.add(manaChange);
        chains.add(spellEffect);

        var manaCheck = new BsonDocument();
        manaCheck.put("Type", new BsonString("StatsCondition"));
        manaCheck.put("Costs", new BsonDocument().append("Mana", new BsonString(String.valueOf(spell.getManaCost()))));
        manaCheck.put("Next", new BsonDocument().append("Type", new BsonString("Parallel"))
                .append("Interactions", chains));
        manaCheck.put("Failed", new BsonString("Spell_Failed"));

        var charging = new BsonDocument();
        charging.put("Type", new BsonString("Charging"));
        charging.put("Effects", new BsonDocument()
                .append("WorldSoundEventId", new BsonString("SFX_Skeleton_Mage_Spellbook_Charge"))
                .append("ItemAnimationId", new BsonString("CastHurlCharging"))
                .append("ClearAnimationOnFinish", new BsonString("true"))
                .append("ClearSoundEventOnFinish", new BsonString("true")));
        charging.put("AllowIndefiniteHold", new BsonString("true"));
        charging.put("Next", new BsonDocument().append(String.valueOf(spell.getCastTime()), manaCheck));

        var root = new BsonArray();
        root.add(charging);

        var cooldown = new BsonDocument();
        cooldown.put("Cooldown", new BsonString(String.valueOf(spell.getCooldown())));

        var bdoc = new BsonDocument();
        bdoc.put("Interactions", root);
        bdoc.put("Cooldown", cooldown);

        var json = bdoc.toJson();
        LOGGER.atInfo().log("GrimoireCastInteraction: Constructed interaction JSON: %s", json);

        return bdoc;
    }

}
