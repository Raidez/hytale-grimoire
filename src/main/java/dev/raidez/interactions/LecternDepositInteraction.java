package dev.raidez.interactions;

import org.joml.Vector3i;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.Lectern;

/**
 * Interaction for depositing a grimoire into a lectern.
 */
public class LecternDepositInteraction extends SimpleBlockInteraction {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public static final BuilderCodec<LecternDepositInteraction> CODEC = BuilderCodec
            .builder(LecternDepositInteraction.class, LecternDepositInteraction::new, SimpleBlockInteraction.CODEC)
            .build();

    @Override
    protected void interactWithBlock(
            World world,
            CommandBuffer<EntityStore> commandBuffer,
            InteractionType interactionType,
            InteractionContext context,
            ItemStack itemStack,
            Vector3i blockPos,
            CooldownHandler cooldownHandler) {

        var ref = context.getEntity();
        var store = context.getCommandBuffer().getStore();
        var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        var chunkStore = world.getChunkStore().getStore();

        // Check if the entity is holding a grimoire
        if (!Utils.isGrimoire(itemStack)) {
            LOGGER.atWarning().log("LecternDepositInteraction: Entity is not holding a grimoire!");
            return;
        }

        // Ensure that a lectern was targeted
        var blockType = (blockPos != null) ? world.getBlockType(blockPos) : null;
        if (!Utils.isLectern(blockType)) {
            LOGGER.atWarning()
                    .log("LecternDepositInteraction: The targeted block is not a lectern!");
            return;
        }

        // Get the block entity reference for the targeted lectern
        var blockRef = BlockModule.getBlockEntity(world, blockPos.x, blockPos.y, blockPos.z);
        if (blockRef == null) {
            LOGGER.atWarning().log("LecternDepositInteraction: Failed to get the lectern entity!");
            return;
        }

        // Change lectern state to hold the grimoire (visual)
        world.setBlockInteractionState(blockPos, blockType, "InfuseMode");

        // Add the lectern component to the chunk store
        var lectern = chunkStore.ensureAndGetComponent(blockRef, Lectern.getComponentType());
        lectern.deposit(itemStack);

        // Remove grimoire from the player's hand
        inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), itemStack, ItemStack.EMPTY);
        LOGGER.atInfo().log("LecternDepositInteraction: Grimoire deposited into lectern.");
    }

    @Override
    protected void simulateInteractWithBlock(
            InteractionType interactionType,
            InteractionContext context,
            ItemStack itemStack,
            World world,
            Vector3i blockPosition) {

        this.interactWithBlock(
                world,
                context.getCommandBuffer(),
                interactionType,
                context,
                itemStack,
                blockPosition,
                new CooldownHandler());
    }

}
