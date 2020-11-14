package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import kotlinx.android.synthetic.main.signature_done.*

/**
 * Activity class which just simply displays a success-message
 * and a button to forward back to the startpage (StudentSelectActivity)
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class SignatureSubmittedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signature_done)
        supportActionBar?.title = getString(R.string.title_signature_done)

        btnRestart.setOnClickListener {
            val intent = Intent(this, StudentSelectActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }
}