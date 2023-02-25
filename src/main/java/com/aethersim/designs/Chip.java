package com.aethersim.designs;

import com.aethersim.designs.templates.ChipTemplate;
import com.aethersim.designs.templates.SimulatedTemplate;
import com.aethersim.projects.Scope;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.data.DataValue;
import com.aethersim.simulation.ChipState;

public class Chip extends DesignElement {

    // Default to -1, since IDs must be greater than 0
    private int id = -1;
    private Point location = new Point(getTransform());

    private ChipTemplate chipTemplate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
        // Attach the transform to the point
        location.setTransform(getTransform());
    }

    public ChipTemplate getChipTemplate() {
        return chipTemplate;
    }

    public void setChipTemplate(ChipTemplate chipTemplate) {
        this.chipTemplate = chipTemplate;
    }

    @Override
    protected void onTransformUpdated() {

    }

    @Override
    protected void onTransformAccepted() {
        // Update the location of the chip to accept the transformed values
        location = location.withTransform();
    }

    @Override
    public boolean contains(Point point) {
        int chipX1 = getLocation().getX();
        int chipY1 = getLocation().getY();
        int chipX2 = chipX1 + getChipTemplate().getWidth();
        int chipY2 = chipY1 + getChipTemplate().getHeight();
        return point.getX() >= chipX1 &&
                point.getX() <= chipX2 &&
                point.getY() >= chipY1 &&
                point.getY() <= chipY2;
    }

    @Override
    public void deserialize(DataMap data, DataContext context) {
        // Get all necessary information from the data map by deserializing the proper key
        if (data.containsKey("Location")) {
            DataMap locationData = data.get("Location").getMap();
            getLocation().deserialize(locationData, context);
        }
    }

    @Override
    public void serialize(DataMap data, DataContext context) {
        // Store all necessary information into the data map
        if (getId() > 0)
            data.put("Id", DataValue.from(getId()));

        if (getLocation() != null)
            // Serialize the location and store it in the proper key
            data.put("Location", context.serialize(getLocation()));

        if (getChipTemplate() != null) {
            // Store the chip template as an ID, so that it can be loaded from that ID during deserialization
            data.put("Template", DataValue.from(getChipTemplate().getId()));

            // If the chip template is plugin-based, note the plugin in the used plugins in the context
            if (getChipTemplate().getLoadScope() == Scope.PLUGIN && getChipTemplate().getParentPlugin() != null)
                context.getUsedPlugins().add(getChipTemplate().getParentPlugin());

            // If the template is a simulated template, serialize the state
            if (getChipTemplate() instanceof SimulatedTemplate<?> simulatedTemplate) {
                // Get the chip state (if it exists) for this chip
                ChipState state = simulatedTemplate.getState(this);
                if (state != null)
                    data.put("State", context.serialize(state));
            }
        }
    }
}
