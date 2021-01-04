package com.ap.iwasthere.helpers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ap.iwasthere.activities.PermissionErrorActivity
import com.ap.iwasthere.activities.student.StudentSelectActivity

/**
 * A class which is responsible for handling permissions for the application.
 * It implements a method to check the state of a required permission, and a handler
 * for when the user chooses Allow/Deny.
 *
 * @author Gaetan Dumortier
 * @since 14 November 2020
 */
class PermissionHelper(private val activity: AppCompatActivity) {
    fun checkOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$activity.packageName"))
        activity.startActivity(intent)
    }

    /**
     * Check the current permissions of the application against the required permission.
     * If they are not granted, it will prompt again.
     * Else it will navigate to the StudentSelect activity
     */
    fun checkPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                val intent = Intent(activity, StudentSelectActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            else -> {
                activity.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    /**
     * Handle the callback when the user chooses an option in the permission prompt window.
     * If the user allowed the permission, navigate to the StudentSelect activity.
     * If they denied, ask again. If permanently denied, navigate to PermissionError activity.
     *
     * @param grantResults an array containing the permission state the user decided on (PERMISSION_GRANTED, PERMISSION_DENIED)
     */
    fun onRequestPermissionsResultHandler(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(activity, StudentSelectActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        } else {
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            ) {
                // User denied permissions once
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                    1
                )
            } else {
                // User denied permissions permanently
                val intent = Intent(activity, PermissionErrorActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }

        return
    }
}