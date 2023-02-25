package com.aethersim.projects.io.data;

public interface DataSerializable {

    void deserialize(DataMap data, DataContext context);

    void serialize(DataMap data, DataContext context);
}
