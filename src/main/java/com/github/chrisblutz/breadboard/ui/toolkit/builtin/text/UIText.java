package com.github.chrisblutz.breadboard.ui.toolkit.builtin.text;

import com.github.chrisblutz.breadboard.ui.toolkit.UIComponent;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;


public class UIText extends UIComponent {

    private static final int PADDING = 5;

    private String text;
    private UIDimension textBounds;
    private Font font = UITheme.getFont(ThemeKeys.Fonts.UI.TEXT_DEFAULT);

    public UIText(String text) {
        this.text = text;
        setMinimumSizeFromText();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setMinimumSizeFromText();
    }

    private void setMinimumSizeFromText() {
        textBounds = getGraphicsContext().getStringBounds(font, text);
        setMinimumSize(textBounds.add(PADDING * 2, PADDING * 2));
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw the string vertically-centered and left-aligned
        graphics.getInternalGraphics().setFont(font);
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND));
        FontMetrics metrics = graphics.getInternalGraphics().getFontMetrics();
        graphics.drawString(getText(), PADDING, (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));
    }
}
