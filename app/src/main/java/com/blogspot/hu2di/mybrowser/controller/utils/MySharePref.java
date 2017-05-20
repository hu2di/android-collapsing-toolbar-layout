package com.blogspot.hu2di.mybrowser.controller.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HUNGDH on 5/18/2017.
 */

public class MySharePref {

    private static final String SETTINGS = "com.blogspot.hu2di.mybrowser.settings";

    private static final String KEY_NEWS = "news";

    public static boolean getIsNews(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_NEWS, true);
    }

    public static boolean putIsNews(Context context, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_NEWS, value);
        return editor.commit();
    }
}
