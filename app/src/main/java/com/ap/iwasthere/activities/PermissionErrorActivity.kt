package com.ap.iwasthere.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.PermissionHelper
import kotlinx.android.synthetic.main.permission_error.*


/**
 * Activity which gets called if the application has insufficient permissions.
 * It will allow the user to be prompted again and allow the permissions as required.
 *
 * @author Gaetan Dumortier
 * @since 14 November 2020
 */
class PermissionErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_error)

        btnPermissionRetry.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)

            PermissionHelper(this).checkPermissions()
        }
    }
}