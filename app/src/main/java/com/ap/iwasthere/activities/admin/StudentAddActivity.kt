package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_add.*

class StudentAddActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_add)
        NetworkObserver(applicationContext).observe(layoutAdminStudentAdd, this)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.item_student_add))

        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region View listeners
        btnStudentAdd.setOnClickListener {
            checkSplitter()
        }
        //endregion
    }

    private fun addStudent(name: String, number: String) {
        val nameArray = name.split("\\s".toRegex())
        val lastName = nameArray.drop(1).toString().replace("[", "").replace("]", "").replace(",", "")
        val student = Student().makeStudent(nameArray[0], lastName, number)

        FirebaseHelper().addStudent(student, object : FirebaseCallback.ItemCallback {
            override fun onItemCallback(value: Any) {
                if ((value as Student).id != null) {
                    txtStudentName.text.clear()
                } else {
                    txtStudentName.text.clear()
                }
            }
        })
    }

    private fun checkSplitter() {
        val text = txtStudentName.text
        if (text.contains(",")) {
            for (line in text.split(",")) {
                line.trim()
                val value = line.split(";")
                addStudent(value[1], value[0])
            }
        } else {
            text.lines().forEach { s ->
                s.trim()
                if (s.isNotEmpty()) {
                    val value = s.split(";")
                    addStudent(value[1], value[0])
                }
            }
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