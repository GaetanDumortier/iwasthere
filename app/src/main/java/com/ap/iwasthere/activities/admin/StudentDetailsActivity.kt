package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.models.adapters.SignatureAdapter
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_details.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StudentDetailsActivity : AppCompatActivity() {
    private val SIGNATURES_AMOUNT = 5 // How many signatures to fetch
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var signatureAdapter: SignatureAdapter
    private lateinit var student: Student
    private var signatures: ArrayList<Signature> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_details)
        NetworkObserver(applicationContext).observe(layoutAdminStudentDetail, this)

        student = intent.getParcelableExtra("student")!!
        updateView(student)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, student.fullName!!)
        UIUtils().adminNavigationActionListener(this, navView)

        signatureAdapter = SignatureAdapter(this, R.layout.signature_row, signatures)
        signatureListView.adapter = signatureAdapter
        //endregion

        //region Database
        getSignatures()
        //endregion
    }

    private fun updateView(student: Student) {
        lblStudentName.text = student.fullName!!
        lblStudentNumber.text = "12346578"
    }

    private fun getSignatures() {
        FirebaseHelper().fetchAllSignaturesFromUser(student.id!!, object : FirebaseCallback.ListCallback {
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
                Log.d("StudentDetail", signatures.toString())
            }
        }, SIGNATURES_AMOUNT)
    }

    //region Override functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion
}