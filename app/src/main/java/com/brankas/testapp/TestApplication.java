package com.brankas.testapp;

import android.app.Application;

import as.brank.sdk.tap.statement.StatementTapSDK;

public class TestApplication extends Application {
    private boolean isDebug = false;
    private static TestApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TestApplication getInstance() {
        if (instance== null) {
            synchronized(TestApplication.class) {
                if (instance == null)
                    instance = new TestApplication();
            }
        }
        // Return the instance
        return instance;
    }

    public void updateTap(boolean isDebug) {
        this.isDebug = isDebug;
        StatementTapSDK.INSTANCE.initialize(this, isDebug? Constants.API_KEY_SANDBOX:
                Constants.API_KEY, null, isDebug);
    }

    public boolean isDebug() {
        return isDebug;
    }
}