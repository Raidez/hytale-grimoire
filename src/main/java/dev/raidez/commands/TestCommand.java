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

import dev.raidez.metadata.Spell;

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

        var spell = is.getFromMetadataOrDefault(Spell.METADATA_KEY, Spell.CODEC);
        Spell.builder(spell)
                .name("Fireball")
                .description("A powerful fireball spell that deals damage to enemies.")
                .level(1)
                .castTime(2)
                .cooldown(5)
                .manaCost(10)
                .build();

        var edited = is.withMetadata(Spell.KEYED_CODEC, spell);

        byte activeSlot = hotbar.getActiveSlot();
        hotbar.getInventory().replaceItemStackInSlot(activeSlot, is, edited);
        playerRef.sendMessage(Message.raw(""));
    }

}
