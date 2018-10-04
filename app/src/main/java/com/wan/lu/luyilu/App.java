package com.wan.lu.luyilu;

import android.app.Application;
import android.content.Context;


public class App extends Application  {

    public static App instance;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }


    public static Application getApplication() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

}
