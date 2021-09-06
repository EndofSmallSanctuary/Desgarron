package com.example.desgarron.Logic.SigurComponents;

import android.content.Context;
import android.provider.Settings;

public class DeviceId {
    public static String get(Context context) {
        String upperCase = Settings.Secure.getString(context.getContentResolver(), "android_id").toUpperCase();
        while (upperCase.length() < 16) {
            upperCase = upperCase + "0";
        }
        if (upperCase.length() % 2 == 0) {
            return upperCase;
        }
        return upperCase + "0";
    }

    private DeviceId() {
    }
}
