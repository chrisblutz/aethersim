package com.github.chrisblutz.breadboard.ui.toolkit.display.theming;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.ui.toolkit.UIColor;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UITheme {

    private static final GraphicsEnvironment LOCAL_GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();

    private static final Constructor yamlThemeConstructor;

    private static final UIColor DEFAULT_COLOR = UIColor.rgb((byte) 94, (byte) 6, (byte) 97);
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

    // Represents the active theme
    private static UIThemeInstance theme;

    // Cached map of properties
    private static final Map<String, UIColor> colorPropertyCache = new HashMap<>();
    private static final Map<String, Font> fontPropertyCache = new HashMap<>();

    static {
        // Configure the YAML type description for the UIThemeInstance class
        TypeDescription typeDescription = new TypeDescription(UIThemeInstance.class);
        typeDescription.substituteProperty("Name", String.class, "getName", "setName");
        typeDescription.substituteProperty("Authors", String[].class, "getAuthors", "setAuthors");
        typeDescription.substituteProperty("Colors", Map.class, "getColors", "setColors");
        typeDescription.substituteProperty("Fonts", Map.class, "getFonts", "setFonts");

        // Configure the YAML constructor for the UIThemeInstance class
        yamlThemeConstructor = new Constructor(UIThemeInstance.class);
        yamlThemeConstructor.addTypeDescription(typeDescription);

        // By default, load the "Default" theme
        loadTheme("Default");
    }

    public static UIColor getColor(String key) {
        // If the color isn't present in the cache, parse it
        if (!colorPropertyCache.containsKey(key)) {
            // Get the value of the color key from the property mapping.  A null value indicates no
            // value was found.
            String colorValue = null;
            // If we have a theme, query the color
            if (theme != null)
                colorValue = theme.getColors().get(key);

            // Log an error if the color value is null
            if (colorValue == null)
                BreadboardLogging.getInterfaceLogger().warn("Color key '{}' does not exist.", key);
            else
                BreadboardLogging.getInterfaceLogger().debug("Decoding color value '{}'.", colorValue);

            // Available formats for colors:
            //  - hex XXXXXX
            //  - rgb XXX XXX XXX

            // Parse the color value and add it to the cache
            colorPropertyCache.put(key, parseColorValue(colorValue));
        }

        // Return the cached color
        return colorPropertyCache.get(key);
    }

    private static UIColor parseColorValue(String colorValue) {
        // If the color value is null, return the default color
        if (colorValue == null)
            return DEFAULT_COLOR;

        // Split by spaces
        String[] colorComponents = colorValue.split(" ");

        // If there are fewer than two elements, log an error and return the default color
        if (colorComponents.length < 2) {
            BreadboardLogging.getInterfaceLogger().warn("Color value '{}' does not represent a valid color.", colorValue);
            return DEFAULT_COLOR;
        }

        // Switch on the first element of the color value
        switch (colorComponents[0]) {
            case "hex" -> {
                // Make sure we have two elements
                if (colorComponents.length == 2) {
                    return parseHexColorValue(colorComponents[1]);
                } else {
                    BreadboardLogging.getInterfaceLogger().warn("Hexadecimal color value '{}' does not match the format 'hex XXXXXX'.", colorValue);
                }
            }

            case "rgb" -> {
                // Make sure we have four elements
                if (colorComponents.length == 4) {
                    return parseRGBColorValue(colorComponents[1], colorComponents[2], colorComponents[3]);
                } else {
                    BreadboardLogging.getInterfaceLogger().warn("RGB color value '{}' does not match the format 'hex XXXXXX'.", colorValue);
                }
            }

            default -> {
                // This is an invalid color specifier, so log an error
                BreadboardLogging.getInterfaceLogger().warn("Color value '{}' specifies an invalid color format '{}'.", colorValue, colorComponents[0]);
            }
        }

        // If we get here, we had errors finding the color, so return the default color
        return DEFAULT_COLOR;
    }

    private static UIColor parseHexColorValue(String hexColorValueString) {
        // If the color value is null, return the default color
        if (hexColorValueString == null)
            return DEFAULT_COLOR;

        // Load the value as an integer in base 16
        try {
            int hexColorValue = Integer.parseInt(hexColorValueString, 16);
            // Create the color
            return UIColor.rgb(hexColorValue); // TODO - alpha
        } catch (NumberFormatException e) {
            BreadboardLogging.getInterfaceLogger().warn("Color value '{}' is not a valid hexadecimal value.", hexColorValueString);
            return DEFAULT_COLOR;
        }
    }

    private static UIColor parseRGBColorValue(String redColorValueString, String greenColorValueString, String blueColorValueString) {
        // Load the values as integers in base 10
        try {
            int redColorValue = Integer.parseInt(redColorValueString, 10);
            if (redColorValue < 0 || redColorValue > 255)
                throw new NumberFormatException();
            try {
                int greenColorValue = Integer.parseInt(greenColorValueString, 10);
                if (greenColorValue < 0 || greenColorValue > 255)
                    throw new NumberFormatException();
                try {
                    int blueColorValue = Integer.parseInt(blueColorValueString, 10);
                    if (blueColorValue < 0 || blueColorValue > 255)
                        throw new NumberFormatException();
                    // Create the color
                    return UIColor.rgb((byte) redColorValue, (byte) greenColorValue, (byte) blueColorValue); // TODO alpha
                    // TODO Clean this method up
                } catch (NumberFormatException e) {
                    BreadboardLogging.getInterfaceLogger().warn("Blue color component '{}' is not a valid byte (0-255).", blueColorValueString);
                }
            } catch (NumberFormatException e) {
                BreadboardLogging.getInterfaceLogger().warn("Green color component '{}' is not a valid byte (0-255).", greenColorValueString);
            }
        } catch (NumberFormatException e) {
            BreadboardLogging.getInterfaceLogger().warn("Red color component '{}' is not a valid byte (0-255).", redColorValueString);
        }

        // If we get here, we had issues reading one of the colors, so return the default color
        return DEFAULT_COLOR;
    }

    public static Font getFont(String key) {
        // If the font isn't present in the cache, parse it
        if (!fontPropertyCache.containsKey(key)) {
            // Get the value of the font key from the property mapping.  A null value indicates no
            // value was found.
            String fontValue = null;
            // If we have a theme, query the color
            if (theme != null)
                fontValue = theme.getFonts().get(key);

            // Log an error if the font value is null
            if (fontValue == null)
                BreadboardLogging.getInterfaceLogger().warn("Font key '{}' does not exist.", key);
            else
                BreadboardLogging.getInterfaceLogger().debug("Decoding font value '{}'.", fontValue);

            // Available formats for colors:
            //  - Font Family Style Size

            // Parse the color value and add it to the cache
            fontPropertyCache.put(key, parseFontValue(fontValue));
        }

        // Return the cached font
        return fontPropertyCache.get(key);
    }

    private static Font parseFontValue(String fontValue) {
        // If the value is null, return the default font
        if (fontValue == null)
            return DEFAULT_FONT;

        // Split the value into parts
        String[] fontValueComponents = fontValue.split(" ");

        // If there are fewer than three components, log an error and return the default
        if (fontValueComponents.length < 3) {
            BreadboardLogging.getInterfaceLogger().warn("Font value '{}' does not match the format 'Font Family Style Size' (font family may contain whitespace).", fontValue);
            return DEFAULT_FONT;
        }

        String fontSizeString = fontValueComponents[fontValueComponents.length - 1];
        String fontStyleString = fontValueComponents[fontValueComponents.length - 2];
        String fontFamily = String.join(" ", Arrays.copyOf(fontValueComponents, fontValueComponents.length - 2));

        // If the font family doesn't exist, log an error and return the default font
        if (!Arrays.asList(LOCAL_GRAPHICS_ENVIRONMENT.getAvailableFontFamilyNames()).contains(fontFamily)) {
            BreadboardLogging.getInterfaceLogger().warn("Font family '{}' does not exist.", fontFamily);
            return DEFAULT_FONT;
        }

        int fontStyle;
        switch (fontStyleString.toLowerCase()) {
            case "plain" -> fontStyle = Font.PLAIN;
            case "bold" -> fontStyle = Font.BOLD;
            case "italic" -> fontStyle = Font.ITALIC;
            default -> {
                BreadboardLogging.getInterfaceLogger().warn("Font style '{}' must be one of 'plain', 'bold', or 'italic'.", fontStyleString);
                return DEFAULT_FONT;
            }
        }

        int fontSize;
        try {
            fontSize = Integer.parseInt(fontSizeString);
            // Check that the size is greater than 0
            if (fontSize <= 0) {
                BreadboardLogging.getInterfaceLogger().warn("Font size '{}' must be a positive integer.", fontSizeString);
                return DEFAULT_FONT;
            }
        } catch (NumberFormatException e) {
            BreadboardLogging.getInterfaceLogger().warn("Font size '{}' must be a positive integer.", fontSizeString);
            return DEFAULT_FONT;
        }

        // Now that we've validated the components, create the font
        return new Font(fontFamily, fontStyle, fontSize);
    }

    public static void loadTheme(String name) {
        BreadboardLogging.getInterfaceLogger().info("Loading theme '{}'.", name);
        // Retrieve the directory for the theme
        File themeDirectory = new File("Themes/" + name);
        // If the theme doesn't exist, keep default and log an error
        if (!themeDirectory.exists() || !themeDirectory.isDirectory()) {
            BreadboardLogging.getInterfaceLogger().warn("Unable to find directory for theme '{}'.", name);
            return;
        }

        // If the theme directory exists, check for the theme YAML
        File themeYaml = new File(themeDirectory, "theme.yaml");
        // If it doesn't exist, keep default and log an error
        if (!themeYaml.exists() || !themeYaml.isFile()) {
            BreadboardLogging.getInterfaceLogger().warn("Unable to find YAML file for theme '{}'.", name);
            return;
        }

        // Load the YAML file for the theme
        try (
            FileInputStream fileInputStream = new FileInputStream(themeYaml)
        ) {
            // Load the YAML into a UIThemeInstance object
            Yaml yaml = new Yaml(yamlThemeConstructor);
            theme = yaml.load(fileInputStream);

            // Since we've loaded properties, we need to clear the caches
            colorPropertyCache.clear();
        } catch (IOException e) {
            BreadboardLogging.getInterfaceLogger().error("An I/O error occurred while loading theme '{}'.", name, e);
            return;
        } catch (YAMLException e) {
            BreadboardLogging.getInterfaceLogger().error("A YAML I/O error occurred while loading theme '{}'.", name, e);
            return;
        }

        // Now that we've loaded the main YAML, load any auxiliary files (like fonts)
        loadThemeFonts(themeDirectory);
    }

    private static void loadThemeFonts(File themeDirectory) {
        // Find the fonts directory
        File fontsDirectory = new File(themeDirectory, "Fonts");
        // If the directory doesn't exist, return now
        if (!fontsDirectory.exists() || !fontsDirectory.isDirectory())
            return;

        // List all files in the directory, and try to load them as fonts
        File[] fontFiles = fontsDirectory.listFiles();
        // If the list of files is null, log an error and return
        if (fontFiles == null) {
            BreadboardLogging.getInterfaceLogger().error("An unspecified error occurred while loading fonts.");
            return;
        }

        // Read all font files
        for (File fontFile : fontFiles) {
            BreadboardLogging.getInterfaceLogger().info("Loading font from '{}'.", fontFile.getName());

            try {
                // Read all fonts from the file
                Font[] fonts = Font.createFonts(fontFile);
                // Register the fonts with the graphics environment
                List<String> fontFamilies = new ArrayList<>();
                for (Font font : fonts) {
                    LOCAL_GRAPHICS_ENVIRONMENT.registerFont(font);
                    fontFamilies.add(font.getFamily());
                }
                // Log all loaded families
                if (fontFamilies.size() > 0)
                    BreadboardLogging.getInterfaceLogger().info("    Loaded families: '{}'", String.join("', '", fontFamilies));
            } catch (IOException e) {
                BreadboardLogging.getInterfaceLogger().error("An I/O error occurred while reading fonts from '{}'.", fontFile.getName(), e);
            } catch (FontFormatException e) {
                BreadboardLogging.getInterfaceLogger().error("File '{}' does not contain a recognizable font.", fontFile.getName(), e);
            }
        }
    }

    public static void resetTheme() {
        loadTheme("Default");
    }
}
