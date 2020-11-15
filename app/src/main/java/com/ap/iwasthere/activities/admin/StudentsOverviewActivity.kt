package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallBack
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.students_overview.*

class StudentsOverviewActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private var students: ArrayList<Student> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.students_overview)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.menu_students_title))
        UIUtils().adminNavigationActionListener(this, navView)
        //endregion
    }

    private fun getAllStudents() {
        FirebaseHelper().fetchAllStudents(object : FirebaseCallBack {
            override fun onStudentCallBack(value: List<Student>) {
                students.clear()
                for (s in value) {
                    students.add(s)
                }
                // arrayAdapter.notifyDataSetChanged()
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