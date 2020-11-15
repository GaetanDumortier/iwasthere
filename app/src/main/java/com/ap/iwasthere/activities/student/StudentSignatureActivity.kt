package com.ap.iwasthere.activities.student

import android.Manifest
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.LocationHelper
import com.ap.iwasthere.helpers.PermissionHelper
import com.ap.iwasthere.helpers.SignatureHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.CanvasView
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.livedatapermission.PermissionManager

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
    var location: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signature)

        location = LocationHelper(this).getLastLocation()

        //region Observers
        NetworkObserver(applicationContext).observe(layoutStudentSignature, this)
        //endregion

        val i = intent
        val student = i.getParcelableExtra<Student>("student")
        if (student == null) {
            returnToSelectActivity()
        }

        //region UI
        supportActionBar?.title = getString(R.string.title_student_signature, student!!.firstName)
        initializeCanvas()
        //endregion

        //region View Listeners
        /**
         * StudentSignatureDone: OnClickListener.
         * Will attempt to save the signature to send to the database.
         */
        btnSignatureDone.setOnClickListener {
            if (!canvasView.canvasIsEmpty() && SignatureHelper(
                    this,
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
        //endregion
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
        studentSelectIntent.putExtra("reload", true)
        this.finish()
    }

    //region Override functions
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
    //endregion
}