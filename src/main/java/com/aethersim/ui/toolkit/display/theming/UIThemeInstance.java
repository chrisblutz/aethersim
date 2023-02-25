package com.aethersim.ui.toolkit.display.theming;

import java.util.Map;

public class UIThemeInstance {

    private String name;
    private String[] authors;

    private Map<String, String> colors;
    private Map<String, String> fonts;

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String[] getAuthors() {
        return authors;
    }

    protected void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    protected void setColors(Map<String, String> colors) {
        this.colors = colors;
    }

    public Map<String, String> getFonts() {
        return fonts;
    }

    protected void setFonts(Map<String, String> fonts) {
        this.fonts = fonts;
    }
}
