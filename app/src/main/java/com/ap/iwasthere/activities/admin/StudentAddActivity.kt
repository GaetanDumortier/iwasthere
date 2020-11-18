package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.student_add.*

class StudentAddActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_add)

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

    private fun addStudent(name: String) {
        val nameArray = name.split("\\s".toRegex())
        val lastName = nameArray.drop(1).toString().replace("[", "").replace("]", "").replace(",", "")
        val student = Student().makeStudent(nameArray[0], lastName)
        Log.d("StudentAdd", student.toString())

        FirebaseHelper().addStudent(student, object : FirebaseCallback.ItemCallback {
            override fun onItemCallback(value: Any) {
                if ((value as Student).id != null) {
                    //SnackbarHelper(view).makeAndShow("De student is successvol toegevoegd!")
                    txtStudentName.text.clear()
                } else {
                    //SnackbarHelper(view).makeAndShow("Er is iets fout gegaan tijdens het toevoegen van de student!")
                    txtStudentName.text.clear()
                }
            }
        })
    }

    private fun checkSplitter() {
        var splitter = "\\n"
        if (txtStudentName.text.contains(",")) {
            splitter = ","
        }

        for (line in txtStudentName.text.split(splitter)) {
            addStudent(line.trim())
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