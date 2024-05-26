package org.lt.utils;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.Nullable;

public class MyPersistentPluginState implements PersistentStateComponent<MyPersistentPluginState> {
    private String mySetting = "default_lt_config";

    @Nullable
    @Override
    public MyPersistentPluginState getState() {
        return this;
    }

    @Override
    public void loadState(MyPersistentPluginState state) {
        // Load the state
        mySetting = state.mySetting;
    }

    // Accessor methods for your configuration data
    public String getMySetting() {
        return mySetting;
    }

    public void setMySetting(String value) {
        mySetting = value;
    }
}
