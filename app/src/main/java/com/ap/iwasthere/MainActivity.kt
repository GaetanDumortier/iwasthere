package com.ap.iwasthere

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.helpers.PermissionHelper
import com.google.firebase.database.FirebaseDatabase

/**
 * Main entrypoint of the application.
 *
 * @author Gaetan Dumortier
 * @since 12 November 2020
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance()
        PermissionHelper(this).checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionHelper(this).onRequestPermissionsResultHandler(grantResults)
    }
}
