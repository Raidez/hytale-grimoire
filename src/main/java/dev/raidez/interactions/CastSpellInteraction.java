package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import dev.raidez.metadata.GrimoireMetadata;
import dev.raidez.metadata.SpellMetadata;

public class CastSpellInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<CastSpellInteraction> CODEC = BuilderCodec
            .builder(CastSpellInteraction.class, CastSpellInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType type,
            InteractionContext context,
            CooldownHandler cooldown) {

        var ref = context.getEntity();
        var store = context.getCommandBuffer().getStore();
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (is == null || !is.getItemId().equals("Weapon_Grimoire"))
            return;

        // Get the grimoire metadata
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.METADATA_KEY, GrimoireMetadata.CODEC);
        if (meta == null || meta.getSpellList().length == 0)
            return;

        // Get the spell to cast
        if (meta.getSpellIndex() < 0 || meta.getSpellIndex() >= meta.getSpellList().length)
            return;

        var spellId = meta.getSpellList()[meta.getSpellIndex()];
        var spell = SpellMetadata.spellMap.get(spellId);
        if (spell == null)
            return;

        // Cast the spell interaction
        var interaction = spell.getInteraction();
        if (interaction == null || interaction.isEmpty())
            return;

        // Send a message to the player about the spell being cast
        var spellName = Message.translation(spell.getName());
        playerRef.sendMessage(Message.join(Message.raw("Casting spell: "), spellName));
        context.getState().state = InteractionState.Finished;
        context.execute(RootInteraction.getRootInteractionOrUnknown(interaction));
    }

}
