package org.lt.utils;

import com.google.gson.*;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JsonUtils() {
    }

    public static boolean isValidJson(String json) {
        if (ToolUtils.isEmpty(json)) {
            return false;
        }
        return isValidJsonObject(json) || isValidJsonArray(json);
    }

    public static String toString(Object obj) {
        return gson.toJson(obj);
    }

    public static String format(String str) {
        JsonElement parse = JsonParser.parseString(str);
        return gson.toJson(parse);
    }

    private static boolean isGsonFormat(String targetStr, Class<?> clazz) {
        try {
            gson.fromJson(targetStr, clazz);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    public static boolean isValidJsonObject(String targetStr) {
        return isGsonFormat(targetStr, JsonObject.class);
    }

    public static boolean isValidJsonArray(String targetStr) {
        return isGsonFormat(targetStr, JsonArray.class);
    }

}