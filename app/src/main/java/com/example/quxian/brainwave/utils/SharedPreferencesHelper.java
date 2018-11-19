package com.example.quxian.brainwave.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 储存数据 清除缓存
 */
public class SharedPreferencesHelper {

    private static final String sharedPreferences_mainName = "skyworth";

    private static final String regularEx = "#~";

    public static final String LOGIN_STATUS = "login";

    public static final String USER_ACCOUNT = "userAccount";

    public static final String USER_PASSWORD = "userPassword";

    public static final String HEIGHT = "height";

    public static final String AGE = "age";

    public static final String WEIGHT = "weight";


    public static String getString(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static String[] getStringSet(Context c, String key) {
        // 将数据保存至SharedPreferences
        String[] str = null;
        SharedPreferences sp = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "");
        str = values.split(regularEx);

        return str;
    }


    public static Boolean addStringSet(Context c, String key, String data) {
        String[] tmp = SharedPreferencesHelper.getStringSet(c, key);
        String tmp_all = "";
        for (int i = 1; i < tmp.length; i++) {
            tmp_all = tmp_all + regularEx + tmp[i];
        }
        tmp_all = tmp_all + regularEx + data;
        SharedPreferencesHelper.put(c, key, tmp_all);
        return true;
    }

    public static Boolean removeStringSet(Context c, String key, String data) {
        String[] tmp = SharedPreferencesHelper.getStringSet(c, key);
        Boolean res = false;
        String tmp_all = "";
        for (int i = 1; i < tmp.length; i++) {
            if (tmp[i].equals(data)) {
                res = true;
                i = i + 2; // (改了这里?)
                continue;
            }
            tmp_all = tmp_all + regularEx + tmp[i];
        }
        SharedPreferencesHelper.put(c, key, tmp_all);
        return res;
    }

    public static Boolean clearStringSet(Context c, String key) {
        String[] tmp = SharedPreferencesHelper.getStringSet(c, key);
        if (tmp != null) {
            String empty = "";
            SharedPreferencesHelper.put(c, key, empty);
            return true;
        } else {
            return false;
        }
    }

    public static boolean getBool(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static int getInt(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static long getLong(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    public static void put(Context c, String key, int value) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void put(Context c, String key, boolean value) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void put(Context c, String key, long value) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void put(Context c, String key, float value) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void put(Context c, String key, String value) {
        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void put(Context c, String key, String[] values) {
        StringBuilder str = new StringBuilder();
        SharedPreferences sp = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str.append(value);
                str.append(regularEx);
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str.toString());
            et.apply();
        }
    }

    public static void clear(Context c) {

        SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear().apply();

    }
}
