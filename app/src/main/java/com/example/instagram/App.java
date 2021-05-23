package com.example.instagram;

import android.app.Application;
import android.app.LauncherActivity;

import com.parse.Parse;
import com.parse.ParseUser;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Xbw7w4GLl3JXQdtEsylcdbztO8GpMSdl0Wgikcgc")
                .clientKey("EfjuvFnZUECCnawT1wd7rGxglZmZE3aWDkQMtEGo")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
