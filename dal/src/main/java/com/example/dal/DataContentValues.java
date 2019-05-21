package com.example.dal;

import com.example.common.ConstantsKt;
import com.example.common.model.SimpleDto;
import com.example.common.serialization.JsonSerializer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public final class DataContentValues {
    public static final String TAG = "ContentValues";

    /**
     * Holds the actual values
     */
    private HashMap<String, Object> mValues;

    public static DataContentValues fromDto(JsonSerializer serializer, SimpleDto dto) {
        String attributes = serializer.toJson(dto);
        byte[] json = new byte[0];
        try {
            json = attributes.getBytes(ConstantsKt.ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DataContentValues values = new DataContentValues();
        values.put("attributes", json);

        return values;
    }

    /**
     * Creates an empty set of values using the default initial size
     */
    public DataContentValues() {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new HashMap<String, Object>(8);
    }

    /**
     * Creates an empty set of values using the given initial size
     *
     * @param size the initial size of the set of values
     */
    public DataContentValues(int size) {
        mValues = new HashMap<String, Object>(size, 1.0f);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataContentValues)) {
            return false;
        }
        return mValues.equals(((DataContentValues) object).mValues);
    }

    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, String value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Byte value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Short value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Float value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Double value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key   the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, byte[] value) {
        mValues.put(key, value);
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public void putNull(String key) {
        mValues.put(key, null);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return mValues.size();
    }

    /**
     * Indicates whether this collection is empty.
     *
     * @return true iff size == 0
     * {@hide}
     * TODO: consider exposing this new method publicly
     */
    public boolean isEmpty() {
        return mValues.isEmpty();
    }

    public HashMap<String, Object> getValues() {
        return this.mValues;
    }

    public Object getValue(String key) {
        return mValues.get(key);
    }

    public byte[] getBlobValue(String key) {
        return (byte[])mValues.get(key);
    }

    public void clear() {
        this.mValues.clear();
    }
}
