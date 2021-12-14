package com.brankas.testapp;

import android.app.Application;
import as.brank.sdk.tap.direct.DirectTapSDK;

public class TestApplication extends Application {
    private boolean isDebug = true;
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
        DirectTapSDK.INSTANCE.initialize(this, isDebug ? Constants.API_KEY_SANDBOX_DIRECT :
            Constants.API_KEY_DIRECT, null, isDebug);
    }

   public String getDestinationAccountId() {
        return isDebug ? Constants.DESTINATION_ACCOUNT_ID_SANDBOX :
                Constants.DESTINATION_ACCOUNT_ID;
    }

    public boolean isDebug() {
        return isDebug;
    }
}