package com.example.smstest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val callOnClickListener: (View?) -> Unit = {
        requestPhonePermission()
    }

    private val call = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode) {
            RESULT_OK -> {}
            RESULT_CANCELED -> {}
            else -> {}
        }
    }

    private val telephonyManager: TelephonyManager by lazy {
        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListeners()

        telephonyManager.listen(CPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun setOnClickListeners() {
        binding.btnMainCall.setOnClickListener(callOnClickListener)
    }

    private fun requestPhonePermission() {
        if (isCallPhonePermissionNotGranted() || isReadCallLogPermissionNotGranted() || isSendSmsPermissionNotGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.SEND_SMS), 200)
        } else {
            try {
                call.launch(
                    Intent(
                        "android.intent.action.CALL",
                        Uri.parse("tel:${binding.editMainPhone.text}")
                    )
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun isCallPhonePermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED

    private fun isReadCallLogPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED

    private fun isSendSmsPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
}