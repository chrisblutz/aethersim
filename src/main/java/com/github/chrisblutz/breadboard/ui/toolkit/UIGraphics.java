package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.TextAlignment;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Ellipse;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Rectangle;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.RoundRectangle;
import com.github.chrisblutz.breadboard.ui.toolkit.shape.Shape;

import java.awt.*;

public class UIGraphics implements Cloneable {

    public interface GraphicsRunnable {
        void draw(UIGraphics graphics);
    }

    private Graphics2D internalGraphics;

    // Track the alpha values and color as we go, so we can avoid unnecessary graphics calls
    private float topLevelAlpha = 1f;
    private float currentAlpha = 1f;

    private double currentTranslateX = 0, currentTranslateY = 0;
    private double currentScale = 1d;

    // Track current state of properties and settings
    private UIColor currentColor = null;
    private UIStroke currentStroke = null;
    private UIFont currentFont = null;

    @Deprecated
    public UIGraphics(Graphics2D graphics) {
        this.internalGraphics = graphics;
    }

    @Deprecated
    public Graphics2D getInternalGraphics() {
        return internalGraphics;
    }

    @Deprecated
    public double getCurrentTranslateX() {
        return currentTranslateX;
    }

    @Deprecated
    public double getCurrentTranslateY() {
        return currentTranslateY;
    }

    @Deprecated
    public double getCurrentScale() {
        return currentScale;
    }

    public void translate(double x, double y) {
        // Apply this new translation to the current translation, respecting scaling
        this.currentTranslateX += currentScale * x;
        this.currentTranslateY += currentScale * y;
    }

    public void scale(double scale) {
        // Set the scale, but also check if the scale has been updated
        boolean scaleDiffers = currentScale != scale;
        this.currentScale = scale;

        // If the scale has changed, update the necessary resources assigned to the graphics object
        if (scaleDiffers) {
            setScaledStroke();
            setScaledFont();
        }
    }

    public void clip(double width, double height) {
        // Set the clip with an origin on (0, 0)
        clip(0, 0, width, height);
    }

    public void clip(double x, double y, double width, double height) {
        // Set the clip of the internal graphics
        internalGraphics.setClip(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height)
        );
    }

    public void unclip() {
        // Reset the clip on the internal graphics
        internalGraphics.setClip(null);
    }

    public float getAlpha() {
        return topLevelAlpha;
    }

    public void setAlpha(float alpha) {
        this.topLevelAlpha = alpha;

        // Update current color with new alpha value
        if (currentColor != null)
            setColor(currentColor);
    }

    public UIColor getColor() {
        return currentColor;
    }

    public void setColor(UIColor color) {
        // Update current tracked color
        this.currentColor = color;
        // Apply top-level alpha value and find the internal color
        color = color.withAlpha(topLevelAlpha);
        Color internalColor = color.getInternalColor();
        // Set the color of the internal graphics object
        internalGraphics.setColor(internalColor);
        // If the alpha of the color is different from the current alpha
        // of the internal graphics object, set the alpha on the internal
        // graphics object.
        float desiredAlpha = color.getAlpha();
        if (Math.abs(currentAlpha - desiredAlpha) > UIColor.EPSILON) {
            currentAlpha = desiredAlpha;
            internalGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, desiredAlpha));
        }
    }

    public UIStroke getStroke() {
        return currentStroke;
    }

    public void setStroke(UIStroke stroke) {
        this.currentStroke = stroke;
        setScaledStroke();
    }

    private void setScaledStroke() {
        // Get internal stroke from the current stroke (if not null) and scale it
        if (currentStroke != null)
            internalGraphics.setStroke(currentStroke.getInternalStroke((float) currentScale));
    }

    public UIFont getFont() {
        return currentFont;
    }

    public void setFont(UIFont font) {
        this.currentFont = font;
        setScaledFont();
    }

    private void setScaledFont() {
        // Get internal font from the current font (if not null) and scale it
        if (currentFont != null)
            internalGraphics.setFont(currentFont.getInternalFont((float) currentScale));
    }

    public void draw(Shape shape) {
        // Figure out what type of shape it is, and render it accordingly
        if (shape instanceof Rectangle rectangle) {
            drawRect(
                    rectangle.getX(),
                    rectangle.getY(),
                    rectangle.getWidth(),
                    rectangle.getHeight()
            );
        } else if (shape instanceof RoundRectangle roundRectangle) {
            drawRoundRect(
                    roundRectangle.getX(),
                    roundRectangle.getY(),
                    roundRectangle.getWidth(),
                    roundRectangle.getHeight(),
                    roundRectangle.getArcWidth(),
                    roundRectangle.getArcHeight()
            );
        } else if (shape instanceof Ellipse ellipse) {
            drawEllipse(
                    ellipse.getX(),
                    ellipse.getY(),
                    ellipse.getWidth(),
                    ellipse.getHeight()
            );
        }
    }

    public void fill(Shape shape) {
        // Figure out what type of shape it is, and render it accordingly
        if (shape instanceof Rectangle rectangle) {
            fillRect(
                    rectangle.getX(),
                    rectangle.getY(),
                    rectangle.getWidth(),
                    rectangle.getHeight()
            );
        } else if (shape instanceof RoundRectangle roundRectangle) {
            fillRoundRect(
                    roundRectangle.getX(),
                    roundRectangle.getY(),
                    roundRectangle.getWidth(),
                    roundRectangle.getHeight(),
                    roundRectangle.getArcWidth(),
                    roundRectangle.getArcHeight()
            );
        } else if (shape instanceof Ellipse ellipse) {
            fillEllipse(
                    ellipse.getX(),
                    ellipse.getY(),
                    ellipse.getWidth(),
                    ellipse.getHeight()
            );
        }
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        // Render the shape with the internal graphics object, applying current transforms
        internalGraphics.drawLine(
                (int) getTransformedX(x1),
                (int) getTransformedY(y1),
                (int) getTransformedX(x2),
                (int) getTransformedY(y2)
        );
    }

    public void drawRect(double x, double y, double width, double height) {
        // Render the shape with the internal graphics object
        internalGraphics.drawRect(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height)
        );
    }

    public void fillRect(double x, double y, double width, double height) {
        // Render the shape with the internal graphics object
        internalGraphics.fillRect(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height)
        );
    }

    public void drawRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        // Render the shape with the internal graphics object
        internalGraphics.drawRoundRect(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height),
                (int) getTransformedDimension(arcWidth),
                (int) getTransformedDimension(arcHeight)
        );
    }

    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        // Render the shape with the internal graphics object
        internalGraphics.fillRoundRect(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height),
                (int) getTransformedDimension(arcWidth),
                (int) getTransformedDimension(arcHeight)
        );
    }

    public void drawEllipse(double x, double y, double width, double height) {
        // Render the shape with the internal graphics object
        internalGraphics.drawOval(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height)
        );
    }

    public void fillEllipse(double x, double y, double width, double height) {
        // Render the shape with the internal graphics object
        internalGraphics.fillOval(
                (int) getTransformedX(x),
                (int) getTransformedY(y),
                (int) getTransformedDimension(width),
                (int) getTransformedDimension(height)
        );
    }

    public void drawString(String string, float x, float y) {
        drawString(string, x, y, TextAlignment.Horizontal.LEFT, TextAlignment.Vertical.BASELINE);
    }

    public void drawStringCentered(String string, float x, float y){
        drawString(string, x, y, TextAlignment.Horizontal.CENTER, TextAlignment.Vertical.CENTER);
    }

    public void drawString(String string, float x, float y, TextAlignment.Horizontal horizontalAlignment, TextAlignment.Vertical verticalAlignment) {
        FontMetrics internalFontMetrics = internalGraphics.getFontMetrics();
        float stringWidth = internalFontMetrics.stringWidth(string);

        float actualX = (float) getTransformedX(x);
        float actualY = (float) getTransformedY(y);

        // Calculate the X value based on the horizontal alignment, noting that left-alignment is missing as it does
        // not require adjustment from the provided value.
        switch (horizontalAlignment) {
            case CENTER -> actualX -= (stringWidth / 2);
            case RIGHT -> actualX -= stringWidth;
        }

        // Calculate the Y value based on the vertical alignment, noting that baseline-alignment is missing as it does
        // not require adjustment from the provided value.
        switch (verticalAlignment) {
            case LEADING -> {
                actualY += internalFontMetrics.getAscent() + internalFontMetrics.getLeading();
            }
            case ASCENT -> {
                actualY += internalFontMetrics.getAscent();
            }
            case CENTER -> {
            }
            case DESCENT -> {
                actualY -= internalFontMetrics.getDescent();
            }
        }

        // Draw the string at the specified coordinates, now that we've adjusted for the desired alignments.  This is
        // necessary as AWT draws strings starting at the left baseline
        internalGraphics.drawString(string, actualX, actualY);
    }

    //public UIFontMetrics getFontMetrics() {}

    /**
     * This method runs the specified routine using a copy of this graphics
     * object (with all of its current settings in place).  Changes made to
     * the copy do not propagate, so this method can be used to isolate
     * different render routines.
     * <p>
     * Resources used by the copied graphics object are cleaned up before
     * this method returns.
     *
     * @param runnable the routine to run with the copied graphics object
     */
    public void withCopy(GraphicsRunnable runnable) {
        // Create a new copy of the internal graphics object, then call.
        // the runnable.  After the runnable finishes, dispose of the new
        // graphics object.
        Graphics2D copyGraphics = (Graphics2D) internalGraphics.create();
        try {
            // Create a new UIToolkit graphics object with the copy
            UIGraphics cloneGraphics = (UIGraphics) clone();
            cloneGraphics.internalGraphics = copyGraphics;
            // Pass the copy object to the renderer
            runnable.draw(cloneGraphics);
        } catch (CloneNotSupportedException e) {
            // We should never get here, since this class is Cloneable
            BreadboardLogging.getInterfaceLogger().error("Graphics object could not be cloned.", e);
        }
        // Dispose of the internal copy to free resources
        copyGraphics.dispose();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private double getTransformedX(double x) {
        return (currentScale * x) + currentTranslateX;
    }

    private double getTransformedY(double y) {
        return (currentScale * y) + currentTranslateY;
    }

    private double getTransformedDimension(double dimension) {
        return currentScale * dimension;
    }
}
