package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_overview.*
import kotlin.collections.ArrayList


class StudentOverviewActivity : AppCompatActivity() {
    private var TAG = "StudentOverview"

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var arrayAdapter: ArrayAdapter<Student>
    private var students: ArrayList<Student> = ArrayList()
    private var filteredStudents: ArrayList<Student> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_overview)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.menu_students_title))

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredStudents)
        studentOverviewList.adapter = arrayAdapter

        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region Database
        getAllStudents()
        //endregion

        txtSearch.addTextChangedListener {
            filterList(txtSearch.text.toString())
        }

        //region View listeners
        studentOverviewList.setOnItemClickListener { parent, view, position, id ->
            val student: Student = parent.getItemAtPosition(position) as Student
            Log.d(TAG, "Student selected: ${student.id}")
            // TODO: Navigate to new view: StudentDetailsActivity for clicked ID
        }
        //endregion
    }

    private fun getAllStudents() {
        FirebaseHelper().fetchAllStudents(object : FirebaseCallback.ListCallback {
            override fun onListCallback(value: List<Any>) {
                students.clear()
                filteredStudents.clear()
                for (s in value) {
                    students.add(s as Student)
                }
                filteredStudents.addAll(students)
                arrayAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun filterList(input: String) {
        val filtered = ArrayList<Student>()
        if (input.isEmpty()) {
            filtered.addAll(students)
        } else {
            for (s in students) {
                if (s.fullName!!.toLowerCase().contains(input.toLowerCase())) {
                    filtered.add(s)
                }
            }
        }

        filteredStudents.clear()
        filteredStudents.addAll(filtered)
        arrayAdapter.notifyDataSetChanged()
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