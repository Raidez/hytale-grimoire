package dev.raidez;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class GrimoirePlugin extends JavaPlugin {

    private static GrimoirePlugin instance;

    public GrimoirePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        getCommandRegistry()
                .registerCommand(new GrimoireCommand());
    }

    public static GrimoirePlugin get() {
        return instance;
    }

}
