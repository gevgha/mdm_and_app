package com.gevorg.mdm_and_app

import android.app.Application
import android.content.Intent
import android.os.Build


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, MyService::class.java))
        } else {
            startService(Intent(this, MyService::class.java))
        }
    }
}