package com.github.chrisblutz.breadboard.ui.toolkit.builtin.spacing;

import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers.UIFlexContainer;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

/**
 * A {@code UISpacer} is an invisible component that exists to take up space in a layout.  It has a minimum size
 * in a chosen direction (either horizontally or vertically), and no minimum size in the other direction.  Spacers
 * are useful in directional layouts to provide space between two components.
 * <p>
 * If used in a container that allows components to grow to fill space (e.g., a {@link UIFlexContainer}), spacers with
 * a weight greater than 0 will grow automatically to fill their available space.  This means they can be used to add
 * variable separation between components (e.g., splitting components between left and right alignment).
 */
public class UISpacer extends UIComponent {

    // Direction that the spacer will take up space in
    private final Direction direction;
    // Minimum width of the spacer (it may grow larger than this, e.g., in a flex container with weight > 0)
    private int spacerWidth;

    /**
     * Creates a new {@code UISpacer} with 0 width in the specified direction.  This style of spacer is generally
     * only useful in places where the spacer is expected to grow to fill space (e.g., in a
     * {@link UIFlexContainer}).  If this style of spacer is placed into a container that does not allow it to fill
     * space, this spacer will be functionally useless.
     * <p>
     * The spacer has no minimum dimension in the opposite direction provided, so a horizontal spacer will have no
     * vertical minimum height, and vice versa.
     *
     * @param direction the {@link Direction} the spacer should take up space in
     */
    public UISpacer(Direction direction) {
        this(direction, 0);
    }

    /**
     * Creates a new {@code UISpacer} with the specified width in the specified direction.  The width passed to this
     * constructor is a <i>minimum</i> width, so the spacer will grow if placed in a container that allows it to grow
     * to fill space (e.g., a {@link UIFlexContainer}).  Negative widths are not allowed, and will be treated as a
     * width of {@code 0}.
     * <p>
     * The spacer has no minimum dimension in the opposite direction provided, so a horizontal spacer will have no
     * vertical minimum height, and vice versa.
     *
     * @param direction   the {@link Direction} the spacer should take up space in
     * @param spacerWidth the minimum width to assign to the spacer
     */
    public UISpacer(Direction direction, int spacerWidth) {
        this.direction = direction;
        // Clamp minimum width to be at least 0
        if (spacerWidth < 0)
            spacerWidth = 0;
        this.spacerWidth = spacerWidth;

        calculateMinimumSize();
    }

    /**
     * Gets the direction this spacer takes up space in.  This is the direction where the minimum width selected either
     * by the constructor or by {@link #setSpacerWidth(int)} applies.  The other direction does not have a minimum
     * width.
     *
     * @return The {@link Direction} this spacer takes up space in
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the minimum width assigned to this spacer.  In layouts that respect component minimum widths, this spacer
     * will take up <i>at least</i> this much space in the direction selected.  If the spacer is used in a layout that
     * allows components to grow past their minimum sizes (e.g., a {@link UIFlexContainer}), the spacer will fill the
     * space available.  The width of a spacer can never be negative.
     *
     * @return The minimum width assigned to this spacer
     */
    public int getSpacerWidth() {
        return spacerWidth;
    }

    /**
     * Sets the minimum width assigned to this spacer.  In layouts that respect component minimum widths, this spacer
     * will take up <i>at least</i> this much space in the direction selected.  If the spacer is used in a layout that
     * allows components to grow past their minimum sizes (e.g., a {@link UIFlexContainer}), the spacer will fill the
     * space available.  The width of a spacer may not be negative, and any negative values passed to this method will
     * be treated as widths of {@code 0}.
     *
     * @param spacerWidth the minimum width to assign to this spacer
     */
    public void setSpacerWidth(int spacerWidth) {
        // Clamp spacer width to be at least 0
        if (spacerWidth < 0)
            spacerWidth = 0;
        this.spacerWidth = spacerWidth;
        calculateMinimumSize();
    }


    /**
     * Calculates the minimum size for the spacer based on the selected minimum width
     */
    private void calculateMinimumSize() {
        // Set the minimum dimension in the primary direction
        setMinimumSize(new UIDimension(
                direction == Direction.VERTICAL ? 0 : this.spacerWidth,
                direction == Direction.VERTICAL ? this.spacerWidth : 0
        ));
    }

    @Override
    public void render(UIGraphics graphics) { /* do nothing */ }
}
