package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.GrimoirePlugin;

/**
 * Interaction for picking up the grimoire from a lectern.
 */
public class LecternPickupInteraction extends SimpleInstantInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public static final BuilderCodec<LecternPickupInteraction> CODEC = BuilderCodec
            .builder(LecternPickupInteraction.class, LecternPickupInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType interactionType,
            InteractionContext context,
            CooldownHandler cooldownHandler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'firstRun'");
    }
}
