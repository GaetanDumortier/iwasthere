package com.ap.iwasthere.activities.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.models.adapters.SignatureAdapter
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.signature_overview.layoutAdminStudentSignatures
import kotlinx.android.synthetic.main.signature_overview.signatureOverviewList
import kotlinx.android.synthetic.main.student_signatures.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StudentAllSignatures : AppCompatActivity() {
    private lateinit var signatureAdapter: SignatureAdapter
    private lateinit var student: Student
    private var signatures: ArrayList<Signature> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signatures)
        NetworkObserver(applicationContext).observe(layoutAdminStudentSignatures, this)

        student = intent.extras!!.get("student") as Student

        //region UI
        UIUtils().configureSupportActionBar(this, getString(R.string.item_signatures))

        txtStudentName.text = student.fullName
        signatureAdapter = SignatureAdapter(this, R.layout.student_signature_row, signatures)
        signatureOverviewList.adapter = signatureAdapter
        //endregion

        //region Database
        getAllSignatures()
        //endregion
    }

    /**
     * Get all all students and their signatures from the database
     */
    private fun getAllSignatures() {
        FirebaseHelper().fetchAllSignaturesFromUser(student, object : FirebaseCallback.ListCallback {
            override fun onListCallback(value: List<Any>) {
                signatures.clear()
                val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                for (s in value) {
                    signatures.add(s as Signature)
                }
                signatures.sortWith(Comparator { s1: Signature, s2: Signature ->
                    LocalDateTime.parse(s2.date!!, dateFormat).compareTo(LocalDateTime.parse(s1.date!!, dateFormat))
                })
                signatureAdapter.notifyDataSetChanged()
            }
        })
    }
}