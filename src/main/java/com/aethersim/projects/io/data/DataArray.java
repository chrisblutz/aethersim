package com.aethersim.projects.io.data;

import com.aethersim.projects.io.data.exceptions.DataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DataArray extends ArrayList<DataEntry> implements DataEntry {

    public DataArray() {
        super();
    }

    public DataArray(DataEntry... entries) {
        super(Arrays.asList(entries));
        // Make sure all entries are non-null
        if (stream().anyMatch(Objects::isNull))
            throw new DataException("Data arrays cannot have null entries.");
    }

    public DataArray(List<DataEntry> entries) {
        super(entries);
        // Make sure all entries are non-null
        if (stream().anyMatch(Objects::isNull))
            throw new DataException("Data arrays cannot have null entries.");
    }

    /**
     * This method determines if all elements in this array are scalars, as determined by {@link DataEntry#isScalar()}.
     *
     * @return {@code true} if all elements in this array are scalars, {@code false} otherwise
     * @see DataEntry#isScalar()
     */
    public boolean isScalarArray() {
        return stream().allMatch(DataEntry::isScalar);
    }

    /**
     * This method determines if all elements in this array are maps, as determined by {@link DataEntry#isMap()}.
     *
     * @return {@code true} if all elements in this array are maps, {@code false} otherwise
     * @see DataEntry#isMap()
     */
    public boolean isMapArray() {
        return stream().allMatch(DataEntry::isMap);
    }

    /**
     * This method determines if all elements in this array are arrays, as determined by {@link DataEntry#isArray()}.
     *
     * @return {@code true} if all elements in this array are arrays, {@code false} otherwise
     * @see DataEntry#isArray()
     */
    public boolean isArrayArray() {
        return stream().allMatch(DataEntry::isArray);
    }

    @Override
    public void dispose() {
        // Clear the array after disposing of all internal elements
        parallelStream().forEach(DataEntry::dispose);
        clear();
    }

    @Override
    public Object getRaw() {
        return getRawArray();
    }

    /**
     * This method gets the raw objects underlying this array as an array, without any type checking or conversion.
     * All nested {@link DataMap} or {@link DataArray} objects are converted to their raw versions as well.
     *
     * @return The {@code Object[]} array containing all the raw data elements from this array
     * @see DataEntry#isScalar()
     * @see DataMap#getRawMap()
     * @see DataValue#getRaw()
     */
    public Object[] getRawArray() {
        return stream()
                .map(DataEntry::getRaw)
                .toArray(Object[]::new);
    }

    /**
     * This method returns the list of {@link DataMap} objects contained in this array as a basic array.  If the data
     * contained in this array does not match that type, this method will throw an exception.
     *
     * @return The array of {@link DataMap} objects containing all elements from this array
     */
    public DataMap[] getMapArray() {
        if (!isMapArray())
            throw new DataException("Cannot marshal array containing non-map values to DataMap[].");

        return stream()
                .map(DataEntry::getMap)
                .toArray(DataMap[]::new);
    }

    /**
     * This method returns the list of {@link DataArray} objects contained in this array as a basic array.  If the data
     * contained in this array does not match that type, this method will throw an exception.
     *
     * @return The array of {@link DataArray} objects containing all elements from this array
     */
    public DataArray[] getArrayArray() {
        if (!isArrayArray())
            throw new DataException("Cannot marshal array containing non-array values to DataArray[].");

        return stream()
                .map(DataEntry::getArray)
                .toArray(DataArray[]::new);
    }

    /**
     * This method converts the entries in this data array to a {@link String} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.
     *
     * @return The {@code String[]} array containing the data in this array converted to {@link String}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getString()
     */
    public String[] getStringArray() {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to String[].");

        return stream()
                .map(DataEntry::getScalar)
                .map(DataValue::getString)
                .toArray(String[]::new);
    }

    /**
     * This method converts the entries in this data array to a {@code boolean} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code boolean} value, this method will throw an exception.  See {@link DataValue#getBoolean()}
     * for accepted values.
     *
     * @return The {@code boolean[]} array containing the data in this array converted to {@code boolean}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getBoolean()
     */
    public boolean[] getBooleanArray() {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to boolean[].");

        // Since Java doesn't support streaming to boolean arrays, we have to do this manually
        boolean[] array = new boolean[size()];
        for (int index = 0; index < array.length; index++)
            array[index] = get(index).getScalar().getBoolean();
        return array;
    }

    /**
     * This method converts the entries in this data array to a {@code char} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code char} value, this method will throw an exception.
     *
     * @return The {@code char[]} array containing the data in this array converted to {@code char}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getChar()
     */
    public char[] getCharArray() {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to char[].");

        // Since Java doesn't support streaming to char arrays, we have to do this manually
        char[] array = new char[size()];
        for (int index = 0; index < array.length; index++)
            array[index] = get(index).getScalar().getChar();
        return array;
    }

    /**
     * This method converts the entries in this data array to a {@code int} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code int} value, this method will throw an exception.
     * <p>
     * This method uses a radix of 10 for unmarshalling.  To use a different radix, use
     * {@link #getIntArray(int)} instead.
     *
     * @return The {@code int[]} array containing the data in this array converted to {@code int}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getInt()
     */
    public int[] getIntArray() {
        return getIntArray(10);
    }

    /**
     * This method converts the entries in this data array to a {@code int} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code int} value, this method will throw an exception.
     *
     * @param radix the radix to use for string conversions
     * @return The {@code int[]} array containing the data in this array converted to {@code int}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getInt(int)
     */
    public int[] getIntArray(int radix) {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to int[].");

        return stream()
                .map(DataEntry::getScalar)
                .mapToInt(entry -> entry.getInt(radix))
                .toArray();
    }

    /**
     * This method converts the entries in this data array to a {@code long} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code long} value, this method will throw an exception.
     * <p>
     * This method uses a radix of 10 for unmarshalling.  To use a different radix, use
     * {@link #getLongArray(int)} instead.
     *
     * @return The {@code long[]} array containing the data in this array converted to {@code long}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getLong()
     */
    public long[] getLongArray() {
        return getLongArray(10);
    }

    /**
     * This method converts the entries in this data array to a {@code long} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code long} value, this method will throw an exception.
     *
     * @param radix the radix to use for string conversions
     * @return The {@code long[]} array containing the data in this array converted to {@code long}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getLong(int)
     */
    public long[] getLongArray(int radix) {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to long[].");

        return stream()
                .map(DataEntry::getScalar)
                .mapToLong(entry -> entry.getLong(radix))
                .toArray();
    }

    /**
     * This method converts the entries in this data array to a {@code float} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code float} value, this method will throw an exception.
     *
     * @return The {@code float[]} array containing the data in this array converted to {@code float}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getFloat()
     */
    public float[] getFloatArray() {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to float[].");

        // Since Java doesn't support streaming to float arrays, we have to do this manually
        float[] array = new float[size()];
        for (int index = 0; index < array.length; index++)
            array[index] = get(index).getScalar().getFloat();
        return array;
    }

    /**
     * This method converts the entries in this data array to a {@code double} array.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code double} value, this method will throw an exception.
     *
     * @return The {@code double[]} array containing the data in this array converted to {@code double}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getDouble()
     */
    public double[] getDoubleArray() {
        if (!isScalarArray())
            throw new DataException("Cannot marshal array containing non-scalar values to double[].");

        return stream()
                .map(DataEntry::getScalar)
                .mapToDouble(DataValue::getDouble)
                .toArray();
    }
}
