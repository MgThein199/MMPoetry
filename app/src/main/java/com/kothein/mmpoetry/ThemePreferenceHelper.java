package com.kothein.mmpoetry;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemePreferenceHelper {
    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String THEME_KEY = "theme";
    private static final String THEME_STATE_KEY = "theme_state";

    public static void saveTheme(Context context, int theme) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME_KEY, theme);
        editor.apply();
    }

    public static int getTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void saveThemeState(Context context, boolean isDarkMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_STATE_KEY, isDarkMode);
        editor.apply();
    }

    public static boolean getThemeState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(THEME_STATE_KEY, false);
    }
}
