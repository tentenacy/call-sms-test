package com.example.smstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val callOnClickListener: (View?) -> Unit = {
        requestCallPhonePermission()
    }

    private val call = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode) {
            RESULT_OK -> {

            }
            RESULT_CANCELED -> {

            }
            else -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnMainCall.setOnClickListener(callOnClickListener)
    }

    private fun requestCallPhonePermission() {
        if (isCallPhonePermissionGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 200)
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

    private fun  isCallPhonePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED

}