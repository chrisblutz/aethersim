package com.aethersim.plugins;

import com.aethersim.logging.AetherSimLogging;
import com.aethersim.plugins.injection.DataSerializationInjector;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataSerializable;
import com.aethersim.projects.io.data.exceptions.DataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginInjectors {

    // This record maps injectors to their plugins
    private record InjectorRecord<T>(Plugin plugin, T injector) {}

    private static final Map<Class<? extends DataSerializable>,
            List<InjectorRecord<DataSerializationInjector<? extends DataSerializable>>>> serializationInjectors = new HashMap<>();

    static <T extends DataSerializable> void registerDataSerializationInjector(Plugin plugin, Class<T> dataClass, DataSerializationInjector<T> serializationInjector) {
        // If the class doesn't have any registered injectors, initialize the list
        if (!serializationInjectors.containsKey(dataClass))
            serializationInjectors.put(dataClass, new ArrayList<>());

        // Add a new injector record to the mapping
        serializationInjectors.get(dataClass).add(new InjectorRecord<>(plugin, serializationInjector));
    }

    public static <T extends DataSerializable> void doSerializationInjection(Class<? extends DataSerializable> dataClass, DataSerializable object, DataMap data, DataContext context) {
        runSerializationInjectors(dataClass, object, data, context, true);
    }

    public static <T extends DataSerializable> void doDeserializationInjection(Class<? extends DataSerializable> dataClass, DataSerializable object, DataMap data, DataContext context) {
        runSerializationInjectors(dataClass, object, data, context, false);
    }

    private static <T extends DataSerializable> void runSerializationInjectors(Class<? extends DataSerializable> dataClass, DataSerializable object, DataMap data, DataContext context, boolean serializing) {
        // Check that the class is compatible with the object being passed in
        if (!dataClass.isInstance(object))
            throw new DataException("Serialization calls must use compatible classes and objects (" + dataClass.getName() + " is not compatible with " + object.getClass().getName() + ").");

        // If the data class does not have any registered injectors, exit early
        if (!serializationInjectors.containsKey(dataClass))
            return;

        // Otherwise, get all injector records and run them.  Add each plugin to the set of used plugins in the context
        // as well, to make sure the manifest can identify which plugins were used in the creation of a file.
        List<InjectorRecord<DataSerializationInjector<? extends DataSerializable>>> injectorRecords = serializationInjectors.get(dataClass);
        for (InjectorRecord<DataSerializationInjector<? extends DataSerializable>> injectorRecord : injectorRecords) {
            DataSerializationInjector<T> injector = null;
            try {
                // Types are checked when registering injectors, and these statements are wrapped in try/catch
                // statements to catch any cast issues, so we can ignore warnings about unchecked casts.
                injector = (DataSerializationInjector<T>) injectorRecord.injector();
            } catch (ClassCastException e) {
                AetherSimLogging.getPluginManagerLogger().warn(
                        "A data serialization injector belonging to plugin '{}' could not be run.",
                        injectorRecord.plugin().getId(),
                        e
                );
            }

            // If retrieval of the injector encountered an error, continue to the next injector
            if (injector == null)
                continue;

            // Check if we're serializing or deserializing and call the appropriate method
            if (serializing)
                injector.serialize((T) object, data, context);
            else
                injector.deserialize((T) object, data, context);

            // Note in the context that this plugin was used in the (de)serialization of the data
            context.getUsedPlugins().add(injectorRecord.plugin());
        }
    }
}
