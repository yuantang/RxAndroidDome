package com.tom.rxdome;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;

/**
 * Created by tom on 2016/1/25.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }
}
