package com.example.facedetproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefManager {

    private SharedPreferences appPref;
    private SharedPreferences.Editor appPrefEditor;
    private Context mContext;

    private static final String PREF_NAME = "com.technosales.attendanceface.appPref";

    public static final String MODE = "mode";
    public static final String DEVICE_ID = "deviceId";
    public static final String PASSWORD = "devicePassword";
    public static final String URL = "url";

    public AppPrefManager(Context context) {
        this.mContext = context;
        appPref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        appPrefEditor = appPref.edit();
    }

    public void setMode(String mode) {
        appPrefEditor.putString(MODE, mode);
        appPrefEditor.apply();
    }

    public String getMode() {
        return appPref.getString(MODE, "");
    }


    public void setDeviceId(String deviceId) {
        appPrefEditor.putString(DEVICE_ID, deviceId);
        appPrefEditor.apply();
        System.out.println("set Device id is " + deviceId);
    }

    public String getDeviceId() {
        return appPref.getString(DEVICE_ID, "");
    }

    public void setPassword(String password) {
        appPrefEditor.putString(PASSWORD, password);
        appPrefEditor.apply();
        System.out.println("set password  is " + password);
    }

    public String getPassword() {
        return appPref.getString(PASSWORD, "");
    }


    public void setUrl(String url) {
        appPrefEditor.putString(URL, url);
        appPrefEditor.apply();
        System.out.println("set password  is " + url);
    }

    public String getUrl() {
        return appPref.getString(URL, "");
    }

}
