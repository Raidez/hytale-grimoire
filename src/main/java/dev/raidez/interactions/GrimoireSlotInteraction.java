package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.Utils;
import dev.raidez.resources.GrimoireMetadata;

public class GrimoireSlotInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<GrimoireSlotInteraction> CODEC = BuilderCodec
            .builder(GrimoireSlotInteraction.class, GrimoireSlotInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType interactionType,
            InteractionContext context,
            CooldownHandler cooldownHandler) {

        var ref = context.getEntity();
        var store = context.getCommandBuffer().getStore();
        var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (!Utils.isGrimoire(is)) {
            return;
        }

        // Change the spell slot
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        grimoire.changeSpellSlot(1);

        // Update the item stack with the new metadata
        var newIs = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);
        inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);
    }
}
