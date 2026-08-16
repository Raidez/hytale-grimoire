package dev.raidez.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.metadata.SpellMetadata;

public class TestCommand extends AbstractPlayerCommand {

    public TestCommand() {
        super("test", "Test command");
    }

    @Override
    protected void execute(
            CommandContext commandContext,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef playerRef,
            World world) {

        var hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        var is = InventoryComponent.getItemInHand(store, ref);
        if (is == null || hotbar == null)
            return;

        var spellMeta = is.getFromMetadataOrDefault("Spell", SpellMetadata.CODEC);
        spellMeta.setName("Fireball");
        spellMeta.setDescription("Launches a fiery projectile that explodes on impact.");
        spellMeta.setLevel(1);
        spellMeta.setManaCost(5.0f);
        spellMeta.setCastTime(2.0f);
        spellMeta.setCooldown(3.0f);

        var edited = is.withMetadata(SpellMetadata.KEYED_CODEC, spellMeta);

        byte activeSlot = hotbar.getActiveSlot();
        hotbar.getInventory().replaceItemStackInSlot(activeSlot, is, edited);
        playerRef.sendMessage(Message.raw(""));
    }

}
