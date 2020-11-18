package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.student_select.*
import kotlin.collections.ArrayList


/**
 * Activity class which is responsible for handling student selection.
 * This activity will provide the user with an autocomplete list
 * where they can provide their name.
 *
 * @author Gaetan Dumortier
 * @since 12 November 2020
 */
class StudentSelectActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var arrayAdapter: ArrayAdapter<Student>
    private lateinit var student: Student
    private var students: ArrayList<Student> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_select)

        val isReload = intent.getBooleanExtra("reload", false)
        if (isReload) {
            this.recreate()
        }

        //region Observers
        NetworkObserver(applicationContext).observe(layoutStudentSelect, this)
        //endregion

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.title_student_select))

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students)
        txtStudentList.setAdapter(arrayAdapter)

        UIUtils().navigationActionsListener(this, navView)
        //endregion

        //region Database
        getAllStudents()
        //endregion

        //region View Listeners
        /**
         * StudentList: OnFocusChangeListener.
         * Will check if the provided student is valid and show a message if not
         */
        txtStudentList.setOnFocusChangeListener { view, _ ->
            if (txtStudentList.text.isNotEmpty() && !isValidStudent()) {
                txtStudentList.text.clear()

                SnackbarHelper(view).makeAndShow(
                    getString(R.string.invalid_student),
                    BaseTransientBottomBar.LENGTH_LONG
                )
            }
        }

        /**
         * StudentList: OnItemClickListener.
         * Will verify if the selected value is a valid student.
         */
        txtStudentList.setOnItemClickListener { _, view, _, _ ->
            if (!isValidStudent()) {
                txtStudentList.text.clear()

                SnackbarHelper(view).makeAndShow(
                    getString(R.string.invalid_student),
                    BaseTransientBottomBar.LENGTH_LONG
                )
            } else {
                txtStudentList.inputType = InputType.TYPE_NULL
                // Set student object
                this.student = getSelectedStudent()
                val lastName = Student().formatLastName(this.student.lastName!!)
                this.student.lastName = lastName
            }
            UIUtils().hideKeyboard(this, true)
        }

        /**
         * StudentList: OnClickListener.
         * Will clear the view if it was set previously.
         */
        txtStudentList.setOnClickListener {
            if (txtStudentList.text.isNotEmpty()) {
                txtStudentList.text.clear()
            }
            UIUtils().hideKeyboard(this, false)
        }

        /**
         * StudentSelect: OnClickListener.
         * Will verify if the selected value is a valid student.
         */
        btnStudentSelect.setOnClickListener { view ->
            if (!isValidStudent() || txtStudentList.text.isEmpty()) {
                txtStudentList.text.clear()
                UIUtils().hideKeyboard(this, true)

                SnackbarHelper(view).makeAndShow(
                    getString(R.string.invalid_student),
                    BaseTransientBottomBar.LENGTH_LONG
                )
            } else {
                // Start intent
                val intent = Intent(this, StudentSignatureActivity::class.java)
                intent.putExtra("student", this.student)
                startActivity(intent)
            }
        }
        //endregion
    }

    /**
     * Get all current students from the database and add to ArrayList.
     */
    private fun getAllStudents() {
        FirebaseHelper().fetchAllStudents(object : FirebaseCallback.ListCallback {
            override fun onListCallback(value: List<Any>) {
                students.clear()
                for (s in value) {
                    students.add(s as Student)
                }
                arrayAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun isValidStudent(): Boolean {
        return this.students.toString().contains(txtStudentList.text.toString())
    }

    private fun getSelectedStudent(): Student {
        var student: Student? = null
        for (s in this.students) {
            if (s.fullName.equals(txtStudentList.text.toString())) {
                student = s
            }
        }

        return student!!
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