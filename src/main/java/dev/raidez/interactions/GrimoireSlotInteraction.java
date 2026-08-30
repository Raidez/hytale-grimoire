package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.GrimoireMetadata;

public class GrimoireSlotInteraction extends SimpleInstantInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

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

        // Check if the player has an inventory
        if (inventory == null) {
            LOGGER.atWarning().log("GrimoireSlotInteraction: Entity does not have an inventory");
            return;
        }

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (!Utils.isGrimoire(is)) {
            LOGGER.atWarning().log("GrimoireSlotInteraction: Entity is not holding a grimoire");
            return;
        }

        // Change the spell slot
        var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY, GrimoireMetadata.CODEC);
        grimoire.changeSpellSlot(1);

        // Update the item stack with the new metadata
        var newIs = is.withMetadata(GrimoireMetadata.KEYED_CODEC, grimoire);
        inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, newIs);
        LOGGER.atInfo().log("GrimoireSlotInteraction: Changed spell slot");
    }
}
