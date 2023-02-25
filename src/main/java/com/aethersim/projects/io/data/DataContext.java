package com.aethersim.projects.io.data;

import com.aethersim.plugins.Plugin;
import com.aethersim.plugins.PluginInjectors;

import java.util.HashSet;
import java.util.Set;

public class DataContext {

    public static final int DEFAULT_FORMAT_VERSION = 1;

    private final int formatVersion;
    private final Set<Plugin> usedPlugins = new HashSet<>();

    public DataContext() {
        this(DEFAULT_FORMAT_VERSION);
    }

    public DataContext(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public Set<Plugin> getUsedPlugins() {
        return usedPlugins;
    }

    public DataMap serialize(DataSerializable object) {
        return serialize(object, false);
    }

    public DataMap serialize(DataSerializable object, boolean includeTopLevelMetadata) {
        // Create a new data map and serialize the object into it, after applying the top-level metadata if necessary
        DataMap data = new DataMap();
        if (includeTopLevelMetadata)
            serializeTopLevelMetadata(data);
        object.serialize(data, this);

        // Run applicable serialization injectors (including all applicable superclasses or interfaces)
        doSerializationInjection(object.getClass(), object, data, this);

        return data;
    }

    private void serializeTopLevelMetadata(DataMap data) {
        // Set the version in the data map, under the AetherSim header
        DataMap aetherSimData = new DataMap();
        aetherSimData.put("FormatVersion", DataValue.from(getFormatVersion()));
        data.put("AetherSim", aetherSimData);
    }

    private void doSerializationInjection(Class<?> objectClass, DataSerializable object, DataMap data, DataContext context) {
        // Call the injectors for the current object class (if it inherits from DataSerializable), and then call
        // this method again for all subclasses
        if (!DataSerializable.class.isAssignableFrom(objectClass))
            return;

        // The class will be castable to DataSerializable here, since we type checked above
        PluginInjectors.doSerializationInjection((Class<? extends DataSerializable>) objectClass, object, data, context);

        if (objectClass.getSuperclass() != null)
            doSerializationInjection(objectClass.getSuperclass(), object, data, context);
        for (Class<?> classInterface : objectClass.getInterfaces())
            doSerializationInjection(classInterface, object, data, context);
    }
}
