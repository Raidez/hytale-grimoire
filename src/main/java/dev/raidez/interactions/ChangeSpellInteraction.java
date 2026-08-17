package dev.raidez.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import dev.raidez.metadata.GrimoireMetadata;

public class ChangeSpellInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<ChangeSpellInteraction> CODEC = BuilderCodec
            .builder(ChangeSpellInteraction.class, ChangeSpellInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(
            InteractionType type,
            InteractionContext context,
            CooldownHandler cooldown) {

        var ref = context.getEntity();
        var store = context.getCommandBuffer().getStore();
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        // Check if the player is holding a grimoire
        var is = context.getHeldItem();
        if (is == null || !is.getItemId().equals("Weapon_Grimoire"))
            return;

        // Get the grimoire metadata
        var meta = is.getFromMetadataOrDefault(GrimoireMetadata.METADATA_KEY, GrimoireMetadata.CODEC);
        if (meta == null || meta.getSpellList().length == 0)
            return;

        // Update the spell index (wrap around)
        meta.setSpellIndex((short) ((meta.getSpellIndex() + 1) % meta.getSpellList().length));
        var newis = is.withMetadata(GrimoireMetadata.KEYED_CODEC, meta);

        // Update the item stack in the player's inventory
        var inventory = context.getHeldItemContainer();
        inventory.replaceItemStackInSlot(context.getHeldItemSlot(), is, newis);

        // Send a message to the player about the new spell
        var spellId = meta.getSpellList()[meta.getSpellIndex()];
        var spellName = Message.translation(spellId);
        playerRef.sendMessage(Message.join(Message.raw("Changed spell to: "), spellName));
        context.getState().state = InteractionState.Finished;
    }

}
