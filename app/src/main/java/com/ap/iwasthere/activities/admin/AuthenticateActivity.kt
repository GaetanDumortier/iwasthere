package com.ap.iwasthere.activities.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity

import com.ap.iwasthere.R
import com.ap.iwasthere.activities.student.StudentSelectActivity
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.utils.UIUtils

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_authenticate.*
import kotlinx.android.synthetic.main.student_select.*
import kotlinx.android.synthetic.main.student_select.navView

class AuthenticateActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    // When retrieving password from Firebase fails, this will be used as fallback password
    private var adminPassword = "toor"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        getAdminPassword()
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.menu_admin))

        //
        // View listeners
        //
        /**
         * btnAuthenticate: setOnClickListener.
         * Verify if the entered password matches the admin password.
         */
        btnAuthenticate.setOnClickListener {
            if (authenticate()) {
                val intent = Intent(this, StudentSelectActivity::class.java)
                startActivity(intent)
                this.finish()
            } else {
                SnackbarHelper(layoutAdminAuthenticate).makeAndShow(
                    getString(R.string.admin_incorrect_password),
                    Snackbar.LENGTH_LONG
                )
            }
        }

        // Handle navigation clicks
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_start -> {
                    val intent = Intent(this, StudentSelectActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
            }
            true
        }
    }

    /**
     * Attempt to retrieve the admin password from the database.
     * If password retrieval fails, the default defined password will be used.
     */
    private fun getAdminPassword() {
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("admin_password")) {
                        adminPassword = dataSnapshot.child("admin_password").value.toString()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                SnackbarHelper(layoutStudentSelect).makeAndShow(
                    getString(R.string.database_error),
                    Snackbar.LENGTH_LONG
                )
            }
        }
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(eventListener)
    }

    /**
     * Attempt to authenticate with the provided password.
     *
     * @return true on correct password, false on failure
     */
    private fun authenticate(): Boolean {
        return (txtPassword.text.toString().isNotEmpty() && txtPassword.text.toString() == adminPassword)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
