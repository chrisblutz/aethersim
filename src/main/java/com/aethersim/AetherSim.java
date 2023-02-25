package com.aethersim;

import com.aethersim.designs.templates.ChipTemplate;
import com.aethersim.logging.AetherSimLogging;
import com.aethersim.plugins.Plugin;
import com.aethersim.ui.AetherSimUI;

public class AetherSim {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> AetherSimLogging.getLogger().error("Uncaught exception in thread '{}'.", t.getName(), e));


        AetherSimLogging.getLogger().info("--- Starting AetherSim ---");
        AetherSimLogging.logEnvironmentInformation();
        AetherSimLogging.logAetherSimInformation();
        ChipTemplate.registerNative();
        AetherSimLogging.getLogger().info("The following plugins are currently installed:");
        Plugin.getAll().forEach(plugin -> AetherSimLogging.getLogger().info("  - {} [{}]", plugin.getName(), plugin.getId()));
        AetherSimLogging.getLogger().info("Initializing plugins...");
        Plugin.getAll().parallelStream().forEach(Plugin::initialize);
        AetherSimUI.initialize();
    }
}
