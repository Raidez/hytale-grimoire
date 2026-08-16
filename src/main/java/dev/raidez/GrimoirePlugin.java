package dev.raidez;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.components.SpellComponent;
import dev.raidez.handlers.CraftSpellHandler;
import dev.raidez.systems.SpellSystems;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    private ComponentType<EntityStore, SpellComponent> spellComponentType;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        GrimoirePlugin.instance = this;
    }

    @Override
    protected void setup() {

        this.getEventRegistry()
                .registerGlobal(PlayerCraftEvent.class, CraftSpellHandler::onCraftSpell);

        this.spellComponentType = this.getEntityStoreRegistry()
                .registerComponent(SpellComponent.class, SpellComponent::new);
        this.getEntityStoreRegistry()
                .registerSystem(new SpellSystems.EnsureSpellComponents());
    }

    public static GrimoirePlugin get() {
        return instance;
    }

    public ComponentType<EntityStore, SpellComponent> getSpellComponentType() {
        return this.spellComponentType;
    }

}
