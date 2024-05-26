package org.lt.utils;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class I18nBundleUtils {

    private static final Map<String, ResourceBundle> cache = new HashMap<>(16);

    @NonNls
    private static final String BUNDLE_NAME = "bundle.messages";

    private static volatile Locale locale = Locale.ENGLISH;

    public static synchronized void setLocale(Locale locale) {
        I18nBundleUtils.locale = locale;
    }

    public static synchronized String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
//    CommonBundle.message()
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        String bundle = BUNDLE_NAME + "_" + I18nBundleUtils.locale.getLanguage();
        ResourceBundle resourceBundle = cache.get(bundle);
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(bundle);
            cache.put(bundle, resourceBundle);
        }
        return resourceBundle;
    }
}