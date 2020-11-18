package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallBack
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_overview.*

class StudentOverviewActivity : AppCompatActivity() {
    private var TAG = "StudentOverview"

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var arrayAdapter: ArrayAdapter<Student>
    private var students: ArrayList<Student> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_overview)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.menu_students_title))

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students)
        studentOverviewList.adapter = arrayAdapter

        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region Database
        getAllStudents()
        //endregion

        //region View listeners
        studentOverviewList.setOnItemClickListener { parent, view, position, id ->
            val student: Student = parent.getItemAtPosition(position) as Student
            Log.d(TAG, "Student selected: ${student.id}")
            // TODO: Navigate to new view: StudentDetailsActivity for clicked ID
        }
        //endregion
    }

    private fun getAllStudents() {
        FirebaseHelper().fetchAllStudents(object : FirebaseCallBack {
            override fun onStudentCallBack(value: List<Student>) {
                students.clear()
                for (s in value) {
                    students.add(s)
                }
                arrayAdapter.notifyDataSetChanged()
            }
        })
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