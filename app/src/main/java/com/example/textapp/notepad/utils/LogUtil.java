package com.example.textapp.notepad.utils;

import android.util.Log;

/**
 * log tool
 */
public class LogUtil {
    /**
     * log tag
     */
    public static String TAG = "testlog";

    /**
     * debug log
     *
     * @param message
     */
    public static void d(String message) {
        Log.d(TAG, message);
    }
}
