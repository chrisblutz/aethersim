package com.aethersim.ui.toolkit.builtin.input;

import com.aethersim.ui.toolkit.*;
import com.aethersim.ui.toolkit.builtin.listeners.OnClickListener;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;
import com.aethersim.ui.toolkit.layout.Padding;
import com.aethersim.ui.toolkit.layout.UIDimension;

import java.awt.*;
import java.awt.event.KeyEvent;

public class UIButton extends UIComponent implements UIInteractable, UIFocusable {

    // Track button state
    protected boolean hovered = false; // TODO private
    protected boolean pressed = false; // TODO private

    private OnClickListener onClickListener;
    protected String text; // TODO private
    private Padding padding = new Padding(5, 5, 5, 5); // TODO defaults
    protected UIFont font = UITheme.getFont(ThemeKeys.Fonts.UI.BUTTON_DEFAULT); // TODO private

    public UIButton(String text, OnClickListener onClickListener) {
        this.text = text;
        this.onClickListener = onClickListener;

        calculateMinimumSize();
    }

    public UIFont getFont() {
        return font;
    }

    public void setFont(UIFont font) {
        this.font = font;
        calculateMinimumSize();
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
        calculateMinimumSize();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        calculateMinimumSize();
    }

    protected void calculateMinimumSize() {
        UIDimension textBounds = getGraphicsContext().getStringBounds(getFont().getInternalFont(1f), getText());
        setMinimumSize(textBounds.add(
                padding.getPaddingLeft() + padding.getPaddingRight(),
                padding.getPaddingTop() + padding.getPaddingBottom()
        ));
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw the background of the button
        if (pressed)
            graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BUTTON_BACKGROUND_PRESSED));
        else if (hovered)
            graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BUTTON_BACKGROUND_HOVERED));
        else
            graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BUTTON_BACKGROUND));

        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Draw text
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND));
        graphics.setFont(getFont());
        FontMetrics metrics = graphics.getInternalGraphics().getFontMetrics();
        graphics.drawString(getText(), (getWidth() / 2) - (metrics.stringWidth(getText()) / 2), (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));

        // If the button is focused, draw the focus border
        if (isFocused()) {
            graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BUTTON_BORDER_FOCUSED));
            graphics.setStroke(UIStroke.dashed(1, UIStroke.Cap.BUTT, UIStroke.Join.ROUND, new float[] {1, 2}));
            graphics.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
        }
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        // Consume click events, even though we don't do anything here
        return true;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        // Only fire on left mouse clicks, but consume all clicks
        if (button == 1)
            pressed = true;
        return true;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        // Only fire on left mouse clicks
        if (button == 1) {
            // Once the mouse is released, call the onClick listener
            if (pressed && onClickListener != null)
                onClickListener.onClick();
            pressed = false;
        }
    }

    @Override
    public void onMouseEntered() {
        // Mark as hovered when the mouse enters the button
        hovered = true;
    }

    @Override
    public void onMouseExited() {
        // When the mouse exits, mark it as not hovered and not pressed.
        // Setting it as not pressed avoids issues where a button "clicks"
        // after the mouse is not hovering over it anymore.
        hovered = false;
        pressed = false;
    }

    @Override
    public void onMouseDragged(int x, int y) {}

    @Override
    public boolean onMouseMoved(int x, int y) { return true; }

    @Override
    public boolean onMouseScrolled(int scrollAmount) { return false; }

    @Override
    public boolean onKeyTyped(KeyEvent e) { return false; }

    @Override
    public boolean onKeyPressed(KeyEvent e) { return false; }

    @Override
    public boolean onKeyReleased(KeyEvent e) { return false; }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {}

    @Override
    public void onFocusGained(boolean keyboardTriggered) {}
}
