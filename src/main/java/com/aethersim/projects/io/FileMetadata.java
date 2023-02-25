package com.aethersim.projects.io;

import com.aethersim.plugins.Plugin;
import com.aethersim.projects.io.data.*;

import java.util.Set;

public record FileMetadata(Set<Plugin> usedPlugins) implements DataSerializable {

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // If there are plugins, include an array of their IDs
        if (usedPlugins() != null && usedPlugins().size() > 0) {
            DataArray pluginArray = new DataArray();
            usedPlugins().forEach(plugin -> pluginArray.add(DataValue.from(plugin.getId())));
            data.put("UsedPlugins", pluginArray);
        }
    }
}
