package com.github.chrisblutz.breadboard.plugins;

import com.github.chrisblutz.breadboard.plugins.injection.ProjectIOInjector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Plugin {

    private static final HashMap<String, Plugin> pluginInstances = new HashMap<>();

    private Logger logger;

    private Map<Class<?>, Set<ProjectIOInjector>> projectIOInjectors;

    private Plugin(String id, String name) {
        logger = LogManager.getLogger("Plugin[" + id + "]");
    }

    public abstract void initialize();

    public abstract void deinitialize();

    protected Logger getLogger() {
        return logger;
    }

    static {
        pluginInstances.put("test", new Plugin("test", "Test Plugin") {
            @Override
            public void initialize() {
                getLogger().info("Testing plugin initialization!");
            }

            @Override
            public void deinitialize() {
                getLogger().info("Testing plugin deinitialization!");
            }
        });
    }

    public static Plugin get(String id) {
        return pluginInstances.get(id);
    }

    public static Collection<Plugin> getAll() {
        return pluginInstances.values();
    }
}
