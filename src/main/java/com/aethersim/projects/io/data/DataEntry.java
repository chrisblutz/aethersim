package com.aethersim.projects.io.data;


import com.aethersim.projects.io.data.exceptions.DataException;

public interface DataEntry {

    /**
     * This method returns the raw data within this entry.  It is intended for internal use, and should be avoided if
     * possible in favor of one of the other {@code get} methods.
     *
     * @return The raw {@link Object} for this entry
     */
    Object getRaw();

    /**
     * This method determines if the data stored in this entry is a scalar (i.e. not a map or array).
     *
     * @return {@code true} if the data in this entry is a scalar, {@code false} otherwise
     */
    default boolean isScalar() {
        return this instanceof DataValue;
    }

    /**
     * This method determines if the data stored in this entry is a map.
     *
     * @return {@code true} if the data in this entry is a map, {@code false} otherwise
     */
    default boolean isMap() {
        return this instanceof DataMap;
    }

    /**
     * This method determines if the data stored in this entry is an array.
     *
     * @return {@code true} if the data in this entry is an array, {@code false} otherwise
     */
    default boolean isArray() {
        return this instanceof DataArray;
    }

    /**
     * This method returns the {@link DataValue} contained in this entry, if applicable.  If the data contained in this
     * entry is not of that type, this method will throw an exception.
     *
     * @return The {@link DataValue} contained in this entry
     * @see #isScalar()
     * @see #isMap()
     * @see #isArray()
     */
    default DataValue getScalar() {
        if (!isMap())
            throw new DataException("Cannot marshal non-scalar value to DataValue.");

        return (DataValue) this;
    }

    /**
     * This method returns the {@link DataMap} contained in this entry, if applicable.  If the data contained in this
     * entry is not of that type, this method will throw an exception.
     *
     * @return The {@link DataMap} contained in this entry
     * @see #isScalar()
     * @see #isMap()
     * @see #isArray()
     */
    default DataMap getMap() {
        if (!isMap())
            throw new DataException("Cannot marshal non-map value to DataMap.");

        return (DataMap) this;
    }

    /**
     * This method returns the {@link DataArray} contained in this entry, if applicable.  If the data contained in this
     * entry is not of that type, this method will throw an exception.
     *
     * @return The {@link DataArray} contained in this entry
     * @see #isScalar()
     * @see #isMap()
     * @see #isArray()
     */
    default DataArray getArray() {
        if (!isArray())
            throw new DataException("Cannot marshal non-array value to DataArray.");

        return (DataArray) this;
    }

    void dispose();
}
