package com.example.smstest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver: BroadcastReceiver() {

    private var phoneState: String? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("CallReceiver", "${intent?.action}")

        if(intent?.action.equals("android.intent.action.PHONE_STATE")) {
            val telecomManager =
                context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

            intent?.extras?.let {
                val state = it.getString(TelephonyManager.EXTRA_STATE)
                if(state.equals(phoneState)) return else phoneState = state
                when {
                    state.equals(TelephonyManager.EXTRA_STATE_RINGING) -> {
                        val phoneNumber = it.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        Log.d("CallReceiver", "통화벨 울리는 중")
                    }
                    state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) -> {
                        Log.d("CallReceiver", "통화중")
                    }
                    else -> {
                        Log.d("CallReceiver", "통화종료 혹은 통화벨 종료")
                    }
                }

                Log.d("CallReceiver", "phone state: $state")
//                Log.d("CallReceiver", "phone currentPhoneState: $currentPhoneState")

            }
        }
    }
}