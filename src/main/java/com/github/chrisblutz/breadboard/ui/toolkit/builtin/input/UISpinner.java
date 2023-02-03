package com.github.chrisblutz.breadboard.ui.toolkit.builtin.input;

import com.github.chrisblutz.breadboard.ui.toolkit.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.listeners.OnChangeListener;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.exceptions.UIToolkitException;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Padding;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * A {@code UISpinner} is a combination of a {@link UITextField} and two {@link UIButton}s that allows users
 * to select a scalar value.  Spinners can have minimum and maximum values that prevent users from exceeding
 * preset boundary values (e.g., keeping values positive). They can also have default values that determine
 * what value the spinner has before any user input.
 * <p>
 * The handler passed to the spinner defines how the spinner treats the values it operates on, which means that
 * custom types can be supported by writing a {@link SpinnerHandler} for the type.
 * <p>
 * In addition to displaying their current value, spinners can be configured to display a "unit" string that
 * indicates the units of the value the spinner is displaying (e.g., if a quantity represents a number of seconds,
 * the units string could be set to {@code "seconds"} to indicate that).
 *
 * @param <T> the type of value this spinner produces
 */
public class UISpinner<T> extends UIComponent implements UIInteractable, UIFocusable {

    // Represents the text field in the spinner.  Its isFocused() method is overridden to allow the internal
    // focus functionality we need for the spinner focus.
    private class UISpinnerTextField extends UIValidatedTextField {

        // Creates a new validated text field using the constructor without a placeholder
        private UISpinnerTextField(String validationRegex, OnChangeListener<String> onTextChangeListener) {
            super(validationRegex, onTextChangeListener);
        }

        @Override
        public boolean isFocused() {
            return textFieldFocused;
        }

        @Override
        public void onFocusLost(boolean keyboardTriggered) {
            // Call the superclass implementation, then call the spinner-specific functionality
            super.onFocusLost(keyboardTriggered);
            validateTextFieldContents();
        }
    }

    // Defines the default padding for the text field and buttons
    private static final int DEFAULT_PADDING = 4; // TODO Theme integration

    // Built-in handler for integer values in a spinner
    private static SpinnerHandler<Integer> integerHandler;

    // Internal text field used to display the value of the spinner
    private UIValidatedTextField spinnerTextField;
    // Internal button objects used for the increment/decrement buttons
    private final UIButton decrementButton, incrementButton;

    // These variables track the hover and press states of the various components that make up the spinner
    private boolean decrementHovered = false, incrementHovered = false, textFieldHovered = false;
    private boolean decrementPressed = false, incrementPressed = false;
    private boolean textFieldFocused = false;

    // Padding for the text field that also determines space around the buttons
    private Padding padding = new Padding(DEFAULT_PADDING);

    // Handler for determining how the spinner handles values of a specific type
    private final SpinnerHandler<T> handler;
    // String defining the "units" of the value of the spinner, displayed on the text field
    private String unitsString;
    // Listener for changes in the value of the spinner
    private OnChangeListener<T> onValueChangeListener;
    // Minimum and maximum values of the spinner, or null if not set
    private T minimumValue, maximumValue;
    // Current value of the spinner, and the value that was last used for the change listener
    private T lastNotifiedValue, value;

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the handler also determines the default value, minimum value, and maximum value
     * for the spinner.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, OnChangeListener<T> onValueChangeListener) {
        this(handler, handler.getDefaultMinimum(), handler.getDefaultMaximum(), onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the default value is provided, but the handler provides the minimum and maximum
     * values for the spinner.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param defaultValue          the default value of the spinner before user input
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, T defaultValue, OnChangeListener<T> onValueChangeListener) {
        this(handler, handler.getDefaultMinimum(), handler.getDefaultMaximum(), defaultValue, onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the minimum and maximum values are provided, but the handler provides default
     * value for the spinner.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param minimumValue          the minimum value allowed by the spinner
     * @param maximumValue          the maximum value allowed by the spinner
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, T minimumValue, T maximumValue, OnChangeListener<T> onValueChangeListener) {
        this(handler, minimumValue, maximumValue, handler.getDefaultValue(minimumValue, maximumValue), onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the default value, minimum value, and maximum value are all provided.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param minimumValue          the minimum value allowed by the spinner
     * @param maximumValue          the maximum value allowed by the spinner
     * @param defaultValue          the default value of the spinner before user input
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, T minimumValue, T maximumValue, T defaultValue, OnChangeListener<T> onValueChangeListener) {
        this(handler, null, minimumValue, maximumValue, defaultValue, onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the handler also determines the default value, minimum value, and maximum value
     * for the spinner.  The unit string, which is displayed on the text field, is provided.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param unitsString           the {@link String} that represents the unit of the value of the spinner
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, String unitsString, OnChangeListener<T> onValueChangeListener) {
        this(handler, unitsString, handler.getDefaultMinimum(), handler.getDefaultMaximum(), onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the default value is provided, but the handler provides the minimum and maximum
     * values for the spinner.  The unit string, which is displayed on the text field, is provided.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param unitsString           the {@link String} that represents the unit of the value of the spinner
     * @param defaultValue          the default value of the spinner before user input
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, String unitsString, T defaultValue, OnChangeListener<T> onValueChangeListener) {
        this(handler, unitsString, handler.getDefaultMinimum(), handler.getDefaultMaximum(), defaultValue, onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the minimum and maximum values are provided, but the handler provides default
     * value for the spinner.  The unit string, which is displayed on the text field, is provided.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param unitsString           the {@link String} that represents the unit of the value of the spinner
     * @param minimumValue          the minimum value allowed by the spinner
     * @param maximumValue          the maximum value allowed by the spinner
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, String unitsString, T minimumValue, T maximumValue, OnChangeListener<T> onValueChangeListener) {
        this(handler, unitsString, minimumValue, maximumValue, handler.getDefaultValue(minimumValue, maximumValue), onValueChangeListener);
    }

    /**
     * Creates a new {@code UISpinner} that allows users to select values either using a text field or using
     * increment and decrement buttons.  The handler passed to this constructor determines how the spinner
     * handles the type it is assigned.  The listener passed to this constructor is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * For this constructor, the default value, minimum value, and maximum value are all provided.  The unit string,
     * which is displayed on the text field, is provided.
     *
     * @param handler               the {@link SpinnerHandler} that determines how values are handled by this spinner
     * @param unitsString           the {@link String} that represents the unit of the value of the spinner
     * @param minimumValue          the minimum value allowed by the spinner
     * @param maximumValue          the maximum value allowed by the spinner
     * @param defaultValue          the default value of the spinner before user input
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public UISpinner(SpinnerHandler<T> handler, String unitsString, T minimumValue, T maximumValue, T defaultValue, OnChangeListener<T> onValueChangeListener) {
        this.handler = handler;
        this.onValueChangeListener = onValueChangeListener;
        this.unitsString = unitsString;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.value = defaultValue;

        // Make sure that the value is within the minimum/maximum bounds
        if (getMinimumValue() != null && handler.compare(value, minimumValue) < 0)
            value = minimumValue;
        else if (getMaximumValue() != null && handler.compare(value, maximumValue) > 0)
            value = maximumValue;

        // Set the last notified value to the default value
        this.lastNotifiedValue = value;

        this.spinnerTextField = new UISpinnerTextField(handler.getValidationRegex(), newValue -> {
            // Parse out the value for the string, and use the default if the string is empty
            T value;
            if (newValue.isEmpty())
                value = handler.getDefaultValue(getMinimumValue(), getMaximumValue());
            else
                value = handler.getValue(newValue);

            // Make sure we got a valid result, and if not, reset the string and return
            if (value == null) {
                String currentValueString = handler.getString(getValue());
                spinnerTextField.setText(currentValueString);
                return;
            }

            // Set the value to its minimums and maximums if necessary, and determine if visual clamping is required
            boolean clampRequired = false;
            if (getMinimumValue() != null && handler.compare(value, getMinimumValue()) < 0) {
                value = getMinimumValue();
                if (handler.getClampToMinimum(getMinimumValue()))
                    clampRequired = true;
            } else if (getMaximumValue() != null && handler.compare(value, getMaximumValue()) > 0) {
                value = getMaximumValue();
                if (handler.getClampToMaximum(getMaximumValue()))
                    clampRequired = true;
            }

            // If visual clamping is required, reset the string and return
            // Otherwise, set the value of the spinner
            if (clampRequired) {
                String currentValueString = handler.getString(getValue());
                spinnerTextField.setText(currentValueString);
            } else {
                UISpinner.this.value = value;
            }
        });
        spinnerTextField.setText(handler.getString(value));

        this.decrementButton = new UIButton("\uE15B", () -> {
            // If the value is greater than the minimum, decrement and notify
            if (getMinimumValue() != null && handler.compare(value, getMinimumValue()) > 0) {
                value = handler.decrement(value);
                // Set the text of the text field to the string representation
                spinnerTextField.setText(handler.getString(value));

                // If the value was not previously notified, notify now
                if (onValueChangeListener != null && value != lastNotifiedValue) {
                    lastNotifiedValue = value;
                    onValueChangeListener.onChange(value);
                }
            }
        });
        decrementButton.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.SPINNER_BUTTON_DEFAULT));

        this.incrementButton = new UIButton("\uE145", () -> {
            // If the value is less than the maximum, increment and notify
            if (getMaximumValue() != null && handler.compare(value, getMaximumValue()) < 0) {
                value = handler.increment(value);
                // Set the text of the text field to the string representation
                spinnerTextField.setText(handler.getString(value));

                // If the value was not previously notified, notify now
                if (onValueChangeListener != null && value != lastNotifiedValue) {
                    lastNotifiedValue = value;
                    onValueChangeListener.onChange(value);
                }
            }
        });
        incrementButton.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.SPINNER_BUTTON_DEFAULT));

        calculateMinimumSize();
    }

    /**
     * Gets the minimum value allowed by this spinner.  Any value below this value as determined by
     * {@link SpinnerHandler#compare(Object, Object)} will be rejected in the text field, and the decrement button
     * will be unable to lower a value beyond this point.
     * <p>
     * A {@code null} value returned from this method indicates the spinner does not have a minimum value set.
     *
     * @return The minimum value allowed by this spinner, or {@code null} if no minimum is set
     */
    public T getMinimumValue() {
        return minimumValue;
    }

    /**
     * Sets the minimum value allowed by this spinner.  Any value below this value as determined by
     * {@link SpinnerHandler#compare(Object, Object)} will be rejected in the text field, and the decrement button
     * will be unable to lower a value beyond this point.
     * <p>
     * Passing a {@code null} value to this method will remove the minimum value from the spinner.
     *
     * @param minimumValue the minimum value allowed by this spinner, or {@code null} for no minimum
     */
    public void setMinimumValue(T minimumValue) {
        this.minimumValue = minimumValue;
    }

    /**
     * Gets the maximum value allowed by this spinner.  Any value above this value as determined by
     * {@link SpinnerHandler#compare(Object, Object)} will be rejected in the text field, and the increment button
     * will be unable to raise a value beyond this point.
     * <p>
     * A {@code null} value returned from this method indicates the spinner does not have a maximum value set.
     *
     * @return The maximum value allowed by this spinner, or {@code null} if no maximum is set
     */
    public T getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the maximum value allowed by this spinner.  Any value above this value as determined by
     * {@link SpinnerHandler#compare(Object, Object)} will be rejected in the text field, and the increment button
     * will be unable to raise a value beyond this point.
     * <p>
     * Passing a {@code null} value to this method will remove the maximum value from the spinner.
     *
     * @param maximumValue the maximum value allowed by this spinner, or {@code null} for no maximum
     */
    public void setMaximumValue(T maximumValue) {
        this.maximumValue = maximumValue;
    }

    /**
     * Gets the {@link OnChangeListener} currently assigned to this spinner.  This method will return {@code null}
     * if no listener is set.
     *
     * @return the {@link OnChangeListener} assigned to this spinner, or {@code null} if there isn't one assigned
     */
    public OnChangeListener<T> getOnValueChangeListener() {
        return onValueChangeListener;
    }

    /**
     * Sets the {@link OnChangeListener} assigned to this spinner.  The listener is called whenever the value
     * for the spinner is updated.  It is not called while changes are being made in the text field, but it is
     * called when the field loses focus and the value updates.  It is called each time either the increment
     * or decrement buttons are pressed.
     * <p>
     * Passing {@code null} to this method will remove the {@link OnChangeListener} from the spinner.
     *
     * @param onValueChangeListener the {@link OnChangeListener} that should be called when the value of the spinner
     *                              changes
     */
    public void setOnValueChangeListener(OnChangeListener<T> onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    /**
     * Gets the padding around the buttons and text field for this spinner.  This padding is directly applied to
     * the text field used by the spinner, and the buttons are also placed away from the edges of the text box
     * as requested by the padding values.
     *
     * @return The {@link Padding} assigned to this spinner
     */
    public Padding getPadding() {
        return padding;
    }

    /**
     * Sets the padding around the buttons and text field for this spinner.  The padding is directly applied to
     * the text field used by the spinner, and the buttons are also placed away from the edges of the text box
     * as requested by the padding values.
     * <p>
     * Passing {@code null} to this method will result in the spinner having a padding of {@code 0} in all
     * directions.
     *
     * @param padding the {@link Padding} to assign to the spinner
     */
    public void setPadding(Padding padding) {
        // Since we don't want null padding, set the padding to 0 if null is passed in
        if (padding == null)
            padding = new Padding(0);
        // Update the current padding and recalculate sizes
        this.padding = padding;
        calculateMinimumSize();
    }

    /**
     * Gets the units string assigned to this spinner.  The units string is a unit of measurement that is
     * displayed on top of the text field to describe the units of measurement for the value being displayed.
     *
     * @return The units string assigned to this spinner, or {@code null} if there isn't one assigned
     */
    public String getUnitsString() {
        return unitsString;
    }

    /**
     * Sets the units string assigned to this spinner.  The units string is a unit of measurement that is
     * displayed on top of the text field to describe the units of measurement for the value being displayed.
     * A {@code null} units string will result in no units being displayed.
     *
     * @param unitsString the units string to assign to the spinner, or {@code null} for no units
     */
    public void setUnitsString(String unitsString) {
        this.unitsString = unitsString;
        calculateMinimumSize();
    }

    /**
     * Gets the current value from this spinner.  This value will always be within the minimum and maximum
     * boundaries set for the spinner (assuming the assigned {@link SpinnerHandler} works properly), and
     * it will never be {@code null}.
     *
     * @return The current value from this spinner
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the current value for this spinner.  The value should be within the minimum and maximum values
     * assigned for this spinner, and if it is not, it will be clamped to the closest extreme.  It must also
     * not be {@code null}, as spinners do not support {@code null} values.
     *
     * @param value the value to set for this spinner
     * @throws UIToolkitException if the value passed to the method is {@code null}
     */
    public void setValue(T value) {
        // Don't allow null values
        if (value == null)
            throw new UIToolkitException("Cannot set spinner value to null.");

        // Make sure that the value is within the minimum/maximum bounds
        if (getMinimumValue() != null && handler.compare(value, getMinimumValue()) < 0)
            value = getMinimumValue();
        else if (getMaximumValue() != null && handler.compare(value, getMaximumValue()) > 0)
            value = getMaximumValue();

        this.value = value;

        // Update the text field
        String currentValueString = handler.getString(getValue());
        if (!spinnerTextField.getText().equals(currentValueString))
            spinnerTextField.setText(currentValueString);

        // If the value was not previously notified, notify now
        if (onValueChangeListener != null && value != lastNotifiedValue) {
            lastNotifiedValue = value;
            onValueChangeListener.onChange(value);
        }
    }

    /**
     * Calculates the minimum size for the spinner (and all internal components within the spinner) based
     * on current fonts, strings, padding, etc.
     */
    private void calculateMinimumSize() {
        spinnerTextField.setPadding(new Padding(
                padding.getPaddingTop(),
                (padding.getPaddingRight() * 3) + decrementButton.getMinimumSize().getWidth() + (unitsString != null ? getGraphicsContext().getStringBounds(new Font("Montserrat", Font.PLAIN, 10), unitsString).getWidth() + padding.getPaddingRight() : 0),
                padding.getPaddingBottom(),
                (padding.getPaddingLeft() * 3) + incrementButton.getMinimumSize().getWidth()
        ));
        setMinimumSize(new UIDimension(
                Math.max(
                        Math.max(
                                decrementButton.getMinimumSize().getWidth(),
                                incrementButton.getMinimumSize().getWidth()
                        ),
                        spinnerTextField.getMinimumSize().getWidth()
                ) + (unitsString != null ? getGraphicsContext().getStringBounds(new Font("Montserrat", Font.PLAIN, 10), unitsString).getWidth() + padding.getPaddingRight() : 0),
                Math.max(
                        Math.max(
                                decrementButton.getMinimumSize().getHeight(),
                                incrementButton.getMinimumSize().getHeight()
                        ),
                        spinnerTextField.getMinimumSize().getHeight()
                )
        ));
    }

    /**
     * When the text field loses focus, validates the contents of the text field and calls the {@link OnChangeListener}
     * assigned to this spinner if the value has changed.  This method is called from the
     * {@link UISpinnerTextField#onFocusLost(boolean)} method.
     */
    private void validateTextFieldContents() {
        // When focus is lost on the text field, set the text to the string representation of the current
        // value (e.g., instead of 003, display 3) if it's not already set to it.
        String currentValueString = handler.getString(getValue());
        if (!spinnerTextField.getText().equals(currentValueString))
            spinnerTextField.setText(currentValueString);

        // If the value was not previously notified, notify now
        if (onValueChangeListener != null && value != lastNotifiedValue) {
            lastNotifiedValue = value;
            onValueChangeListener.onChange(value);
        }
    }

    @Override
    public void setSize(UIDimension size) {
        super.setSize(size);
        // The text field should render as the full size of this component
        spinnerTextField.setSize(size);

        // The decrement button should render at the far left of the component
        decrementButton.setSize(new UIDimension(
                decrementButton.getMinimumSize().getWidth(),
                size.getHeight() - padding.getPaddingTop() - padding.getPaddingBottom()
        ));
        decrementButton.getRenderSpace().update(
                getRenderSpace().getX() + padding.getPaddingLeft(),
                getRenderSpace().getY() + padding.getPaddingTop(),
                decrementButton.getWidth(),
                decrementButton.getHeight()
        );

        // The increment button should render at the far right of the component
        incrementButton.setSize(new UIDimension(
                incrementButton.getMinimumSize().getWidth(),
                size.getHeight() - padding.getPaddingTop() - padding.getPaddingBottom()
        ));
        incrementButton.getRenderSpace().update(
                getRenderSpace().getX() + getWidth() - padding.getPaddingRight() - incrementButton.getWidth(),
                getRenderSpace().getY() + padding.getPaddingTop(),
                incrementButton.getWidth(),
                incrementButton.getHeight()
        );
    }

    @Override
    public void render(UIGraphics graphics) {
        // Render the text field as the "background"
        spinnerTextField.render(graphics);

        // Render the units string
        if (unitsString != null) {
            graphics.withCopy(unitTextGraphics -> {
                unitTextGraphics.setColor(UITheme.getColor(ThemeKeys.Colors.UI.TEXT_FIELD_FOREGROUND_PLACEHOLDER));
                unitTextGraphics.getInternalGraphics().setFont(new Font("Montserrat", Font.PLAIN, 10)); // TODO
                FontMetrics metrics = unitTextGraphics.getInternalGraphics().getFontMetrics();
                unitTextGraphics.drawString(unitsString, getWidth() - (padding.getPaddingRight() * 2) - incrementButton.getWidth() - metrics.stringWidth(unitsString), (getHeight() / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2));
            });
        }

        // Create the decrement button renderer
        graphics.withCopy(decrementButtonGraphics -> {
            decrementButtonGraphics.translate(decrementButton.getX() - getX(), decrementButton.getY() - getY());
            decrementButtonGraphics.clip(decrementButton.getWidth(), decrementButton.getHeight());
            decrementButton.render(decrementButtonGraphics);
        });

        // Create the increment button renderer
        graphics.withCopy(incrementButtonGraphics -> {
            incrementButtonGraphics.translate(incrementButton.getX() - getX(), incrementButton.getY() - getY());
            incrementButtonGraphics.clip(incrementButton.getWidth(), incrementButton.getHeight());
            incrementButton.render(incrementButtonGraphics);
        });
    }

    /**
     * Calculates which of the internal components of this spinner the mouse is currently hovering over (if any).
     *
     * @param x the X coordinate of the mouse
     * @param y the Y coordinate of the mouse
     */
    private void calculateHoveredComponents(int x, int y) {
        // Store current hover state
        boolean decrementCurrentlyHovered = decrementHovered;
        boolean incrementCurrentlyHovered = incrementHovered;
        boolean textFieldCurrentlyHovered = textFieldHovered;

        // Calculate whether the mouse is in any of the components in the spinner
        int renderSpaceOffsetX = getRenderSpace().getX() + x;
        int renderSpaceOffsetY = getRenderSpace().getY() + y;
        if (decrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY)) {
            decrementHovered = true;
            incrementHovered = false;
            textFieldHovered = false;
        } else if (incrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY)) {
            decrementHovered = false;
            incrementHovered = true;
            textFieldHovered = false;
        } else {
            decrementHovered = false;
            incrementHovered = false;
            textFieldHovered = true;
        }

        // Call any enter/exit listeners on components that changed hover state
        if (decrementCurrentlyHovered && !decrementHovered)
            decrementButton.onMouseExited();
        else if (!decrementCurrentlyHovered && decrementHovered)
            decrementButton.onMouseEntered();

        if (incrementCurrentlyHovered && !incrementHovered)
            incrementButton.onMouseExited();
        else if (!incrementCurrentlyHovered && incrementHovered)
            incrementButton.onMouseEntered();

        if (textFieldCurrentlyHovered && !textFieldHovered)
            spinnerTextField.onMouseExited();
        else if (!textFieldCurrentlyHovered && textFieldHovered)
            spinnerTextField.onMouseEntered();
    }

    @Override
    public boolean onMouseClicked(int x, int y, int button) {
        // If the mouse is inside of one of the buttons, pass the click on to them.
        // Otherwise, pass it through to the text field
        int renderSpaceOffsetX = getRenderSpace().getX() + x;
        int renderSpaceOffsetY = getRenderSpace().getY() + y;

        // Store the return value so we can return it later
        boolean returnValue;
        if (decrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY))
            returnValue = decrementButton.onMouseClicked(x - decrementButton.getX(), y - decrementButton.getY(), button);
        else if (incrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY))
            returnValue = incrementButton.onMouseClicked(x - incrementButton.getX(), y - incrementButton.getY(), button);
        else
            returnValue = spinnerTextField.onMouseClicked(x, y, button);

        // If we're hovering on the text field, mark it as focused, and vice versa
        spinnerTextField.setFocusState(textFieldHovered, false);
        textFieldFocused = textFieldHovered;

        return returnValue;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        // If the mouse is inside of one of the buttons, pass the click on to them.
        // Otherwise, pass it through to the text field
        int renderSpaceOffsetX = getRenderSpace().getX() + x;
        int renderSpaceOffsetY = getRenderSpace().getY() + y;

        // Store the return value so we can return it later
        boolean returnValue;
        if (decrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY)) {
            decrementPressed = true;
            returnValue = decrementButton.onMousePressed(x - decrementButton.getX(), y - decrementButton.getY(), button);
        } else if (incrementButton.getRenderSpace().getRectangle().contains(renderSpaceOffsetX, renderSpaceOffsetY)) {
            incrementPressed = true;
            returnValue = incrementButton.onMousePressed(x - incrementButton.getX(), y - incrementButton.getY(), button);
        } else {
            returnValue = spinnerTextField.onMousePressed(x, y, button);
        }

        // If we're hovering on the text field, mark it as focused, and vice versa
        spinnerTextField.setFocusState(textFieldHovered, false);
        textFieldFocused = textFieldHovered;

        return returnValue;
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        // Pass the mouse release event onto whatever component is pressed
        if (decrementPressed) {
            decrementPressed = false;
            decrementButton.onMouseReleased(x - decrementButton.getX(), y - decrementButton.getY(), button);
        } else if (incrementPressed) {
            incrementPressed = false;
            incrementButton.onMouseReleased(x - incrementButton.getX(), y - incrementButton.getY(), button);
        } else {
            spinnerTextField.onMouseReleased(x, y, button);
        }
    }

    @Override
    public void onMouseEntered() {
    }

    @Override
    public void onMouseExited() {
        if (decrementHovered)
            decrementButton.onMouseExited();
        else if (incrementHovered)
            incrementButton.onMouseExited();
        else if (textFieldHovered)
            spinnerTextField.onMouseExited();

        decrementHovered = false;
        incrementHovered = false;
        textFieldHovered = false;
    }

    @Override
    public void onMouseDragged(int x, int y) {
        calculateHoveredComponents(x, y);

        // Pass the mouse drag event onto whatever component is pressed
        if (decrementPressed)
            decrementButton.onMouseDragged(x - decrementButton.getX(), y - decrementButton.getY());
        else if (incrementPressed)
            incrementButton.onMouseDragged(x - incrementButton.getX(), y - incrementButton.getY());
        else
            spinnerTextField.onMouseDragged(x, y);
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        // If the mouse is inside of one of the buttons, pass the click on to them.
        // Otherwise, pass it through to the text field
        calculateHoveredComponents(x, y);

        if (decrementHovered)
            return decrementButton.onMouseMoved(x, y);
        else if (incrementHovered)
            return incrementButton.onMouseMoved(x, y);
        else if (textFieldHovered)
            return spinnerTextField.onMouseMoved(x, y);

        return true;
    }

    @Override
    public boolean onMouseScrolled(int scrollAmount) {
        return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        // Pass key events through to the text field
        return spinnerTextField.onKeyTyped(e);
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        // Pass key events through to the text field
        return spinnerTextField.onKeyPressed(e);
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        // Pass key events through to the text field
        return spinnerTextField.onKeyReleased(e);
    }

    @Override
    public void onFocusGained(boolean keyboardTriggered) {
        spinnerTextField.setFocusState(keyboardTriggered, keyboardTriggered);
        textFieldFocused = true;
    }

    @Override
    public void onFocusLost(boolean keyboardTriggered) {
        spinnerTextField.setFocusState(false, keyboardTriggered);
        textFieldFocused = false;
    }

    @Override
    public void onRenderPass() {
        // Pass the render pass listener call to the child components
        spinnerTextField.onRenderPass();
        decrementButton.onRenderPass();
        incrementButton.onRenderPass();
    }

    /**
     * Gets the built-in {@link SpinnerHandler} for {@link Integer} or {@code int} values.  This handler allows both
     * positive and negative integer values (though this can be limited by setting minimum or maximum values on
     * the spinner itself).  The default minimum and maximum values provided by the handler are
     * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} respectively.  The default value set by the handler
     * if none is provided to the spinner constructor is {@code 0}.
     *
     * @return The built-in {@link SpinnerHandler} for {@link Integer} or {@code int} values
     */
    public static SpinnerHandler<Integer> getIntegerHandler() {
        // Create the handler if it doesn't exist, then return it
        if (integerHandler == null) {
            integerHandler = new SpinnerHandler<>() {
                @Override
                public String getValidationRegex() {
                    return "([-+]|[+-]?[0-9]+)";
                }

                @Override
                public boolean getClampToMinimum(Integer minimum) {
                    return minimum < 0;
                }

                @Override
                public boolean getClampToMaximum(Integer maximum) {
                    return maximum > 0;
                }

                @Override
                public Integer getDefaultValue(Integer minimum, Integer maximum) {
                    return Math.max(Math.min(0, maximum), minimum);
                }

                @Override
                public Integer getDefaultMinimum() {
                    return Integer.MIN_VALUE;
                }

                @Override
                public Integer getDefaultMaximum() {
                    return Integer.MAX_VALUE;
                }

                @Override
                public Integer getValue(String string) {
                    // Try to convert, and return null if it fails (indicating invalid value)
                    try {
                        return Integer.parseInt(string);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                @Override
                public String getString(Integer value) {
                    return value.toString();
                }

                @Override
                public int compare(Integer first, Integer second) {
                    return Integer.compare(first, second);
                }

                @Override
                public Integer decrement(Integer value) {
                    return value - 1;
                }

                @Override
                public Integer increment(Integer value) {
                    return value + 1;
                }
            };
        }

        return integerHandler;
    }
}
