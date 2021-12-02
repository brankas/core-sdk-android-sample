package com.brankas.testapp

import `as`.brank.sdk.tap.direct.DirectTapSDK
import android.app.Application

class TestAppApplication: Application() {
    var isDebug = false

    companion object {
        lateinit var instance: TestAppApplication
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        updateTap(isDebug)
    }

    fun updateTap(isDebug: Boolean) {
        this.isDebug = isDebug
        DirectTapSDK.initialize(this, if(isDebug) Constants.API_KEY_SANDBOX_DIRECT else
            Constants.API_KEY_DIRECT, null, isDebug)
    }

    fun getDestinationAccountId(): String {
        return if(isDebug) Constants.DESTINATION_ACCOUNT_ID_SANDBOX else
            Constants.DESTINATION_ACCOUNT_ID
    }
}