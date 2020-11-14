package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.SignatureHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.CanvasView
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.student_signature.*

/**
 * Activity class which will provide a drawable canvas for the user
 * to draw their signature. On submission, various checks, as well as IP geo-locating
 * and database insertion will be executed.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class StudentSignatureActivity : AppCompatActivity() {
    private lateinit var canvasView: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signature)
        NetworkObserver(applicationContext).observe(layoutStudentSignature, this)

        val student = intent.getParcelableExtra<Student>("student")
        if (student == null) {
            returnToSelectActivity()
        }

        //
        // UI
        //
        supportActionBar?.title = getString(R.string.title_student_signature, student!!.firstName)
        initializeCanvas()

        //
        // View Listeners
        //

        /**
         * StudentSignatureDone: OnClickListener.
         * Will attempt to save the signature to send to the database.
         */
        btnSignatureDone.setOnClickListener {
            if (!canvasView.canvasIsEmpty() && SignatureHelper(
                    applicationContext,
                    canvasView
                ).saveSignature(student)
            ) {
                val selectIntent = Intent(this, SignatureSubmittedActivity::class.java)
                startActivity(selectIntent)
                this.finish()
            } else {
                SnackbarHelper(canvasView).makeAndShow(
                    getString(R.string.signature_error),
                    Snackbar.LENGTH_LONG
                )
            }
        }

        /**
         * StudentSignature: OnClickListener.
         * Will clear the canvas when the reset button is clicked.
         */
        btnSignatureReset.setOnClickListener { canvasView.clear() }
    }

    /**
     * Initialize and set the CanvasView for the current context and
     * add it to the view for the user to draw their signature on.
     */
    private fun initializeCanvas() {
        canvasView = CanvasView(this)
        signatureCanvas.addView(canvasView, 0)
        canvasView.requestFocus()
    }

    private fun returnToSelectActivity() {
        val studentSelectIntent = Intent(this, StudentSelectActivity::class.java)
        startActivity(studentSelectIntent)
        this.finish()
    }

    /**
     * Send an intent back to the StudentSelectActivity to prevent another student from
     * re-opening the app (if it was left on the sign-page) and signing for someone else.
     */
    override fun onRestart() {
        super.onRestart()
        returnToSelectActivity()
    }

    /**
     * Send an intent back to the StudentSelectActivity to prevent another student from
     * re-opening the app (if it was left on the sign-page) and signing for someone else.
     */
    override fun onStop() {
        super.onStop()
        this.finish()
    }
}