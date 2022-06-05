package org.hamic.hv


import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import org.hamic.internal.IActivity
import org.hamic.internal.IGrantedCallBack

class MainActivity : IActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestCameraPermission(
            context = this@MainActivity,
            requestCode = requestCodeCameraPermission,
            grantedCallBack = object : IGrantedCallBack {
                override fun on() {

                }
            });
    }

}