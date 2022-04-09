package com.example.smstest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.esafirm.imagepicker.model.Image
import com.example.smstest.util.sendMMS
import java.lang.Exception

class CPhoneStateListener(private val context: Context, private var image: Image?, private val listener: () -> Unit): PhoneStateListener() {
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
                listener()
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.e("CPhoneStateListener", "IDLE")

                if(prevState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    try {
//                        SmsManager.getDefault().sendTextMessage(phoneNumber, null, "통화 종료", null, null)
                        image?.let { sendMMS(context, phoneNumber, it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        prevState = state
    }
}