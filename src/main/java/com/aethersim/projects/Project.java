package com.aethersim.projects;

import com.aethersim.designs.Design;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataSerializable;
import com.aethersim.projects.io.data.DataValue;

public class Project implements DataSerializable {

    private String id;
    private String name;

    private Design design;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {

    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // Store all necessary information into the data map
        if (getId() != null)
            data.put("Id", DataValue.from(getId()));
        if (getName() != null)
            data.put("Name", DataValue.from(getName()));
        if (getDesign() != null)
            data.put("Design", context.serialize(design));
    }
}
