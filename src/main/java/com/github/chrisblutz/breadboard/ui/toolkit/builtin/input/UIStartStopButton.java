package com.github.chrisblutz.breadboard.ui.toolkit.builtin.input;

import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.listeners.OnClickListener;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;

public class UIStartStopButton extends UIButton {

    private static final String startText = "\uE037";
    private static final String stopText = "\uE047";

    private boolean started = false;

    public UIStartStopButton(OnClickListener startListener, OnClickListener stopListener) {
        // Initialize with a null listener
        super("", null);
        // Update the listener to handle starting and stopping
        setOnClickListener(() -> {
            // Toggle the started boolean then call the appropriate listener
            started = !started;
            if (started)
                startListener.onClick();
            else
                stopListener.onClick();
        });

        //setPaddingX(8); // TODO set to 8 on X
        setFont(UITheme.getFont(ThemeKeys.Fonts.UI.START_STOP_BUTTON_DEFAULT));
    }

    @Override
    protected void calculateMinimumSize() {
        UIDimension startTextBounds = getGraphicsContext().getStringBounds(font, startText);
        UIDimension stopTextBounds = getGraphicsContext().getStringBounds(font, stopText);
        UIDimension maxBounds = new UIDimension(
                Math.max(startTextBounds.getWidth(), stopTextBounds.getWidth()) + (getPadding().getPaddingLeft() + getPadding().getPaddingLeft()),
                Math.max(startTextBounds.getHeight(), stopTextBounds.getHeight()) + (getPadding().getPaddingTop() + getPadding().getPaddingBottom())
        );
        setMinimumSize(maxBounds);
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw the background of the button
        if (pressed)
            graphics.setColor(UITheme.getColor(
                    started ?
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STARTED_PRESSED :
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STOPPED_PRESSED
            ));
        else if (hovered)
            graphics.setColor(UITheme.getColor(
                    started ?
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STARTED_HOVERED :
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STOPPED_HOVERED
            ));
        else
            graphics.setColor(UITheme.getColor(
                    started ?
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STARTED :
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BACKGROUND_STOPPED
            ));

        graphics.getInternalGraphics().fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Draw text
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND));
        graphics.getInternalGraphics().setFont(font);
        FontMetrics metrics = graphics.getInternalGraphics().getFontMetrics();
        String text = started ? stopText : startText;
        graphics.drawString(text, (getWidth() / 2) - (metrics.stringWidth(text) / 2), (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));

        // If the button is focused, draw the focus border
        if (isFocused()) {
            graphics.setColor(UITheme.getColor(
                    started ?
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BORDER_STARTED_FOCUSED :
                    ThemeKeys.Colors.UI.START_STOP_BUTTON_BORDER_STOPPED_FOCUSED
            ));
            graphics.getInternalGraphics().setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] {1, 2}, 0));
            graphics.getInternalGraphics().drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
        }
    }
}
