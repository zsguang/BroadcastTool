package com.zsg.broadcasttool.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    public static final String AUTO_RECONNECT = "autoReconnect";
    public static final String DISCONNECT_TIME = "disconnectTime";
    public static final String DISCONNECT_TIME_INDEX = "disconnectTimeIndex";

    private static SPUtils mInstance;
    private static SharedPreferences sp;

    private SPUtils(Context context) {
        sp = getSP(context);
    }

    public static SPUtils getInstance(Context context) {
        if(mInstance == null) {
            synchronized (SPUtils.class) {
                if(mInstance == null)
                    mInstance = new SPUtils(context);
            }
        }
        return mInstance;
    }

    public boolean containsSPKey(String key) {
        return sp.contains(key);
    }

    public boolean removeSPKey(String key) {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(key);
        return editor.commit();
    }

    public void setSPString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSPString(String key) {
        return sp.getString(key, "");
    }

    public String getSPString(String key, String defVal) {
        return sp.getString(key, defVal);
    }

    public boolean setSPBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getSPBoolean(String key, boolean defVal) {
        return sp.getBoolean(key, defVal);
    }

    public boolean setSPInt(String key, int value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        return editor.commit();
    }

    public int getSPInt(String key, int defVal) {
        return sp.getInt(key, defVal);
    }

    public boolean setSPLong(String key, long value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        return editor.commit();
    }

    public long getSPLong(String key, long defVal) {
        return sp.getLong(key, defVal);
    }

    public boolean setSPFloat(String key, float value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float getSPFloat(String key, float defVal) {
        return sp.getFloat(key, defVal);
    }

    private static SharedPreferences getSP(Context context) {
        String preferencesName = context.getPackageName() + "_preferences";     // com.rscja.btwedge_preferences
        return context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return sp.edit();
    }
}
