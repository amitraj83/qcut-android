package com.qcut.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {
    static public String GOOGLE_SIGN = "google";
    static public String FACEBOOK_SIGN = "facebook";
    static public String NORMAL_SIGN = "normal";

    private static SharedPreferences sharedPref;

    public SharedPrefManager (Context context) {
        String PACKNAME = "com.qcut.customer";
        sharedPref = context.getSharedPreferences(PACKNAME, MODE_PRIVATE);
    }

    static public void setStringSharedPref(String key, String value){
        sharedPref.edit().putString(key, value).apply();
    }

    static public String getStringSharedPref(String key){
        return sharedPref.getString(key, "");
    }

    static public void removeStringSharedPref(String key){
        sharedPref.edit().remove(key).apply();
    }

    static public boolean checkStringSharedPref(String key){
        if(sharedPref.getString(key, "").equals("")){
            return false;
        }
        else {
            return true;
        }
    }
}
