package com.aethersim.designs.templates;

import com.aethersim.designs.Chip;
import com.aethersim.designs.Pin;
import com.aethersim.designs.exceptions.DesignException;
import com.aethersim.plugins.Plugin;
import com.aethersim.projects.Scope;
import com.aethersim.projects.io.data.DataSerializable;
import com.aethersim.simulation.SimulatedDesign;
import com.aethersim.ui.toolkit.UIGraphics;
import com.aethersim.ui.toolkit.UITheme;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ChipTemplate implements DataSerializable {

    private static final Map<String, ChipTemplate> chipTemplateRegistry = new HashMap<>();

    private Scope loadScope;
    private Plugin parentPlugin;

    private String id;
    private String name;

    private int width;
    private int height;

    private Set<Pin> pins = new LinkedHashSet<>();

    public Scope getLoadScope() {
        return loadScope;
    }

    public void setLoadScope(Scope loadScope) {
        this.loadScope = loadScope;
    }

    public Plugin getParentPlugin() {
        return parentPlugin;
    }

    public void setParentPlugin(Plugin parentPlugin) {
        this.parentPlugin = parentPlugin;
    }

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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Collection<Pin> getPins() {
        return pins;
    }

    protected void setPins(Set<Pin> pins) {
        this.pins = pins;
    }

    public void renderChipPackage(UIGraphics graphics, Chip chip, SimulatedDesign design) {
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.Design.CHIP_FOREGROUND));
        graphics.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.TEXT_DEFAULT).derive(1f)); // TODO

        // Draw the chip text in the center of the chip
        graphics.drawStringCentered(getName(), (float) getWidth() / 2, (float) getHeight() / 2);
    }

    public static ChipTemplate get(String id) {
        if (chipTemplateRegistry.containsKey(id))
            return chipTemplateRegistry.get(id);
        else
            throw new DesignException("Chip template '" + id + "' does not exist.");
    }

    public static Set<ChipTemplate> getForScope(Scope scope) {
        return chipTemplateRegistry.values().stream()
                .filter(template -> template.getLoadScope() == scope)
                .collect(Collectors.toSet());
    }

    public static void register(ChipTemplate template, Scope scope) {
        register(template, scope, null);
    }

    public static void register(ChipTemplate template, Scope scope, Plugin parentPlugin) {
        String id = template.getId();
        if (chipTemplateRegistry.containsKey(id))
            throw new DesignException("Chip template '" + id + "' already exists, so it cannot be registered again.");

        // If the scope is PLUGIN, make sure the plugin is not null
        if (scope == Scope.PLUGIN && parentPlugin == null)
            throw new DesignException("Chip templates with a plugin scope must define a non-null parent plugin.");

        chipTemplateRegistry.put(id, template);

        // Set the scope of the template
        template.setLoadScope(scope);
        template.setParentPlugin(parentPlugin);
    }

    public static void registerNative() {
        // Register all native built-in templates (including transistor templates)
        TransistorTemplate.registerAll();
        ConstantTemplate.registerAll();
        ToggleTemplate.registerAll();
    }
}
