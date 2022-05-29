package com.shengj.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.shengj.common.App;
import com.socks.library.KLog;

import java.util.HashSet;
import java.util.Set;

/**
 * Preference工具
 */
public class PreferenceUtil {

    private PreferenceUtil() {
    }

    public static SharedPreferences getPreferences(Context context) {

        context = getContext(context);
//        return context.getSharedPreferences("PreferenceUtil", Context.MODE_MULTI_PROCESS);

        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(String key, String defaultValue, Context context) {

        return getPreferences(context).getString(key, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String key, Set<String> defaultValue, Context context) {
        return getPreferences(context).getStringSet(key, defaultValue);
    }


    public static int getInt(String key, int defaultValue, Context context) {
        return getPreferences(context).getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue, Context context) {
        return getPreferences(context).getLong(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue, Context context) {
        return getPreferences(context).getFloat(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue, Context context) {
        return getPreferences(context).getBoolean(key, defaultValue);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void putString(String key, String value, Context context) {
        getEditor(context).putString(key, value).apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putStringSet(String key, Set<String> value, Context context) {
        remove(key, context);
        getEditor(context).putStringSet(key, value).commit();
    }


    public static void putInt(String key, int value, Context context) {
        getEditor(context).putInt(key, value).apply();
    }

    public static void putLong(String key, long value, Context context) {
        getEditor(context).putLong(key, value).apply();
    }

    public static void putFloat(String key, float value, Context context) {
        getEditor(context).putFloat(key, value).apply();
    }

    public static void putBoolean(String key, boolean value, Context context) {
        getEditor(context).putBoolean(key, value).apply();
    }

    public static void remove(String key, Context context) {
        getEditor(context).remove(key).apply();
    }

    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }


    public static Context getContext(Context context) {

        if (context == null) {
            context = App.getInstance().getApplicationContext();
        }

        return context.getApplicationContext();

    }

    public static void putAdvShowTime(Context context, Set<String> advStrs) {

//        putStringSet("AdvShow",advStrs,context);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (String s : advStrs) {
                stringBuilder.append(s);
                stringBuilder.append(",");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        String str = stringBuilder.toString();
        KLog.e(str);
        putString("AdvShow_2", str, context);
    }

    public static Set<String> getAdvShowTime(Context context) {
//        return getStringSet("AdvShow", new HashSet<String>(),context);

        String string = getString("AdvShow_2", "", context);
        Set<String> set = new HashSet<>();
        try {
            String[] strings = string.split(",");
            if (strings.length > 0) {
                for (String s : strings) {
                    if (!TextUtils.isEmpty(s)) {
                        set.add(s);
                    }

                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return set;

    }

    /**
     * 存储漫画供养章节提示时间
     *
     * @param comicId
     * @param time
     */
    public static void putSupportTime(String comicId, long time) {
        App.getInstance().getApplicationContext().getSharedPreferences("PreferenceSupport",
                Context.MODE_PRIVATE).edit().putLong(comicId, time).apply();
    }

    public static long getSupportTime(String comicId) {
        return App.getInstance().getApplicationContext().getSharedPreferences("PreferenceSupport",
                Context.MODE_PRIVATE).getLong(comicId, 0);
    }

}
