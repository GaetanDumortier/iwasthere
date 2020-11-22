package com.ap.iwasthere.activities.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_overview.*

class StudentOverviewActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var arrayAdapter: ArrayAdapter<Student>
    private var students: ArrayList<Student> = ArrayList()
    private var filteredStudents: ArrayList<Student> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_overview)
        NetworkObserver(applicationContext).observe(layoutAdminStudentOverview, this)

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

        //region View listeners
        /**
         * Search field: TextChangedListener.
         * Will filter the students list realtime with provided input.
         */
        runOnUiThread {
            txtSearch.addTextChangedListener { filterList(txtSearch.text.toString()) }
            /**
             * StudentOverviewList: OnClickListener.
             * Will show a detail page for selected student.
             */
            studentOverviewList.setOnItemClickListener { parent, _, position, _ ->
                val student: Student = parent.getItemAtPosition(position) as Student
                val intent = Intent(this, StudentDetailsActivity::class.java)
                intent.putExtra("student", student)
                startActivity(intent)
            }
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
                filteredStudents.sortWith(Comparator { s1: Student, s2: Student ->
                    s1.fullName!!.compareTo(s2.fullName!!)
                })
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

        runOnUiThread {
            filteredStudents.clear()
            filteredStudents.addAll(filtered)
            filteredStudents.sortWith(Comparator { s1: Student, s2: Student ->
                s1.fullName!!.compareTo(s2.fullName!!)
            })

            arrayAdapter.notifyDataSetChanged()
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