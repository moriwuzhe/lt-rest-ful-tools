package org.lt.config;

import com.intellij.ide.util.PropertiesComponent;

public abstract class LTConfig {

    private static final PropertiesComponent propertiesComponent
            = PropertiesComponent.getInstance();

    private LTConfig() {
    }

    public boolean contains(String key) {
        return propertiesComponent.isValueSet(key);
    }

    public static void set(String key, String value) {
        propertiesComponent.setValue(key, value);
    }

    public static String getValue(String key) {
        return getValue(key, null);
    }

    public static String getValue(String key, String defaultValue) {
        return propertiesComponent.getValue(key, defaultValue);
    }

    public static String[] getValues(String key) {
        return propertiesComponent.getValues(key);
    }

    public final boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public final boolean getBoolean(String name, boolean defaultValue) {
        return propertiesComponent.getBoolean(name, defaultValue);
    }

    public final float getFloat(String name, float defaultValue) {
        return propertiesComponent.getFloat(name, defaultValue);
    }

    public final float getInt(String name, int defaultValue) {
        return propertiesComponent.getInt(name, defaultValue);
    }

    public final float getLong(String name, long defaultValue) {
        return propertiesComponent.getLong(name, defaultValue);
    }

}
