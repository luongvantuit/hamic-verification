package org.hamic.hv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import org.hamic.internal.IActivity

class ScanQrCodeActivity : IActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)
        val codeScannerView = findViewById<CodeScannerView>(R.id.code_scanner_view)
        codeScanner = CodeScanner(this@ScanQrCodeActivity, codeScannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
    }

    override fun onStart() {
        super.onStart()

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val data = it.text.split(" ")
                if (data.isEmpty() || data.size != 3 || "" in data) {

                    Toast.makeText(this@ScanQrCodeActivity, "QR code format is wrong!", Toast.LENGTH_SHORT).show()
                    codeScanner.startPreview()
                } else {
                    val intent: Intent =
                        Intent(this@ScanQrCodeActivity, ResultHandlerActivity::class.java)
                    intent.putExtra("id", data[0])
                    intent.putExtra("blockHash", data[1])
                    intent.putExtra("imgHash", data[2])
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkCameraPermission(this@ScanQrCodeActivity)) {
            finish()
        }
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }
}