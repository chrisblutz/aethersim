package com.github.chrisblutz.breadboard.ui.toolkit.shape;

public class Ellipse implements Shape {

    private final double x1, x2, y1, y2, focusX1, focusY1, focusX2, focusY2;
    private final double width, height, focusDistanceSum;

    public Ellipse(double x, double y, double width, double height) {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
        this.width = width;
        this.height = height;
        // Calculate other values we need for ellipses
        double radiusX = width / 2;
        double radiusY = height / 2;
        double centerX = x + (width / 2);
        double centerY = y + (height / 2);
        // Using the above data, calculate the foci for the ellipse
        double maxRadius = Math.max(radiusX, radiusY);
        double minRadius = Math.min(radiusX, radiusY);
        double distanceToFocus = Math.sqrt((maxRadius * maxRadius) - (minRadius * minRadius));
        focusDistanceSum = maxRadius + (maxRadius - distanceToFocus);
        // Set the focus points, depending on whether this is a horizontal or vertical ellipse
        if (width > height) {
            focusX1 = centerX - distanceToFocus;
            focusY1 = centerY;
            focusX2 = centerX + distanceToFocus;
            focusY2 = centerY;
        } else {
            focusX1 = centerX;
            focusY1 = centerY - distanceToFocus;
            focusX2 = centerX;
            focusY2 = centerY + distanceToFocus;
        }
    }

    public double getX() {
        return x1;
    }

    public double getY() {
        return y1;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public boolean contains(double x, double y) {
        if ((x >= x1) && (x <= x2) && (y >= y1) && (y <= y2)) {
            // If the point is inside the rectangular bounding box for the ellipse, check if it is inside the
            // ellipse itself, using the knowledge that the sum of the distances to the foci of the ellipse is
            // constant at every point on the ellipse.
            double distanceFocusX1 = x - focusX1;
            double distanceFocusY1 = y - focusY1;
            double distanceToFocus1 = Math.sqrt((distanceFocusX1 * distanceFocusX1) + (distanceFocusY1 * distanceFocusY1));
            double distanceFocusX2 = x - focusX2;
            double distanceFocusY2 = y - focusY2;
            double distanceToFocus2 = Math.sqrt((distanceFocusX2 * distanceFocusX2) + (distanceFocusY2 * distanceFocusY2));
            double combinedDistance = distanceToFocus1 + distanceToFocus2;
            return combinedDistance <= focusDistanceSum;
        } else {
            return false;
        }
    }

    @Override
    public Shape translate(double x, double y) {
        return new Ellipse(x1 + x, y1 + y, width, height);
    }

    @Override
    public Shape scale(double scale) {
        return new Ellipse(x1 * scale, y1 * scale, width * scale, height * scale);
    }
}
