package com.example.textapp.notepad;

import android.app.Application;
import android.content.Context;


/**
 * Application
 */
public class APP extends Application {
    /**
     * APP Context
     */
    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        APP.context = this;

    }
}
