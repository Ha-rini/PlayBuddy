package com.example.play_buddy_v2;

import android.app.Application;
import android.os.Bundle;

import android.app.Application ;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application  {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
    }
}

