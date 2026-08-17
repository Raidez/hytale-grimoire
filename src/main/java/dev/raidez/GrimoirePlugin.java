package dev.raidez;

import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.raidez.handlers.CraftGrimoireHandler;
import dev.raidez.handlers.CraftSpellHandler;
import dev.raidez.interactions.CastSpellInteraction;
import dev.raidez.interactions.ChangeSpellInteraction;
import dev.raidez.interactions.InfuseSpellInteraction;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        GrimoirePlugin.instance = this;
    }

    @Override
    protected void setup() {
        // Register handlers
        this.getEventRegistry()
                .registerGlobal(PlayerCraftEvent.class, CraftSpellHandler::onCraftSpell);
        this.getEventRegistry()
                .registerGlobal(PlayerCraftEvent.class, CraftGrimoireHandler::onCraftGrimoire);

        // Register interactions
        this.getCodecRegistry(Interaction.CODEC)
                .register("CastSpell", CastSpellInteraction.class, CastSpellInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC)
                .register("ChangeSpell", ChangeSpellInteraction.class, ChangeSpellInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC)
                .register("InfuseSpell", InfuseSpellInteraction.class, InfuseSpellInteraction.CODEC);
    }

    public static GrimoirePlugin get() {
        return instance;
    }

}
