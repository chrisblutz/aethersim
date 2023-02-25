package com.aethersim.designs.templates;

import com.aethersim.designs.Design;
import com.aethersim.designs.Pin;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;

import java.util.Collection;

public class DesignedTemplate extends ChipTemplate {

    public Design design;

    public DesignedTemplate(Design design) {
        this.design = design;
    }

    public Design getDesign() {
        return design;
    }

    @Override
    public Collection<Pin> getPins() {
        return design.getPins();
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
        data.put("PackageWidth", DataValue.from(getWidth()));
        data.put("PackageHeight", DataValue.from(getHeight()));
        if (getDesign() != null)
            data.put("Design", context.serialize(getDesign()));
    }
}
