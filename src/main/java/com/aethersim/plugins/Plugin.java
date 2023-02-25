package com.aethersim.plugins;

import com.aethersim.designs.Chip;
import com.aethersim.plugins.injection.DataSerializationInjector;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;

public abstract class Plugin {

    private static final HashMap<String, Plugin> pluginInstances = new HashMap<>();

    private final String id, name;
    private final Logger logger;

    private Plugin(String id, String name) {
        this.id = id;
        this.name = name;
        logger = LogManager.getLogger("Plugin[" + id + "]");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract void initialize();

    public abstract void deinitialize();

    protected Logger getLogger() {
        return logger;
    }

    protected <T extends DataSerializable> void registerDataSerializationInjector(Class<T> dataClass, DataSerializationInjector<T> serializationInjector) {
        // Call the non-public method in the injector manager to register the serialization injector and
        // attach it to this plugin
        PluginInjectors.registerDataSerializationInjector(this, dataClass, serializationInjector);
    }

    static {
        pluginInstances.put("test", new Plugin("test", "Test Plugin") {
            @Override
            public void initialize() {
                getLogger().info("Testing plugin initialization!");
                registerDataSerializationInjector(Chip.class, new DataSerializationInjector<>() {
                    @Override
                    public void deserialize(Chip object, DataMap data, DataContext context) {
                        getLogger().info("Hi from the deserializer!");
                    }

                    @Override
                    public void serialize(Chip object, DataMap data, DataContext context) {
                        getLogger().info("Hi from the serializer!");
                    }
                });
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
