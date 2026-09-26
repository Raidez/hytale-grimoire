package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import dev.raidez.GrimoirePlugin;
import dev.raidez.pages.InfusePage;

/**
 * Interaction for infusing the grimoire inside a lectern.
 */
public class LecternInfuseInteraction extends SimpleInstantInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public static final BuilderCodec<LecternInfuseInteraction> CODEC = BuilderCodec
            .builder(LecternInfuseInteraction.class, LecternInfuseInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType interactionType,
            InteractionContext context,
            CooldownHandler cooldownHandler) {

        var ref = context.getEntity();
        var store = ref.getStore();
        var player = store.getComponent(ref, Player.getComponentType());
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        // Open the infuse page
        var page = new InfusePage(playerRef);
        player.getPageManager().openCustomPage(ref, store, page);
    }

}
