package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;

import java.awt.*;

public class UIGraphics implements Cloneable {

    public interface GraphicsRunnable {
        void draw(UIGraphics graphics);
    }

    private Graphics2D internalGraphics;

    // Track the alpha values and color as we go, so we can avoid unnecessary graphics calls
    private float topLevelAlpha = 1f;
    private float currentAlpha = 1f;

    // Track top-level clip and any applied transforms, as we want any clips applied here
    // to apply on top of the top-level clip, which was defined without the current transforms
    private int currentTranslateX = 0, currentTranslateY = 0;
    private double currentScaleX = 1d, currentScaleY = 1d;

    // Track current state of properties and settings
    private UIFont currentFont = null;
    private UIColor currentColor = null;

    UIGraphics(Graphics2D graphics) {
        this.internalGraphics = graphics;
    }

    @Deprecated
    public Graphics2D getInternalGraphics() {
        return internalGraphics;
    }

    public void translate(int x, int y) {
        // Set the translation on the internal graphics
        internalGraphics.translate(x, y);
    }

    /*public void scale(int x, int y) {} TODO */

    public void clip(int width, int height) {
        // Set the clip with an origin on (0, 0)
        clip(0, 0, width, height);
    }

    public void clip(int x, int y, int width, int height) {
        // Set the clip of the internal graphics
        internalGraphics.setClip(x, y, width, height);
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

    /*public UIFont getFont() { TODO
        return currentFont;
    }*/

    /*public void setFont(UIFont font) {}*/

    public void drawLine(int x1, int y1, int x2, int y2) {
        // Render the shape with the internal graphics object
        internalGraphics.drawLine(x1, y1, x2, y2);
    }

    public void drawRect(int x, int y, int width, int height) {
        // Render the shape with the internal graphics object
        internalGraphics.drawRect(x, y, width, height);
    }

    public void fillRect(int x, int y, int width, int height) {
        // Render the shape with the internal graphics object
        internalGraphics.fillRect(x, y, width, height);
    }

    public void drawEllipse(int x, int y, int width, int height) {
        // Render the shape with the internal graphics object
        internalGraphics.drawOval(x, y, width, height);
    }

    public void fillEllipse(int x, int y, int width, int height) {
        // Render the shape with the internal graphics object
        internalGraphics.fillOval(x, y, width, height);
    }

    public void drawString(String string, int x, int y) {
        // Render the string with the internal graphics object
        internalGraphics.drawString(string, x, y);
    }

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
            runnable.draw(new UIGraphics(copyGraphics));
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
}
