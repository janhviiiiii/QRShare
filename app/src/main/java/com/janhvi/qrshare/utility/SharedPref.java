package com.janhvi.qrshare.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_ID = "Uid";
    private static final String USER_NAME = "Username";
    private static final String USER_EMAIL = "Email";

    public static SharedPreferences sharedPreferences(Context con) {
        return con.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static void setIsLoggedIn(Context con, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences(con).edit();
        editor.putBoolean(IS_LOGGED_IN, value);
        editor.apply();
    }

    public static boolean getIsLoggedIn(Context con) {
        return sharedPreferences(con).getBoolean(IS_LOGGED_IN, false);
    }

    public static void setUuid(Context con, String uid) {
        SharedPreferences.Editor editor = sharedPreferences(con).edit();
        editor.putString(USER_ID, uid);
        editor.apply();
    }

    public static String getUserUid(Context con) {
        return sharedPreferences(con).getString(USER_ID, "");
    }

    public static void setUsername(Context con, String username) {
        SharedPreferences.Editor editor = sharedPreferences(con).edit();
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    public static String getUserName(Context con) {
        return sharedPreferences(con).getString(USER_NAME, "");
    }

    public static void setUserEmail(Context con, String userEmail) {
        SharedPreferences.Editor editor = sharedPreferences(con).edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.apply();
    }

    public static String getUserEmail(Context con) {
        return sharedPreferences(con).getString(USER_EMAIL, "");
    }

    public static void deleteAll(Context context) {
        SharedPreferences.Editor editor = sharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}