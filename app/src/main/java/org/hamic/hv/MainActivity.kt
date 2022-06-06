package org.hamic.hv


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import org.hamic.internal.IActivity

class MainActivity : IActivity() {


    private lateinit var scannerQrCode: AppCompatButton

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannerQrCode = findViewById(R.id.button_scanner_qr_code)
    }

    override fun onStart() {
        super.onStart()

        scannerQrCode.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, ScanQrCodeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkCameraPermission(this@MainActivity)) {
            val intent = Intent(this@MainActivity, PermissionCameraActivity::class.java)
            startActivity(intent)
        }
    }

}