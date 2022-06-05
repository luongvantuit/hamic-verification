package org.hamic.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class IActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestCameraPermission(
        context: Context,
        requestCode: Int? = requestCodeCameraPermission,
        grantedCallBack: IGrantedCallBack?
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            grantedCallBack!!.on()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode!!)
        }
    }

    fun checkCameraPermission(context: Context): Int {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
    }

    companion object {
        const val requestCodeCameraPermission: Int = 30
    }
}