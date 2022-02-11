package com.example.smstest

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import java.lang.Exception

object CPhoneStateListener: PhoneStateListener() {
    var prevState: Int = 0
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        Log.e("CPhoneStateListener", state.toString())
        Log.e("phoneNumber", "$phoneNumber")
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.e("CPhoneStateListener", "RINGING")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.e("CPhoneStateListener", "OFFHOOK")
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.e("CPhoneStateListener", "IDLE")
                if(prevState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    try {
                        SmsManager.getDefault().sendTextMessage(phoneNumber, null, "통화 종료", null, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        prevState = state
    }
}