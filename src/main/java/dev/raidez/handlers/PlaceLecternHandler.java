package dev.raidez.handlers;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;

/**
 * Add metadata to placed lecterns.
 */
public class PlaceLecternHandler extends WorldEventSystem<EntityStore, PlaceBlockEvent> {

    private final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();

    public PlaceLecternHandler() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public void handle(Store<EntityStore> arg0, CommandBuffer<EntityStore> arg1, PlaceBlockEvent arg2) {
        LOGGER.atInfo().log("PlaceLecternHandler: Triggered");
    }

}
