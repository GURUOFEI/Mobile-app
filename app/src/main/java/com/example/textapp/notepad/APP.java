package com.example.textapp.notepad;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.textapp.notepad.utils.LogUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Application
 */
public class APP extends Application {
    /**
     * APP Context
     */
    public static Context context;

    /**
     * 是否Debug，方便测试
     */
    public final static boolean isDebut = true;


    @Override
    public void onCreate() {
        super.onCreate();
        APP.context = this;

        initGoogleMap();

    }

    /**
     * 初始化谷歌地图
     */
    private void initGoogleMap() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyCy-k7nsCPN00whjrj6DJZwwjlafFxozRs");
    }
}
