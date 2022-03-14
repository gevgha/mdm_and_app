package com.gevorg.mdm_and_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.e("TAG", "App onReceive: "+intent?.getStringExtra("data") )
//        context?.getSharedPreferences("app",Context.MODE_PRIVATE)?.edit().putString()
    }
}