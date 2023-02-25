package com.aethersim.projects.io.data;


import com.aethersim.projects.io.data.exceptions.DataException;

public class DataValue implements DataEntry {

    private final Object data;

    protected DataValue(Object data) {
        if (data == null)
            throw new DataException("Data values cannot have null data.");
        this.data = data;
    }

    @Override
    public void dispose() { /* do nothing */ }

    @Override
    public Object getRaw() {
        return data;
    }

    /**
     * This method returns the {@link String} value of the data in this entry.  This is equivalent to the following:
     * <pre>
     *     getRaw().getString()
     * </pre>
     *
     * @return The {@link String} form of the data in this entry
     */
    public String getString() {
        return getRaw().toString();
    }

    /**
     * This method returns the {@code boolean} value of the data in this entry  If the data cannot be marshaled to a
     * {@code boolean} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code boolean} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code boolean} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to {@code true} if it is a variant of {@code "true"}, and
     *     {@code false} if it is a variant of {@code "false"}</li>
     *     <li>A {@code char} value is converted to {@code true} if it is a variant of {@code 't'} and
     *     {@code false} if it is a variant of {@code 'f'}</li>
     *     <li>A numeric ({@code int}, {@code long}, {@code float}, and {@code double}) value is converted to
     *     {@code true} if it is non-zero, and {@code false} if it is zero</li>
     * </ul>
     *
     * @return The {@code boolean} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code boolean}
     */
    public boolean getBoolean() {
        // If it's a boolean already, return that value
        if (getRaw() instanceof Boolean booleanData)
            return booleanData;

        // If it's a string, try to read "true" or "false" (case-insensitive)
        if (getRaw() instanceof String stringData) {
            // Convert the string to lower case to provide case-insensitive checks
            String rawString = getString().toLowerCase();
            // Check for "true" and "false".  Otherwise, throw an exception.
            if (rawString.equals("true"))
                return true;
            else if (rawString.equals("false"))
                return false;
        }

        // If it's a character, check if it's t/T or f/F
        if (getRaw() instanceof Character characterData) {
            if (characterData == 't' || characterData == 'T')
                return true;
            else if (characterData == 'f' || characterData == 'F')
                return false;
        }

        // If it's a numeric value, check if it's non-zero
        if (getRaw() instanceof Integer integerData)
            return integerData != 0;
        if (getRaw() instanceof Long longData)
            return longData != 0L;
        if (getRaw() instanceof Float floatData)
            return floatData != 0f;
        if (getRaw() instanceof Double doubleData)
            return doubleData != 0f;

        // If we get here, we haven't found a way to marshal the value to a boolean
        throw new DataException("Cannot marshall value to boolean.");
    }

    /**
     * This method returns the {@code char} value of the data in this entry  If the data cannot be marshaled to a
     * {@code char} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code char} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code char} value is left as-is and returned</li>
     *     <li>A {@link String} value containing a single character will return that character</li>
     * </ul>
     *
     * @return The {@code char} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code char}
     */
    public char getChar() {
        // If it's a char already, return that value
        if (getRaw() instanceof Character characterData)
            return characterData;

        // If it's a string, check if it is one character long
        if (getRaw() instanceof String stringData) {
            char[] charArray = stringData.toCharArray();
            if (charArray.length == 1)
                return charArray[0];
        }

        // If we get here, we haven't found a way to marshal the value to a char
        throw new DataException("Cannot marshal value to char.");
    }

    /**
     * This method returns the {@code int} value of the data in this entry  If the data cannot be marshaled to a
     * {@code int} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to an {@code int} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>An {@code int} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code int} if it represents a valid integer</li>
     *     <li>A {@code boolean} value is converted to {@code 1} if {@code true} and {@code 0} if {@code false}</li>
     *     <li>A {@code char} value is converted to {@code int} and returned</li>
     *     <li>A numeric ({@code long}, {@code float}, and {@code double}) value is converted to {@code long}
     *     and returned</li>
     * </ul>
     * <p>
     * This method uses a radix of 10 for unmarshalling if the underlying value is a {@link String}.  To use a
     * different radix, use {@link #getInt(int)} instead.
     *
     * @return The {@code int} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to an {@code int}
     */
    public int getInt() {
        return getInt(10);
    }

    /**
     * This method returns the {@code int} value of the data in this entry  If the data cannot be marshaled to a
     * {@code int} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to an {@code int} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>An {@code int} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code int} if it represents a valid integer</li>
     *     <li>A {@code boolean} value is converted to {@code 1} if {@code true} and {@code 0} if {@code false}</li>
     *     <li>A {@code char} value is converted to {@code int} and returned</li>
     *     <li>A numeric ({@code long}, {@code float}, and {@code double}) value is converted to {@code long}
     *     and returned</li>
     * </ul>
     *
     * @param radix the radix to use for conversions from {@link String}, otherwise unused
     * @return The {@code int} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to an {@code int}
     */
    public int getInt(int radix) {
        // If it's an int already, return that value
        if (getRaw() instanceof Integer integerData)
            return integerData;

        // If it's a string, try to parse an integer from it
        if (getRaw() instanceof String stringData) {
            try {
                return Integer.parseInt(stringData, radix);
            } catch (NumberFormatException e) {
                throw new DataException("Cannot marshal value to int.", e);
            }
        }

        // If it's a boolean, return 1 if true and 0 if false
        if (getRaw() instanceof Boolean booleanData)
            return booleanData ? 1 : 0;

        // If it's a char, return it as an integer
        if (getRaw() instanceof Character characterData)
            return characterData;

        // If it's a long, return it as long as it's within the range of integer values
        if (getRaw() instanceof Long longData) {
            try {
                return Math.toIntExact(longData);
            } catch (NumberFormatException e) {
                throw new DataException("Cannot marshal value to int.", e);
            }
        }

        // If it's a float or double, return it as a long
        if (getRaw() instanceof Float floatData)
            return floatData.intValue();
        if (getRaw() instanceof Double doubleData)
            return doubleData.intValue();

        // If we get here, we haven't found a way to marshal the value to an int
        throw new DataException("Cannot marshal value to int.");
    }

    /**
     * This method returns the {@code long} value of the data in this entry  If the data cannot be marshaled to a
     * {@code long} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code long} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code long} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code long} if it represents a valid long integer</li>
     *     <li>A {@code boolean} value is converted to {@code 1} if {@code true} and {@code 0} if {@code false}</li>
     *     <li>A {@code char} value is converted to {@code long} and returned</li>
     *     <li>A numeric ({@code int}, {@code float}, and {@code double}) value is converted to {@code long}
     *     and returned</li>
     * </ul>
     * <p>
     * This method uses a radix of 10 for unmarshalling if the underlying value is a {@link String}.  To use a
     * different radix, use {@link #getLong(int)} instead.
     *
     * @return The {@code long} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code long}
     */
    public long getLong() {
        return getLong(10);
    }

    /**
     * This method returns the {@code long} value of the data in this entry  If the data cannot be marshaled to a
     * {@code long} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code long} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code long} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code long} if it represents a valid long integer</li>
     *     <li>A {@code boolean} value is converted to {@code 1} if {@code true} and {@code 0} if {@code false}</li>
     *     <li>A {@code char} value is converted to {@code long} and returned</li>
     *     <li>A numeric ({@code int}, {@code float}, and {@code double}) value is converted to {@code long}
     *     and returned</li>
     * </ul>
     *
     * @param radix the radix to use for conversions from {@link String}, otherwise unused
     * @return The {@code long} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code long}
     */
    public long getLong(int radix) {
        // If it's a long already, return that value
        if (getRaw() instanceof Long longData)
            return longData;

        // If it's a string, try to parse a long from it
        if (getRaw() instanceof String stringData) {
            try {
                return Long.parseLong(stringData, radix);
            } catch (NumberFormatException e) {
                throw new DataException("Cannot marshal value to long.", e);
            }
        }

        // If it's a boolean, return 1 if true and 0 if false
        if (getRaw() instanceof Boolean booleanData)
            return booleanData ? 1 : 0;

        // If it's a char, return it as a long
        if (getRaw() instanceof Character characterData)
            return characterData;

        // If it's an int, float, or double, return it as a long
        if (getRaw() instanceof Integer integerData)
            return integerData;
        if (getRaw() instanceof Float floatData)
            return floatData.longValue();
        if (getRaw() instanceof Double doubleData)
            return doubleData.longValue();

        // If we get here, we haven't found a way to marshal the value to a long
        throw new DataException("Cannot marshal value to long.");
    }

    /**
     * This method returns the {@code float} value of the data in this entry  If the data cannot be marshaled to a
     * {@code float} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code float} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code float} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code float} if it represents a valid
     *     floating-point number</li>
     *     <li>A numeric ({@code int}, {@code long}, and {@code double}) value is converted to {@code float}
     *     and returned</li>
     * </ul>
     *
     * @return The {@code float} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code long}
     */
    public float getFloat() {
        // If it's a float already, return that value
        if (getRaw() instanceof Float floatData)
            return floatData;

        // If it's a string, try to parse a float from it
        if (getRaw() instanceof String stringData) {
            try {
                return Float.parseFloat(stringData);
            } catch (NumberFormatException e) {
                throw new DataException("Cannot marshal value to float.", e);
            }
        }

        // If it's an int, long, or double, return it as a float
        if (getRaw() instanceof Integer integerData)
            return (float) integerData;
        if (getRaw() instanceof Long longData)
            return (float) longData;
        if (getRaw() instanceof Double doubleData)
            return doubleData.floatValue();

        // If we get here, we haven't found a way to marshal the value to a float
        throw new DataException("Cannot marshal value to float.");
    }

    /**
     * This method returns the {@code double} value of the data in this entry  If the data cannot be marshaled to a
     * {@code double} value, this method will also throw an exception.
     * <p>
     * The following criteria are used to determine if a value evaluates to a {@code double} value (any value that
     * does not meet any of the criteria below will cause this method to throw an exception):
     * <ul>
     *     <li>A {@code double} value is left as-is and returned</li>
     *     <li>A {@link String} value is converted to an {@code double} if it represents a valid
     *     floating-point number</li>
     *     <li>A numeric ({@code int}, {@code long}, and {@code float}) value is converted to {@code double}
     *     and returned</li>
     * </ul>
     *
     * @return The {@code double} form of the data in this entry
     * @throws DataException if the raw data in this object cannot be marshalled to a {@code long}
     */
    public double getDouble() {
        // If it's a double already, return that value
        if (getRaw() instanceof Double doubleData)
            return doubleData;

        // If it's a string, try to parse a double from it
        if (getRaw() instanceof String stringData) {
            try {
                return Double.parseDouble(stringData);
            } catch (NumberFormatException e) {
                throw new DataException("Cannot marshal value to double.", e);
            }
        }

        // If it's an int, long, or float, return it as a float
        if (getRaw() instanceof Integer integerData)
            return (double) integerData;
        if (getRaw() instanceof Long longData)
            return (double) longData;
        if (getRaw() instanceof Float floatData)
            return floatData.doubleValue();

        // If we get here, we haven't found a way to marshal the value to a double
        throw new DataException("Cannot marshal value to double.");
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@link String}.  The string <em>may not</em>
     * be {@code null}, and passing a {@code null} to this method will result in an exception.
     *
     * @param value the {@link String} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(String value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code boolean}.
     *
     * @param value the {@code boolean} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(boolean value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code char}.
     *
     * @param value the {@code char} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(char value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code int}.
     *
     * @param value the {@code int} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(int value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code long}.
     *
     * @param value the {@code long} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(long value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code float}.
     *
     * @param value the {@code float} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(float value) {
        return new DataValue(value);
    }

    /**
     * This method constructs a new {@code DataValue} from the specified {@code double}.
     *
     * @param value the {@code double} value to use
     * @return The new {@code DataValue} object
     */
    public static DataValue from(double value) {
        return new DataValue(value);
    }
}
