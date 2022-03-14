package com.gevorg.mdm_and_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MyService :Service()  {

    private var appTimerDisposable: Disposable? = null
    private val compositeDisposable: CompositeDisposable? = null
    private var messenger: Messenger? = null

    override fun onBind(intent: Intent?): IBinder? {
        if (messenger == null) {
            synchronized(MyService::class.java) {
                if (messenger == null) {
                    messenger = Messenger(IncomingHandler())
                }
            }
        }
        //Return the proper IBinder instance
        return messenger!!.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        if (appTimerDisposable == null) {
             appTimerDisposable = Observable.interval(1, TimeUnit.HOURS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ aLong ->

                }) { throwable -> }
            compositeDisposable?.add(appTimerDisposable!!)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (appTimerDisposable != null) {
            appTimerDisposable?.dispose()
            compositeDisposable!!.delete(appTimerDisposable!!)
            appTimerDisposable = null
        }
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("mdm_service", "Mdm Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    inner class IncomingHandler : Handler(){
        override fun handleMessage(msg: Message) {
            println("*****************************************")
            println("Remote Service successfully invoked!!!!!!")
            println("*****************************************")
            val what: Int = msg.what

            //Setup the reply message
            val message: Message = Message.obtain(null, 2, 0, 0)
            try {
                //make the RPC invocation
                val replyTo: Messenger = msg.replyTo
                replyTo.send(message)
            } catch (rme: RemoteException) {
                //Show an Error Message
                Log.e("IncomingHandler", "handleMessage:  failed", )
            }
        }
    }
}