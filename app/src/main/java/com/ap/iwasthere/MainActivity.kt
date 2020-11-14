package com.ap.iwasthere

import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.activities.student.StudentSelectActivity
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

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

        // Initialize firebase connection
        FirebaseDatabase.getInstance()

        try {
            val intent = Intent(this, StudentSelectActivity::class.java)
            startActivity(intent)
        } catch (e: IntentSender.SendIntentException) {
            SnackbarHelper(layoutMain).makeAndShow(getString(R.string.intent_error))
            return
        }
    }
}
