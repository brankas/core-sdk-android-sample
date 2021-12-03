package com.brankas.testapp

import `as`.brank.sdk.tap.statement.StatementTapSDK
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
        StatementTapSDK.initialize(this, if(isDebug) Constants.API_KEY_SANDBOX else
            Constants.API_KEY, null, isDebug)
    }

    fun getDestinationAccountId(): String {
        return "";
    }
}