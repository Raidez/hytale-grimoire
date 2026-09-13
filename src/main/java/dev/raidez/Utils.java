package dev.raidez;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class Utils {

    public static boolean isGrimoire(ItemStack is) {
        return is != null && is.getItemId().equals("Weapon_Grimoire");
    }

    public static boolean isScroll(ItemStack is) {
        var tagIndex = AssetRegistry.getOrCreateTagIndex("Scroll");
        var tags = is.getItem().getData().getTags();
        if (!tags.containsKey(tagIndex)) {
            return false;
        }
        return true;
    }

    /*
     * public static Spell getFromItemStack(ItemStack is) {
     * // Check if the item has spell tag
     * var tagIndex = AssetRegistry.getOrCreateTagIndex(Spell.SCROLL_TAG);
     * var tags = is.getItem().getData().getTags();
     * if (!tags.containsKey(tagIndex)) {
     * return null;
     * }
     * 
     * // Get the spell
     * var spell = Spell.getFromItem(is.getItem());
     * return spell;
     * }
     */

    /**
     * Create a new interaction context and execute the given interaction.
     * Used by commands context.
     * Starts a brand new chain via initChain, so the target RootInteraction's own
     * cooldown is checked/applied.
     * 
     * @param interactionId
     * @param store
     * @param ref
     */
    public static void executeInteraction(
            String interactionId,
            Store<EntityStore> store,
            Ref<EntityStore> ref) {

        var manager = store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
        if (manager == null) {
            return;
        }

        var type = InteractionType.Primary;
        var context = InteractionContext.forInteraction(manager, ref, type, store);
        var interaction = RootInteraction.getAssetMap().getAsset(interactionId);
        if (interaction == null) {
            return;
        }

        InteractionChain chain = manager.initChain(type, context, interaction, true);
        manager.queueExecuteChain(chain);
    }

    /**
     * Execute an interaction in the given interaction context.
     * Used by interactions context.
     * Pushes onto the current chain (continuation), so the target RootInteraction's
     * cooldown is NOT checked/applied.
     * 
     * @param interactionId
     * @param context
     */
    public static void executeInteraction(
            String interactionId,
            InteractionContext context) {

        var interaction = RootInteraction.getAssetMap().getAsset(interactionId);
        if (interaction == null) {
            return;
        }
        context.execute(interaction);
    }

}
