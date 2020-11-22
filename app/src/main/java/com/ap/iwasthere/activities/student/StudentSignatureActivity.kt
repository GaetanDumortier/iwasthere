package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SignatureHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.CanvasView
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.student_signature.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Activity class which will provide a drawable canvas for the user
 * to draw their signature. On submission, various checks, as well as IP geo-locating
 * and database insertion will be executed.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class StudentSignatureActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var canvasView: CanvasView
    private lateinit var student: Student

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signature)

        //region Observers
        NetworkObserver(applicationContext).observe(layoutStudentSignature, this)
        //endregion

        student = intent.getParcelableExtra("student")!!

        //region UI
        supportActionBar?.title = getString(R.string.title_student_signature, student.firstName)
        initializeCanvas()
        //endregion

        checkFirstSignature()

        //region View Listeners
        /**
         * StudentSignatureDone: OnClickListener.
         * Will attempt to save the signature to send to the database.
         */
        btnSignatureDone.setOnClickListener {
            launch {
                if (!canvasView.canvasIsEmpty() && SignatureHelper(
                        this@StudentSignatureActivity,
                        canvasView
                    ).saveSignature(student)
                ) {
                    val selectIntent = Intent(this@StudentSignatureActivity, SignatureSubmittedActivity::class.java)
                    startActivity(selectIntent)
                    this@StudentSignatureActivity.finish()
                } else {
                    SnackbarHelper(canvasView).makeAndShow(
                        getString(R.string.signature_error),
                        Snackbar.LENGTH_LONG
                    )
                }
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

    private fun checkFirstSignature() {
        val alert = UIUtils().buildAlertDialog(
            this,
            "Privacy waarschuwing",
            "Wanneer je verder gaat zal de app je locatie meesturen. Dit is nodig voor een correcte registratie."
        )
        alert.setPositiveButton("Oke") { dialog, _ -> dialog.dismiss() }
        alert.setNegativeButton("Dit wil ik niet") { dialog, _ -> dialog.cancel() }
        alert.setOnCancelListener { returnToSelectActivity() }
        FirebaseHelper().fetchAllSignaturesFromUser(student, object : FirebaseCallback.ListCallback {
            override fun onListCallback(value: List<Any>) {
                if (value.isEmpty()) {
                    alert.show()
                }
            }
        })
    }

    private fun returnToSelectActivity() {
        val studentSelectIntent = Intent(this, StudentSelectActivity::class.java)
        startActivity(studentSelectIntent)
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