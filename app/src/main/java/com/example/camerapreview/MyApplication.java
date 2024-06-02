package com.example.camerapreview;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    public final static int PERMISSION_REQUEST_CODE_CAMERA = 101;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
