package com.ap.iwasthere.activities.student

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.student_select.*
import java.util.*
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
        populateStudentsListFromDb()
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
                val studentName = txtStudentList.text.toString().split("\\s".toRegex()).toTypedArray()
                this.student =
                    Student(
                        UUID.randomUUID().toString(),
                        studentName[0],
                        studentName
                            .drop(1).toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "")
                    )
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
     * Fetch all current students from the database and add to ArrayList.
     */
    private fun populateStudentsListFromDb() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("students")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        students.clear()
                        val student = ds.getValue(Student::class.java)

                        student?.setFullName()
                        students.add(student!!)
                    }
                    arrayAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                SnackbarHelper(layoutStudentSelect).makeAndShow(
                    getString(R.string.database_error),
                    Snackbar.LENGTH_LONG
                )
            }
        }
        usersRef.addValueEventListener(eventListener)
    }

    private fun isValidStudent(): Boolean {
        return this.students.toString().contains(txtStudentList.text.toString())
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