package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.metadata.GrimoireMetadata;

public class ChangeSpellInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<ChangeSpellInteraction> CODEC = BuilderCodec
            .builder(ChangeSpellInteraction.class, ChangeSpellInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType type,
            InteractionContext context,
            CooldownHandler cooldown) {

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (is == null || !is.getItemId().equals("Weapon_Grimoire"))
            return;

        // Get the grimoire metadata
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.METADATA_KEY, GrimoireMetadata.CODEC);
        if (meta == null || meta.getSpellList().length == 0)
            return;

        // Update the spell index (wrap around)
        meta.setSpellIndex((short) ((meta.getSpellIndex() + 1) % meta.getSpellList().length));

        System.out.println("ChangeSpellInteraction: Changed spell index to " + meta.getSpellIndex());
    }

}
