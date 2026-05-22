package dc.gtest.vortex.support;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyJsonParser {

//    public static String getStringValue(JSONObject oneObject, String key, String defaultValue) {
//        try {
//            if (oneObject.has(key) && !oneObject.getString(key).toLowerCase().replace(" ", "").equals("null")) {
//                defaultValue = oneObject.getString(key);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return defaultValue;
//    }

    public static String getStringValue(JSONObject oneObject, String key, String defaultValue) {
        if (oneObject == null || key == null) {
            return defaultValue;
        }

        Object value = oneObject.opt(key);

        if (value == null || value == JSONObject.NULL) {
            return defaultValue;
        }

        String stringValue = String.valueOf(value);

        if (stringValue.trim().equalsIgnoreCase("null")) {
            return defaultValue;
        }

        return stringValue;
    }

    public static int getIntValue(JSONObject oneObject, String key, int defaultValue) {
        try {
            if (oneObject.has(key) && !oneObject.getString(key).toLowerCase().replace(" ", "").equals("null")) {
                defaultValue = oneObject.getInt(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static double getDoubleValue(JSONObject oneObject, String key, double defaultValue) {
        try {
            if (oneObject.has(key) && !oneObject.getString(key).toLowerCase().replace(" ", "").equals("null")) {
                defaultValue = oneObject.getDouble(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static boolean getBooleanValue(JSONObject oneObject, String key, boolean defaultValue) {
        try {
            if (oneObject.has(key) && !oneObject.getString(key).toLowerCase().replace(" ", "").equals("null")) {
                defaultValue = oneObject.getBoolean(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static JSONArray getJsonArrayValue(JSONObject oneObject, String key) {
        try {
            if (oneObject.has(key) && !oneObject.getString(key).toLowerCase().replace(" ", "").equals("null")) {
                return oneObject.getJSONArray(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }
}
