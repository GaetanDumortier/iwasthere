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

    /**
     * Check whether a student with the provided student ID already exists in the database
     *
     * @param studentId the unique identifier of the student
     */
    private fun studentExists(name: String, studentId: String) {
        FirebaseHelper().studentExists(studentId, object : FirebaseCallback.ItemCallback {
            override fun onItemCallback(value: Any) {
                if (value as Boolean) {
                    val alert = UIUtils().buildAlertDialog(
                        this@StudentAddActivity,
                        getString(R.string.dialog_student_exists_title),
                        getString(R.string.dialog_student_exists_message)
                    )
                    alert.setPositiveButton(getString(R.string.dialog_positive_ok)) { dialog, _ -> dialog.dismiss() }
                    alert.setOnCancelListener { dialog -> dialog.cancel() }
                    alert.show()
                } else {
                    addStudent(name, studentId)
                }
            }
        })
    }

    /**
     * Check which splitter is used to add students (either comma or semicolon) and format accordingly
     */
    private fun checkSplitter() {
        val text = txtStudentName.text
        if (text.contains(",")) {
            text.lines().forEach { s ->
                if (s.isNotEmpty()) {
                    val value = s.trim().split(",")
                    studentExists(value[1].trim(), value[0].trim())
                }
            }
        } else {
            text.lines().forEach { s ->
                if (s.isNotEmpty()) {
                    val value = s.trim().split(";")
                    // addStudent(value[1], value[0])
                    studentExists(value[1], value[0])
                }
            }
        }
    }

    /**
     * Add a new student to the database with provided data
     *
     * @param name the formatted full name of the student
     * @param number the unique identifier of the student
     */
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

    //region Override functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion
}