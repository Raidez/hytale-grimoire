package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;

public class LecternHoldInteraction extends SimpleInstantInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public static final BuilderCodec<LecternHoldInteraction> CODEC = BuilderCodec
            .builder(LecternHoldInteraction.class, LecternHoldInteraction::new, SimpleInstantInteraction.CODEC)
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
            LOGGER.atWarning().log("LecternHoldInteraction: Entity does not have an inventory");
            return;
        }

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (is == null || !Utils.isGrimoire(is)) {
            LOGGER.atWarning().log("LecternHoldInteraction: Entity is not holding a grimoire");
            return;
        }

        // Change lectern state to hold the grimoire

        // Add lectern component on the bench

        // Remove grimoire from the player's hand
        inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, ItemStack.EMPTY);
    }
}
