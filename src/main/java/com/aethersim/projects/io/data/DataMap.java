package com.aethersim.projects.io.data;

import com.aethersim.projects.io.data.exceptions.DataException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataMap extends LinkedHashMap<String, DataEntry> implements DataEntry {

    public DataMap() {
        super();
    }

    public DataMap(Map<String, DataEntry> entries) {
        super(entries);
        // Make sure all entries are non-null
        if (values().stream().anyMatch(Objects::isNull))
            throw new DataException("Data maps cannot have null entries.");
    }

    /**
     * This method determines if all values in this map are scalars, as determined by {@link DataEntry#isScalar()}.
     *
     * @return {@code true} if all values in this map are scalars, {@code false} otherwise
     * @see DataEntry#isScalar()
     */
    public boolean isScalarMap() {
        return values().stream().allMatch(DataEntry::isScalar);
    }

    /**
     * This method determines if all values in this map are maps, as determined by {@link DataEntry#isMap()}.
     *
     * @return {@code true} if all values in this map are maps, {@code false} otherwise
     * @see DataEntry#isMap()
     */
    public boolean isMapMap() {
        return values().stream().allMatch(DataEntry::isMap);
    }

    /**
     * This method determines if all values in this map are arrays, as determined by {@link DataEntry#isArray()}.
     *
     * @return {@code true} if all values in this map are arrays, {@code false} otherwise
     * @see DataEntry#isArray()
     */
    public boolean isArrayMap() {
        return values().stream().allMatch(DataEntry::isArray);
    }

    @Override
    public void dispose() {
        // Clear the map after disposing of all internal elements
        values().parallelStream().forEach(DataEntry::dispose);
        clear();
    }

    @Override
    public Object getRaw() {
        return getRawMap();
    }

    /**
     * This method gets the {@code Map<String, Object>} to the raw objects underlying this map, without any type
     * checking or conversion.  All nested {@link DataMap} or {@link DataArray} objects are converted to their raw
     * versions as well.
     *
     * @return The {@code Map<String, Object} containing all the elements from this map
     * @see DataEntry#isScalar()
     * @see DataArray#getRawArray()
     * @see DataValue#getRaw()
     */
    public Map<String, Object> getRawMap() {
        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value to its raw form
                                Map.Entry::getKey,
                                entry -> entry.getValue().getRaw(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method returns the mapping of {@link String} objects to {@link DataMap} objects contained in this map
     * If the data contained in this map does not match that format, this method will throw an exception.
     *
     * @return The {@code Map<String, DataMap>} containing the data from this array
     */
    public Map<String, DataMap> getMapMap() {
        if (!isMapMap())
            throw new DataException("Cannot marshal array containing non-map values to DataMap.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getMap(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method returns the mapping of {@link String} objects to {@link DataArray} objects contained in this map
     * If the data contained in this map does not match that format, this method will throw an exception.
     *
     * @return The {@code Map<String, DataArray>} containing the data from this array
     */
    public Map<String, DataArray> getArrayMap() {
        if (!isArrayMap())
            throw new DataException("Cannot marshal array containing non-map values to DataArray.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getArray(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@link String} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}
     *
     * @return The {@code Map<String, String>} containing the data in this array converted to {@link String}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getString()
     */
    public Map<String, String> getStringMap() {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to String.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getString(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code boolean} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code boolean} value, this method will throw an exception.
     *
     * @return The {@code Map<String, Boolean>} containing the data in this array converted to {@code boolean}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getBoolean()
     */
    public Map<String, Boolean> getBooleanMap() {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to boolean.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getBoolean(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code char} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code char} value, this method will throw an exception.
     *
     * @return The {@code Map<String, Character>} containing the data in this array converted to {@code char}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getChar()
     */
    public Map<String, Character> getCharMap() {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to char.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getChar(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code int} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code int} value, this method will throw an exception.
     * <p>
     * This method uses a radix of 10 for unmarshalling.  To use a different radix, use
     * {@link #getIntMap(int)} instead.
     *
     * @return The {@code Map<String, Integer>} containing the data in this array converted to {@code int}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getInt()
     */
    public Map<String, Integer> getIntMap() {
        return getIntMap(10);
    }

    /**
     * This method converts the entries in this data map to a {@code int} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code int} value, this method will throw an exception.
     *
     * @param radix the radix to use for string conversions
     * @return The {@code Map<String, Integer>} containing the data in this array converted to {@code int}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getInt(int)
     */
    public Map<String, Integer> getIntMap(int radix) {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to int.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getInt(radix),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code long} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code long} value, this method will throw an exception.
     * <p>
     * This method uses a radix of 10 for unmarshalling.  To use a different radix, use
     * {@link #getLongMap(int)} instead.
     *
     * @return The {@code Map<String, Long>} containing the data in this array converted to {@code long}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getLong()
     */
    public Map<String, Long> getLongMap() {
        return getLongMap(10);
    }

    /**
     * This method converts the entries in this data map to a {@code long} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code long} value, this method will throw an exception.
     *
     * @param radix the radix to use for string conversions
     * @return The {@code Map<String, Long>} containing the data in this array converted to {@code long}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getLong(int)
     */
    public Map<String, Long> getLongMap(int radix) {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to long.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getLong(radix),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code float} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code float} value, this method will throw an exception.
     *
     * @return The {@code Map<String, Float>} containing the data in this array converted to {@code float}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getFloat()
     */
    public Map<String, Float> getFloatMap() {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to float.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getFloat(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * This method converts the entries in this data map to a {@code double} mapping.  This method will only work if
     * all values in this array are scalars, as determined by {@link DataEntry#isScalar()}.  If the data cannot be
     * marshaled to a {@code double} value, this method will throw an exception.
     *
     * @return The {@code Map<String, Double>} containing the data in this array converted to {@code double}s.
     * @see DataEntry#isScalar()
     * @see DataValue#getMap()
     */
    public Map<String, Double> getDoubleMap() {
        if (!isScalarMap())
            throw new DataException("Cannot marshal array containing non-map values to double.");

        return entrySet().stream()
                .collect(
                        Collectors.toMap(
                                // Leave the key as-is, and convert the value
                                Map.Entry::getKey,
                                entry -> entry.getValue().getScalar().getDouble(),
                                (value1, value2) -> value1,
                                LinkedHashMap::new
                        )
                );
    }
}
