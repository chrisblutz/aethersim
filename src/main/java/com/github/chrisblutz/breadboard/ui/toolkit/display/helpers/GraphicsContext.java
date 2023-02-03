package com.github.chrisblutz.breadboard.ui.toolkit.display.helpers;

import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsContext {

    private final Graphics2D internalGraphics;

    public GraphicsContext() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        internalGraphics = image.createGraphics();
    }

    public UIDimension getStringBounds(Font font, String string) {
        FontMetrics metrics = internalGraphics.getFontMetrics(font);
        return new UIDimension(
                metrics.stringWidth(string),
                metrics.getHeight()
        );
    }
}
