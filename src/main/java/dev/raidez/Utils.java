package dev.raidez;

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

    /**
     * Create a new interaction context and execute the given interaction.
     * 
     * @param interaction
     * @param store
     * @param ref
     */
    @SuppressWarnings("deprecation")
    public static void executeInteraction(
            String interaction,
            Store<EntityStore> store,
            Ref<EntityStore> ref) {

        var interactionManager = store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
        if (interactionManager == null) {
            return;
        }

        var interactionType = InteractionType.Primary;
        var context = InteractionContext.forInteraction(interactionManager, ref, interactionType, store);
        var rootInteraction = RootInteraction.getRootInteractionOrUnknown(interaction);
        if (rootInteraction == null) {
            return;
        }

        InteractionChain chain = interactionManager.initChain(interactionType, context, rootInteraction, true);
        interactionManager.queueExecuteChain(chain);
    }

    /**
     * Execute an interaction in the given interaction context.
     * 
     * @param interaction
     * @param context
     */
    @SuppressWarnings("deprecation")
    public static void executeInteraction(
            String interaction,
            InteractionContext context) {

        var rootInteraction = RootInteraction.getRootInteractionOrUnknown(interaction);
        if (rootInteraction == null) {
            return;
        }
        context.execute(rootInteraction);
    }

}
