package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.LocationHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.models.adapters.SignatureAdapter
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.signature_overview.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SignatureOverviewActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var signatureAdapter: SignatureAdapter
    private var signatures: ArrayList<Signature> = ArrayList()
    private var students: ArrayList<Student> = ArrayList()
    private var filteredSignatures: ArrayList<Signature> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signature_overview)
        NetworkObserver(applicationContext).observe(layoutAdminStudentSignatures, this)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.item_signatures))

        signatureAdapter = SignatureAdapter(this, R.layout.signature_row, filteredSignatures)
        signatureOverviewList.adapter = signatureAdapter

        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region Database
        getAllStudents()
        //endregion

        //region View listeners
        /**
         * Search field: TextChangedListener.
         * Will filter the students list realtime with provided input.
         */
        txtSearch.addTextChangedListener { filterList(txtSearch.text.toString().toLowerCase()) }

        /**
         * InvalidLocations switch: OnClickListener.
         * Will filter the signatures list for all signatures
         * that are marked with a suspicious location.
         */
        switchShowInvalidLocations.setOnClickListener {
            if (switchShowInvalidLocations.isChecked) {
                val list =
                    filteredSignatures.filter { LocationHelper.locationIsSuspicious(it.location!!) } as ArrayList<Signature>
                filteredSignatures.clear()
                filteredSignatures.addAll(list)
            } else {
                txtSearch.text.clear()
                filteredSignatures.clear()
                filteredSignatures.addAll(signatures)
            }
            signatureAdapter.notifyDataSetChanged()
        }
        //endregion
    }

    private fun getAllStudents() {
        FirebaseHelper().fetchAllStudents(object : FirebaseCallback.ListCallback {
            override fun onListCallback(value: List<Any>) {
                students.clear()
                filteredSignatures.clear()
                for (s in value) {
                    students.add(s as Student)
                    s.signatures?.forEach {
                        signatures.add(it.value)
                    }
                }
                // TODO: cleanup
                filteredSignatures.addAll(signatures)

                val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                signatures.sortWith(Comparator { s1: Signature, s2: Signature ->
                    LocalDateTime.parse(s2.date!!, dateFormat).compareTo(LocalDateTime.parse(s1.date!!, dateFormat))
                })
                signatureAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun filterList(input: String) {
        val filtered = ArrayList<Signature>()
        if (input.isEmpty()) {
            filtered.addAll(signatures)
        } else {
            runOnUiThread {
                for (s in signatures) {
                    if (s.location!!.address!!.toLowerCase().contains(input)
                        || s.date!!.toLowerCase().contains(input)
                    ) {
                        filtered.add(s)
                    }
                }
            }
        }

        // TODO: cleanup
        runOnUiThread {
            filteredSignatures.clear()
            filteredSignatures.addAll(filtered)
            val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            signatures.sortWith(Comparator { s1: Signature, s2: Signature ->
                LocalDateTime.parse(s2.date!!, dateFormat).compareTo(LocalDateTime.parse(s1.date!!, dateFormat))
            })
            signatureAdapter.notifyDataSetChanged()
        }
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