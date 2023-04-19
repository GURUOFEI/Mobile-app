package com.example.textapp.notepad.utils;

import android.widget.Toast;

import com.example.textapp.notepad.APP;


public class ToastUtil {
    /**
     * @param message
     */
    public static void show(String message) {
        Toast.makeText(APP.context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param res
     */
    public static void show(int res) {
        Toast.makeText(APP.context, res, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param message
     */
    public static void showLong(String message) {
        Toast.makeText(APP.context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * @param res
     */
    public static void showLong(int res) {
        Toast.makeText(APP.context, res, Toast.LENGTH_LONG).show();
    }
}
