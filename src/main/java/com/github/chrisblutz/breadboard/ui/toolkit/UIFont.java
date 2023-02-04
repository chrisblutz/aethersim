package com.github.chrisblutz.breadboard.ui.toolkit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UIFont {

    public enum Style {
        PLAIN, BOLD, ITALIC, BOLD_ITALIC
    }

    private static final Map<String, Font> CUSTOM_FONT_CACHE = new HashMap<>();

    private final String name;
    private final Style style;
    private final float size;

    public UIFont(String name, Style style, float size) {
        this.name = name;
        this.style = style;
        this.size = size;
    }

    @Deprecated
    public Font getInternalFont(float scale) {
        // Determine the style value for the font
        int fontStyle = Font.PLAIN;
        switch (style) {
            case BOLD -> fontStyle = Font.BOLD;
            case ITALIC -> fontStyle = Font.ITALIC;
            case BOLD_ITALIC -> fontStyle = Font.BOLD | Font.ITALIC;
        }

        // If the font exists in the font cache, derive it from there.  Otherwise, create a new font
        // object with the specified parameters
        String cacheKeyName = name.toLowerCase();
        if (CUSTOM_FONT_CACHE.containsKey(cacheKeyName)) {
            // Since the font was in the cache, derive a new object from the cached one
            return CUSTOM_FONT_CACHE.get(cacheKeyName).deriveFont(fontStyle).deriveFont(scale * size);
        } else {
            // Since the font was not in the cache, create a new object and return it
            return new Font(name, fontStyle, (int) (scale * size));
        }
    }

    static void addCustomInternalFont(String name, Font font) {
        // If either parameter is null, return
        if (name == null || font == null)
            return;

        // Register the custom font in the cache
        CUSTOM_FONT_CACHE.put(name.toLowerCase(), font);
    }
}
