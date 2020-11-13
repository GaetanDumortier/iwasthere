package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.models.Student
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signature)
        NetworkObserver(applicationContext).observe(layoutStudentSignature, this)

        val i = intent
        val student = i.getParcelableExtra<Student>("student")
        if (student != null) {
            supportActionBar?.title = getString(R.string.title_student_signature, student.firstName)
        } else {
            startSelectActivity()
        }
    }

    private fun startSelectActivity() {
        val studentSelectIntent = Intent(this, StudentSelectActivity::class.java)
        startActivity(studentSelectIntent)
    }
}