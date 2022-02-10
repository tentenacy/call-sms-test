package com.example.smstest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.telephony.TelephonyManager

class CallService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(receiver == null) {
            receiver = CallReceiver()
            registerReceiver(receiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        receiver = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {

        private var receiver: CallReceiver? = null

        fun start(context: Context) {
            if(receiver == null) {
                context.startService(Intent(context, CallService::class.java))
            }
        }
    }
}