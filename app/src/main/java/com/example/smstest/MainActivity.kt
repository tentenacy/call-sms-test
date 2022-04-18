package com.example.smstest

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.MediaStore
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.smstest.databinding.ActivityMainBinding
import com.example.smstest.util.TAG_MESSAGEUTIL
import com.example.smstest.util.Transaction
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import android.widget.Toast
import java.lang.Exception
import java.lang.reflect.Method
import com.android.internal.telephony.ITelephony



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private var image: Image? = null

    private val launcher = registerImagePicker { result: List<Image> ->
        result.forEach {
            binding.fileName = it.name
            image = it
        }
    }

    private val callOnClickListener: (View?) -> Unit = {
        requestPhonePermission()
    }

    private val call = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            RESULT_OK -> {
                Log.d("TAG", "통화성공")
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
        binding.btnMainSelectimage.setOnClickListener {
            launcher.launch()
        }
        binding.btnMainSendmms.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                mSendMMS(binding.editMainPhone.text.toString())
//            }
            mSendMMS(binding.editMainPhone.text.toString())
        }
        binding.btnMainEndcall.setOnClickListener {
            //통화종료는 안 됨
            /*try {
                val telephonyService: ITelephony
                val getITelephony = telephonyManager::class.java.getDeclaredMethod("getITelephony")
                getITelephony.isAccessible = true
                telephonyService = getITelephony.invoke(telephonyManager) as ITelephony
                telephonyService.endCall()
            } catch (ignored: Exception) {
            }*/
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private val cPhoneStateListener = CPhoneStateListener(this, image)

    private fun requestPhonePermission() {
        if (isCallPhonePermissionNotGranted() ||
            isReadCallLogPermissionNotGranted() ||
            isSendSmsPermissionNotGranted() ||
            isReceiveSmsPermissionNotGranted() ||
            isReceiveMmsPermissionNotGranted() ||
            isReadSmsPermissionNotGranted() ||
            isReadExternalStoragePermissionNotGranted() ||
            isWriteExternalStoragePermissionNotGranted() ||
            isReadPhoneStatePermissionNotGranted()
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.RECEIVE_MMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                ),
                200
            )
        } else {
            try {
                val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager.listen(
                    cPhoneStateListener,
                    PhoneStateListener.LISTEN_NONE
                )
                telephonyManager.listen(
                    cPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
                )

                val intent = Intent(Intent.ACTION_CALL)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("tel:${binding.editMainPhone.text}")
                startActivity(intent)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun isCallPhonePermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReadCallLogPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_CALL_LOG
        ) != PackageManager.PERMISSION_GRANTED

    private fun isSendSmsPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReceiveSmsPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECEIVE_SMS
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReceiveMmsPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECEIVE_MMS
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReadSmsPermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReadExternalStoragePermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

    private fun isWriteExternalStoragePermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

    private fun isReadPhoneStatePermissionNotGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED

    private fun mSendMMS(phone: String?) {
        Log.d(TAG_MESSAGEUTIL, "sendMMS(Method) : " + "start")
        val subject = "제목"
        val text = "내용"

        // 예시 (절대경로) : String imagePath = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20190312-181007.png";
        val imagePath = image?.path
        Log.d(TAG_MESSAGEUTIL, "phone : $phone")
        Log.d(TAG_MESSAGEUTIL, "subject : $subject")
        Log.d(TAG_MESSAGEUTIL, "text : $text")
        Log.d(TAG_MESSAGEUTIL, "imagePath : $imagePath")

        val settings = Settings()
        settings.useSystemSending = true

        // TODO : 이 Transaction 클래스를 위에 링크에서 다운받아서 써야함
        val transaction = Transaction(this, settings)

        val message = Message(text, phone)
        message.setImage(image?.let { getBitmap(it.uri) })
        val id = Process.getThreadPriority(Process.myTid()).toLong()
        transaction.sendNewMessage(message, id)
    }

    fun getBitmap(imageUri: Uri): Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
    } else {
        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
    }
}