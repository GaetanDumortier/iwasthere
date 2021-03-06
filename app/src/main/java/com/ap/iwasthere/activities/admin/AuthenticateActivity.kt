package com.ap.iwasthere.activities.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.BuildConfig
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.admin_authenticate.*
import kotlinx.android.synthetic.main.student_select.navView

class AuthenticateActivity : AppCompatActivity() {
    // When retrieving password from Firebase fails, the password from gradle.properties will be used
    private var adminPassword = BuildConfig.ADMIN_PASSWORD

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_authenticate)
        NetworkObserver(applicationContext).observe(layoutAdminAuthenticate, this)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.item_admin))
        UIUtils().navigationActionsListener(this, navView)
        //endregion

        //region Database
        getAdminPassword()
        //endregion

        //region View Listeners
        /**
         * btnAuthenticate: setOnClickListener.
         * Verify if the entered password matches the admin password.
         */
        btnAuthenticate.setOnClickListener {
            if (authenticate()) {
                // val intent = Intent(this, DashboardActivity::class.java)
                val intent = Intent(this, StudentOverviewActivity::class.java)
                startActivity(intent)
                this.finish()
            } else {
                SnackbarHelper(layoutAdminAuthenticate).makeAndShow(
                    getString(R.string.admin_incorrect_password),
                    Snackbar.LENGTH_LONG
                )
                UIUtils().hideKeyboard(this, true)
            }
        }

        txtPassword.setOnClickListener { txtPassword.text.clear() }
        //endregion
    }

    /**
     * Attempt to retrieve the admin password from the database.
     * If password retrieval fails, the default defined password will be used.
     */
    private fun getAdminPassword() {
        FirebaseHelper().getAdminPassword(object : FirebaseCallback.ItemCallback {
            override fun onItemCallback(value: Any) {
                if (value.toString().isNotEmpty()) {
                    this@AuthenticateActivity.adminPassword = value.toString()
                }
            }
        })
    }

    /**
     * Attempt to authenticate with the provided password.
     *
     * @return true on correct password, false on failure
     */
    private fun authenticate(): Boolean {
        return (txtPassword.text.toString().isNotEmpty() && txtPassword.text.toString() == adminPassword)
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
