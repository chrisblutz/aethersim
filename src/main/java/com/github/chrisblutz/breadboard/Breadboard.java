package com.github.chrisblutz.breadboard;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.plugins.Plugin;
import com.github.chrisblutz.breadboard.ui.BreadboardUI;

public class Breadboard {
    public static void main(String[] args) {
        BreadboardLogging.getLogger().info("--- Starting Breadboard ---");
        BreadboardLogging.logEnvironmentInformation();
        BreadboardLogging.getLogger().info("Initializing plugins...");
        Plugin.getAll().parallelStream().forEach(Plugin::initialize);
        BreadboardUI.initialize();
    }
}
