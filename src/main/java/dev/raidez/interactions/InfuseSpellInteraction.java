package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemStackContainerWindow;
import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.metadata.GrimoireMetadata;

public class InfuseSpellInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<InfuseSpellInteraction> CODEC = BuilderCodec
            .builder(InfuseSpellInteraction.class, InfuseSpellInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType type,
            InteractionContext context,
            CooldownHandler cooldown) {

        var ref = context.getEntity();
        var store = context.getCommandBuffer().getStore();
        var player = store.getComponent(ref, Player.getComponentType());
        var pageManager = player.getPageManager();

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (is == null || !is.getItemId().equals("Weapon_Grimoire"))
            return;

        // Get the grimoire metadata
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.METADATA_KEY, GrimoireMetadata.CODEC);
        if (meta == null || meta.getSpellList().length == 0)
            return;

        // Get the container of the grimoire (@see OpenItemStackContainerInteraction)
        var container = ItemStackItemContainer
                .ensureConfiguredContainer(
                        context.getHeldItemContainer(),
                        context.getHeldItemSlot(),
                        is.getItem().getItemStackContainerConfig());

        // Open the grimoire's container
        pageManager.setPageWithWindows(ref, store, Page.Bench, true,
                new ItemStackContainerWindow(container));

        // context.execute(RootInteraction.getRootInteractionOrUnknown("Grimoire_Open_Container"));
    }

}
