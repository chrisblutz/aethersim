package com.aethersim.plugins.injection;

import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataSerializable;

public interface DataSerializationInjector<T extends DataSerializable> {

    void deserialize(T object, DataMap data, DataContext context);

    void serialize(T object, DataMap data, DataContext context);
}
