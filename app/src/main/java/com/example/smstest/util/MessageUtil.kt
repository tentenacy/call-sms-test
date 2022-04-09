package com.example.smstest.util

import android.content.Context
import android.graphics.BitmapFactory

import android.os.Process
import android.util.Log
import com.esafirm.imagepicker.model.Image
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings

val TAG_MESSAGEUTIL = "MessageUtil"

fun sendMMS(context: Context, phone: String?, image: Image) {
    Log.d(TAG_MESSAGEUTIL, "sendMMS(Method) : " + "start")
    val subject = "제목"
    val text = "내용"

    // 예시 (절대경로) : String imagePath = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20190312-181007.png";
    val imagePath = image.path
    Log.d(TAG_MESSAGEUTIL, "subject : $subject")
    Log.d(TAG_MESSAGEUTIL, "text : $text")
    Log.d(TAG_MESSAGEUTIL, "imagePath : $imagePath")
    val settings = Settings()
    settings.useSystemSending = true

    // TODO : 이 Transaction 클래스를 위에 링크에서 다운받아서 써야함
    val transaction = Transaction(context, settings)

    // 제목이 있을경우
    val message = Message(text, phone, subject)

    // 제목이 없을경우
    // Message message = new Message(text, number);
    if (!(imagePath == "" || imagePath == null)) {
        // 예시2 (앱 내부 리소스) :
        // Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mms_test_1);
        val mBitmap = BitmapFactory.decodeFile(imagePath)
        // TODO : image를 여러장 넣고 싶은경우, addImage를 계속호출해서 넣으면됨
        message.addImage(mBitmap)
    }
    val id = Process.getThreadPriority(Process.myTid()).toLong()
    transaction.sendNewMessage(message, id)
}