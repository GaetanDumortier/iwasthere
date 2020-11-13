package com.ap.iwasthere.activities.student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.Student
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.student_select.*

/**
 * Activity class which is responsible for handling student selection.
 * This activity will provide the user with an autocomplete list
 * where they can provide their name.
 *
 * @author Gaetan Dumortier
 * @since 12 November 2020
 */
class StudentSelectActivity : AppCompatActivity() {
    // TODO: fetch from Firebase. (Map of Student objects?, need toString method)
    private val students = arrayOf("Gaetan Dumortier", "Gaetan Jean Veronique Dumortier", "Nathan Ebel")
    private lateinit var student: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_select)
        NetworkObserver(applicationContext).observe(layoutStudentSelect, this)
        supportActionBar?.title = getString(R.string.title_student_select)

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, students)
        txtStudentList.setAdapter(arrayAdapter)

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
                        1,
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
            if (!isValidStudent()) {
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
     * Verify that the provided student is inside the students list
     */
    private fun isValidStudent(): Boolean {
        return this.students.contains(txtStudentList.text.toString())
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