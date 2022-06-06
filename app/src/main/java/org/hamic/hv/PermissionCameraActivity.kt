package org.hamic.hv


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import org.hamic.internal.IActivity

class PermissionCameraActivity : IActivity() {

    private lateinit var goToString: AppCompatButton

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_camera)
        goToString = findViewById(R.id.button_go_to_settings);
        requestCameraPermission(this@PermissionCameraActivity)
    }

    override fun onStart() {
        super.onStart()
        goToString.setOnClickListener {
            val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", this@PermissionCameraActivity.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkCameraPermission(this@PermissionCameraActivity)) {
            finish()
        }
    }

}