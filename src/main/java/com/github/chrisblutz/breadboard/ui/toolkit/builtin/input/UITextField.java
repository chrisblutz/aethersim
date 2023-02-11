package com.github.chrisblutz.breadboard.ui.toolkit.builtin.input;

import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.listeners.OnChangeListener;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Padding;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class UITextField extends UIComponent implements UIInteractable, UIFocusable {

    private class Text {

        private final List<Character> text = new ArrayList<>();

        private int cursorPosition = 0;

        private int selectionStartPosition = 0;
        private int selectionCursorPosition = 0;

        private void add(char c) {
            // Check that the character passes the validation regex, if it exists
            if (isValidInsert(getCursorPosition(), c)) {
                text.add(getCursorPosition(), c);
                moveCursor(getCursorPosition() + 1);
            }

            // Fire onTextChange listener with updated text
            if (getOnTextChangeListener() != null)
                getOnTextChangeListener().onChange(getText());
        }

        private void add(String s) {
            // Add all characters from the string
            for (char c : s.toCharArray()) {
                // Check that the character passes the validation regex, if it exists
                if (isValidInsert(getCursorPosition(), c)) {
                    text.add(getCursorPosition(), c);
                    moveCursor(getCursorPosition() + 1);
                }
            }

            // Fire onTextChange listener with updated text
            if (getOnTextChangeListener() != null)
                getOnTextChangeListener().onChange(getText());
        }

        private void remove() {
            text.remove(getCursorPosition());

            // Fire onTextChange listener with updated text
            if (getOnTextChangeListener() != null)
                getOnTextChangeListener().onChange(getText());
        }

        private void removeSelected() {
            // Remove the characters within the selection bounds
            int index = Math.min(selectionStartPosition, selectionCursorPosition);
            int endIndex = Math.max(selectionStartPosition, selectionCursorPosition);
            while (endIndex > index) {
                text.remove(index);
                endIndex--;
            }
            moveCursor(index);

            // Fire onTextChange listener with updated text
            if (getOnTextChangeListener() != null)
                getOnTextChangeListener().onChange(getText());
        }

        private void clear() {
            text.clear();
            // Don't fire the change listener here, as this is used by setText(), which causes
            // a StackOverflow if it calls the change here
        }

        private void copySelectedToClipboard() {
            StringSelection selectedText = new StringSelection(getSelectedText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedText, selectedText);
        }

        private void pasteSelectedFromClipboard() {
            try {
                String clipboardData = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                add(clipboardData);
            } catch (Exception e) { /* do nothing */ }
        }

        private boolean isEmpty() {
            return text.isEmpty();
        }

        private int length() {
            return text.size();
        }

        private String getText() {
            return getTextToIndex(length());
        }

        private String getTextToCursor() {
            return getTextToIndex(getCursorPosition());
        }

        private boolean isValidInsert(int index, char c) {
            // If there is no validation regex, skip
            if (validationRegex == null || validationRegex.isEmpty())
                return true;

            char[] charArray = new char[length() + 1];
            int arrayIndex = 0;
            for (int listIndex = 0; listIndex < length(); listIndex++) {
                // If we're at the insert index, add the character
                if (arrayIndex == index)
                    charArray[arrayIndex++] = c;
                charArray[arrayIndex++] = text.get(listIndex);
            }
            // Do one last check to catch the addition if we're adding at the end of the string
            if (arrayIndex == index)
                charArray[arrayIndex] = c;
            String proposedValue = new String(charArray);

            // Check that the validation regex matches
            return proposedValue.matches(validationRegex);
        }

        private String getTextToIndex(int index) {
            char[] charArray = new char[index];
            for (int listIndex = 0; listIndex < index; listIndex++)
                charArray[listIndex] = text.get(listIndex);
            return new String(charArray);
        }

        private String getSelectedText() {
            int initialPosition = Math.min(selectionStartPosition, selectionCursorPosition);
            int endPosition = Math.max(selectionStartPosition, selectionCursorPosition);
            char[] charArray = new char[endPosition - initialPosition];
            for (int index = 0; index < charArray.length; index++)
                charArray[index] = text.get(index + initialPosition);
            return new String(charArray);
        }

        private boolean isTextSelected() {
            return selectionStartPosition != selectionCursorPosition;
        }

        public int getCursorPosition() {
            return cursorPosition;
        }

        public int getSelectionStartPosition() {
            return selectionStartPosition;
        }

        public int getSelectionCursorPosition() {
            return selectionCursorPosition;
        }

        private void moveCursor(int cursorPosition) {
            this.cursorPosition = cursorPosition;
            this.selectionStartPosition = cursorPosition;
            this.selectionCursorPosition = cursorPosition;
        }

        private void dragCursor(int cursorPosition) {
            this.cursorPosition = cursorPosition;
            this.selectionCursorPosition = cursorPosition;
        }
    }

    protected Padding padding = new Padding(8, 8, 8, 8);

    private static final int CURSOR_LIMIT_OFFSET = 8, BLINK_DURATION_RENDER_PASSES = 30;

    private String validationRegex = null;

    private OnChangeListener<String> onTextChangeListener;

    private String placeholderText;
    private final Text text = new Text();
    private int textOffsetX = 0;

    private boolean selecting = false;
    private int blinkCounter = 0;
    private boolean blinkOn = true;

    public UITextField(OnChangeListener<String> onTextChangeListener) {
        this(null, onTextChangeListener);
    }

    public UITextField(String placeholderText, OnChangeListener<String> onTextChangeListener) {
        this.placeholderText = placeholderText;
        this.onTextChangeListener = onTextChangeListener;

        // TODO
        setMinimumSize(new UIDimension(200, 40));
    }

    public OnChangeListener<String> getOnTextChangeListener() {
        return onTextChangeListener;
    }

    public void setOnTextChangeListener(OnChangeListener<String> onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public Padding getPadding() {
        return padding;
    }

    protected String getValidationRegex() {
        return validationRegex;
    }

    protected void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
        positionTextForCursorVisibility();
    }

    @Override
    public void setSize(UIDimension size) {
        super.setSize(size);
        // When the size gets set, position the cursor
        positionTextForCursorVisibility();
    }

    @Override
    public void render(UIGraphics graphics) {
        // Draw background
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_BACKGROUND));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        // Set font so we can do calculations with it
        graphics.getInternalGraphics().setFont(new Font("Montserrat", Font.PLAIN, 20));

        // While drawing the text, create a new graphics object, so we can apply a clip
        graphics.withCopy(textGraphics -> {
            FontMetrics metrics = textGraphics.getInternalGraphics().getFontMetrics();
            if (metrics == null)
                metrics = textGraphics.getInternalGraphics().getFontMetrics();

            textGraphics.clip(
                    padding.getPaddingLeft(),
                    padding.getPaddingTop(),
                    getWidth() - (padding.getPaddingLeft() + padding.getPaddingRight()),
                    getHeight() - (padding.getPaddingTop() + padding.getPaddingBottom())
            );

            // Draw the placeholder text, if there is no actual text and the component isn't focused.
            // Otherwise, render the actual text
            if (!isFocused() && text.isEmpty() && placeholderText != null) {
                textGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND_PLACEHOLDER));
                textGraphics.getInternalGraphics().setFont(new Font("Montserrat", Font.PLAIN, 20));
                textGraphics.drawString(placeholderText, padding.getPaddingLeft(), (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));
            }

            // If the component has text, draw it
            if (!text.isEmpty()) {
                textGraphics.getInternalGraphics().setFont(new Font("Montserrat", Font.PLAIN, 20));
                textGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND));
                textGraphics.drawString(text.getTextToIndex(text.length()), padding.getPaddingLeft() - textOffsetX, (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));
            }

            // If there is a selection being made, draw the selection box.  Otherwise, draw the cursor (unless we're in the blinking off state).
            if (text.isTextSelected()) {
                int startX = metrics.stringWidth(text.getTextToIndex(text.getSelectionStartPosition()));
                int dragX = metrics.stringWidth(text.getTextToIndex(text.getSelectionCursorPosition()));
                int minX = Math.min(startX, dragX);
                int maxX = Math.max(startX, dragX);

                textGraphics.getInternalGraphics().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
                textGraphics.fillRect(padding.getPaddingLeft() + minX - textOffsetX, padding.getPaddingTop(), maxX - minX, getHeight() - (padding.getPaddingTop() + padding.getPaddingBottom()));
            } else if (isFocused() && blinkOn) {
                // Calculate length of string to cursor
                int cursorOffsetX = 0;
                if (text.getCursorPosition() > 0 && !text.isEmpty())
                    cursorOffsetX = graphics.getInternalGraphics().getFontMetrics().stringWidth(text.getTextToCursor());
                textGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND_PLACEHOLDER));
                textGraphics.fillRect(padding.getPaddingLeft() + cursorOffsetX - textOffsetX, padding.getPaddingTop(), 2, getHeight() - (padding.getPaddingTop() + padding.getPaddingBottom()));
            }
        });

        // Draw border
        graphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.BORDER_PRIMARY));
        graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String text) {
        this.text.moveCursor(0);
        this.text.clear();
        this.text.moveCursor(Math.min(this.text.getCursorPosition(), this.text.length()));
        this.text.add(text);
        positionTextForCursorVisibility();
    }

    private void positionTextForCursorVisibility() {
        int fullWidth = getGraphicsContext().getStringBounds(new Font("Montserrat", Font.PLAIN, 20), text.getText()).getWidth();
        String textToCursor = text.getTextToCursor();
        int cursorLocationX = getGraphicsContext().getStringBounds(new Font("Montserrat", Font.PLAIN, 20), textToCursor).getWidth();
        // If the full text fits in the text box, set the offset to 0
        // Otherwise, calculate the offset to match the previous edit position
        if (fullWidth < getWidth() - (padding.getPaddingLeft() + padding.getPaddingRight()) - CURSOR_LIMIT_OFFSET)
            textOffsetX = 0;

        // If the current location of the cursor is beyond the end,
        if ((cursorLocationX - textOffsetX) > (getWidth() - (padding.getPaddingLeft() + padding.getPaddingRight()) - CURSOR_LIMIT_OFFSET)) {
            textOffsetX = cursorLocationX - (getWidth() - (padding.getPaddingLeft() + padding.getPaddingRight()) - CURSOR_LIMIT_OFFSET);
        } else if ((cursorLocationX - textOffsetX) < CURSOR_LIMIT_OFFSET && text.getCursorPosition() > 0) {
            textOffsetX = cursorLocationX - CURSOR_LIMIT_OFFSET;
        } else if (text.getCursorPosition() == 0) {
            textOffsetX = 0;
        }

        if (textOffsetX < 0)
            textOffsetX = 0;
    }

    private int getPositionAtPoint(int x, int y) {
        // Iterate over characters to determine which the point is
        // closest to
        int previousDistance = Integer.MAX_VALUE;
        for (int index = 0; index <= text.length(); index++) {
            int lengthToIndex = getGraphicsContext().getStringBounds(new Font("Montserrat", Font.PLAIN, 20), text.getTextToIndex(index)).getWidth();
            int indexLocationX = lengthToIndex + padding.getPaddingLeft() - textOffsetX;
            int distance = Math.abs(x - indexLocationX);

            // If this distance is smaller, store it.
            // If this distance is bigger, we've passed the closest point, so return it
            if (distance < previousDistance) {
                previousDistance = distance;
            } else {
                return index - 1;
            }
        }

        // If we get here, we didn't find an index, so return the last position.
        return text.length();
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        return true;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        if (button == 1) {
//            selectionStartPosition = getPositionAtPoint(x, y);
//            selectionCursorPosition = selectionStartPosition;
            text.moveCursor(getPositionAtPoint(x, y));
            selecting = true;
        }
        return true;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        if (button == 1)
            selecting = false;

        // Redraw the text box
        positionTextForCursorVisibility();
    }

    @Override
    public void onMouseEntered() {}

    @Override
    public void onMouseExited() {}

    @Override
    public void onMouseDragged(int x, int y) {
        if (selecting)
            text.dragCursor(getPositionAtPoint(x, y));

        // Redraw the text box
        positionTextForCursorVisibility();
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return true;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        return false;
    }

    private boolean isPrintableCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return !Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED && block != null && block != Character.UnicodeBlock.SPECIALS;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        // If this is an invalid character, return
        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED || !isPrintableCharacter(e.getKeyChar()))
            return false;

        // Add the specified character to the string
        text.add(e.getKeyChar());

        // Redraw the text box
        positionTextForCursorVisibility();

        return true;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        // Handle backspaces, deletes, and other control characters
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            // If we have selected text, remove it.
            // Otherwise, if there is a character behind the cursor, delete one
            if (text.isTextSelected()) {
                text.removeSelected();
            } else if (text.getCursorPosition() > 0) {
                text.moveCursor(text.getCursorPosition() - 1);
                text.remove();
            }
            return true;
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            // If we have selected text, remove it
            // Otherwise, if there is a character in front of the cursor, delete one
            if (text.isTextSelected())
                text.removeSelected();
            else if (text.getCursorPosition() < text.length())
                text.remove();
            return true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // If there are characters to the left of the cursor, move left
            if (text.getCursorPosition() > 0) {
                if (e.isShiftDown())
                    text.dragCursor(text.getCursorPosition() - 1);
                else if (text.isTextSelected())
                    text.moveCursor(Math.min(text.getSelectionStartPosition(), text.getSelectionCursorPosition()));
                else
                    text.moveCursor(text.getCursorPosition() - 1);
            } else if (!e.isShiftDown() && text.getCursorPosition() == 0 && text.isTextSelected()) {
                text.moveCursor(0);
            }
            return true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // If there are characters to the right of the cursor, move right
            if (text.getCursorPosition() < text.length()) {
                if (e.isShiftDown())
                    text.dragCursor(text.getCursorPosition() + 1);
                else if (text.isTextSelected())
                    text.moveCursor(Math.max(text.getSelectionStartPosition(), text.getSelectionCursorPosition()));
                else
                    text.moveCursor(text.getCursorPosition() + 1);
            } else if (!e.isShiftDown() && text.getCursorPosition() == text.length() && text.isTextSelected()) {
                text.moveCursor(text.length());
            }
            return true;
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C && text.isTextSelected()) {
            text.copySelectedToClipboard();
            return true;
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X && text.isTextSelected()) {
            text.copySelectedToClipboard();
            text.removeSelected();
            return true;
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
            if (text.isTextSelected())
                text.removeSelected();
            text.pasteSelectedFromClipboard();
            return true;
        }

        // Redraw the text box
        positionTextForCursorVisibility();

        return false;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) { return false; /* TODO */ }

    @Override
    public void onFocusGained(boolean keyboardTriggered) {
        // If we've just gained focus, reset the blink counter
        blinkCounter = 0;
        blinkOn = true;
    }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {}

    @Override
    public void onRenderPass() {
        if (isFocused()) {
            blinkCounter++;

            if (blinkCounter >= BLINK_DURATION_RENDER_PASSES) {
                blinkCounter = 0;
                blinkOn = !blinkOn;
            }
        }
    }
}
