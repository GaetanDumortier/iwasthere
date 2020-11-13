package com.ap.iwasthere.activities.student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.Student
import com.ap.iwasthere.utils.NetworkObserver
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
    private lateinit var student: Student
    private var students: ArrayList<Student> = ArrayList()
    private var arrayAdapter: ArrayAdapter<Student>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_select)
        NetworkObserver(applicationContext).observe(layoutStudentSelect, this)
        supportActionBar?.title = getString(R.string.title_student_select)

        //
        // UI
        //
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students)
        txtStudentList.setAdapter(arrayAdapter)

        //
        // Database
        //
        populateStudentsList()

        //
        // View Listeners
        //

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
            hideKeyboard(true)
        }

        /**
         * StudentList: OnClickListener.
         * Will clear the view if it was set previously.
         */
        txtStudentList.setOnClickListener {
            if (txtStudentList.text.isNotEmpty()) {
                txtStudentList.text.clear()
            }
            hideKeyboard(false)
        }

        /**
         * StudentSelect: OnClickListener.
         * Will verify if the selected value is a valid student.
         */
        btnStudentSelect.setOnClickListener { view ->
            if (!isValidStudent() || txtStudentList.text.isEmpty()) {
                txtStudentList.text.clear()
                hideKeyboard(true)

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
    }

    /**
     * Fetch all current students from the database and add to ArrayList.
     */
    private fun populateStudentsList() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("students")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val student = ds.getValue(Student::class.java)
                        student?.setFullName()
                        students.add(student!!)
                    }
                    arrayAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                SnackbarHelper(layoutStudentSelect).makeAndShow(
                    getString(R.string.database_error),
                    Snackbar.LENGTH_LONG
                )
            }
        }
        usersRef.addListenerForSingleValueEvent(eventListener)
    }

    /**
     * Verify that the provided student is inside the students list
     */
    private fun isValidStudent(): Boolean {
        return this.students.toString().contains(txtStudentList.text.toString())
    }

    private fun hideKeyboard(hide: Boolean) {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (hide) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                imm.showSoftInput(view, 0)
            }
        }
    }
}