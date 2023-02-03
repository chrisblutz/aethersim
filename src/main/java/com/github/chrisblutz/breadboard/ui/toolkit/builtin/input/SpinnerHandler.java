package com.github.chrisblutz.breadboard.ui.toolkit.builtin.input;

public interface SpinnerHandler<T> {
    /**
     * Spinners use this method to retrieve the regular expression that should be used to validate the input
     * to the internal text field.  Any input that does not match this regular expression will be invalidated
     * and not allowed.
     * <p>
     * A value of {@code null} indicates that all input should be accepted.
     *
     * @return The regular expression that validates input to the spinner, or {@code null} if all input should
     * be accepted.
     */
    String getValidationRegex();

    /**
     * Spinners use this method to determine if the text field should prevent entry of values less than
     * the minimum value.
     * <p>
     * For example, an integer spinner with a minimum value of -10 might clamp to that value, preventing
     * users from entering anything less than -10 (e.g., a user could not type another 1 after typing -1,
     * as that would yield -11).
     * <p>
     * However, it might not always be preferable.  For example, an integer spinner with a minimum value
     * of 2 but a maximum of 100 would not want this, as it would prevent users from entering a 1 (since
     * 1 is less than the minimum).  Thus, users would not be able to type anything starting with a 1
     * (such as 10).
     * <p>
     * This method will never be called with a {@code null} minimum, as spinners without a minimum cannot
     * clamp to the minimum.
     *
     * @param minimum the minimum value for the spinner
     * @return {@code true} if the spinner should clamp to the minimum, {@code false} otherwise
     */
    boolean getClampToMinimum(T minimum);

    /**
     * Spinners use this method to determine if the text field should prevent entry of values greater than
     * the maximum value.
     * <p>
     * For example, an integer spinner with a maximum value of 10 might clamp to that value, preventing
     * users from entering anything larger than 10 (e.g., a user could not type another 1 after typing 1,
     * as that would yield 11).
     * <p>
     * However, it might not always be preferable.  For example, an integer spinner with a maximum value
     * of -2 but a minimum of -100 would not want this, as it would prevent users from entering a -1 (since
     * -1 is greater than the maximum).  Thus, users would not be able to type anything starting with a
     * -1 (such as -10).
     * <p>
     * This method will never be called with a {@code null} maximum, as spinners without a maximum cannot
     * clamp to the maximum.
     *
     * @param maximum the maximum value for the spinner
     * @return {@code true} if the spinner should clamp to the maximum, {@code false} otherwise
     */
    boolean getClampToMaximum(T maximum);

    /**
     * Spinners use this method to determine the default value to assign to a spinner of this type.
     * This method may not return {@code null}, as spinners require a non-{@code null} default value.
     *
     * @param minimum the minimum value set for the spinner, or {@code null} if there isn't one
     * @param maximum the maximum value set for the spinner, or {@code null} if there isn't one
     * @return The non-{@code null} default value for spinners of this type
     */
    T getDefaultValue(T minimum, T maximum);

    T getDefaultMinimum();

    T getDefaultMaximum();

    /**
     * Spinners use this method to get the actual value of the string entered into the text area.  For example,
     * an integer handler that receives {@code "1"} would return {@code 1}.
     * <p>
     * If the string passed to this method is not a valid representation of the type this handler handles, this
     * method should return {@code null}.  This tells the spinner that the entered value
     * was invalid.
     *
     * @param string the string representation from the text field
     * @return The actual value represented by the string, or {@code null} if the string does not represent
     * a valid value
     */
    T getValue(String string);

    String getString(T value);

    int compare(T first, T second);

    T decrement(T value);

    T increment(T value);
}
