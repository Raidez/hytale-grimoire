package dev.raidez.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;

import dev.raidez.Utils;
import dev.raidez.pages.InfusePage;
import dev.raidez.resources.Lectern;

public class LecternCommand extends AbstractCommandCollection {

    public LecternCommand() {
        super("lectern", "Lectern command");

        addSubCommand(new UICommand());
        addSubCommand(new DepositCommand());
        addSubCommand(new PickupCommand());
    }

    class UICommand extends AbstractPlayerCommand {

        public UICommand() {
            super("ui", "Open the infuse UI page");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            // Open the infuse UI page
            var player = store.getComponent(ref, Player.getComponentType());
            var page = new InfusePage(playerRef);
            player.getPageManager().openCustomPage(ref, store, page);

        }

    }

    class DepositCommand extends AbstractPlayerCommand {

        public DepositCommand() {
            super("deposit", "Deposit the grimoire on the targeted lectern");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var chunkStore = world.getChunkStore().getStore();

            // Check if the player is holding a grimoire
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
            var is = inventory.getActiveItem();
            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("You must hold a grimoire to change the slot!"));
                return;
            }

            // Get the targeted block position
            var blockPos = TargetUtil.getTargetBlock(ref, 8.0, store);
            if (blockPos == null) {
                commandContext.sendMessage(Message.raw("You must target a block!"));
                return;
            }

            // Ensure that a lectern was targeted
            var blockType = world.getBlockType(blockPos);
            if (!Utils.isLectern(blockType)) {
                commandContext.sendMessage(Message.raw("You must target a lectern to deposit the grimoire!"));
                return;
            }

            // world.execute(() -> {
            // Get the block entity reference for the targeted lectern
            var blockRef = BlockModule.getBlockEntity(world, blockPos.x, blockPos.y, blockPos.z);
            if (blockRef == null) {
                commandContext.sendMessage(Message.raw("Failed to get the lectern entity!"));
                return;
            }

            // Change lectern state to hold the grimoire (visual)
            world.setBlockInteractionState(blockPos, blockType, "InfuseMode");

            // Add the lectern component to the chunk store
            var lectern = chunkStore.ensureAndGetComponent(blockRef, Lectern.getComponentType());
            lectern.deposit(is);

            // Remove grimoire from the player's hand
            inventory.getInventory().replaceItemStackInSlot(inventory.getActiveSlot(), is, ItemStack.EMPTY);
            // });

        }

    }

    class PickupCommand extends AbstractPlayerCommand {

        public PickupCommand() {
            super("pickup", "Pick up the grimoire from the targeted lectern");
        }

        @Override
        protected void execute(
                CommandContext commandContext,
                Store<EntityStore> store,
                Ref<EntityStore> ref,
                PlayerRef playerRef,
                World world) {

            var chunkStore = world.getChunkStore().getStore();
            var inventory = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());

            // Get the targeted block position
            var blockPos = TargetUtil.getTargetBlock(ref, 8.0, store);
            if (blockPos == null) {
                commandContext.sendMessage(Message.raw("You must target a block!"));
                return;
            }

            // Ensure that a lectern was targeted
            var blockType = world.getBlockType(blockPos);
            if (!Utils.isLectern(blockType)) {
                commandContext.sendMessage(Message.raw("You must target a lectern to pickup the grimoire!"));
                return;
            }

            // Get the block entity reference for the targeted lectern
            var blockRef = BlockModule.getBlockEntity(world, blockPos.x, blockPos.y, blockPos.z);
            if (blockRef == null) {
                commandContext.sendMessage(Message.raw("Failed to get the lectern entity!"));
                return;
            }

            // world.execute(() -> {
            // Add the lectern component to the chunk store
            var lectern = chunkStore.ensureAndGetComponent(blockRef, Lectern.getComponentType());
            var is = lectern.pickup();

            if (!Utils.isGrimoire(is)) {
                commandContext.sendMessage(Message.raw("The lectern does not contain a grimoire!"));
                return;
            }

            // Change lectern state to not hold the grimoire (visual)
            world.setBlockInteractionState(blockPos, blockType, "default");

            // Add grimoire from the player's hand
            inventory.getInventory().addItemStack(is);
            // });
        }

    }

}
