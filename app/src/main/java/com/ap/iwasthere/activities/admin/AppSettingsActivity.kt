package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.SnackbarHelper
import com.ap.iwasthere.utils.NetworkObserver
import com.ap.iwasthere.utils.UIUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.admin_dashboard.*
import kotlinx.android.synthetic.main.admin_settings.*
import kotlinx.android.synthetic.main.admin_settings.navView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AppSettingsActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var toggle: ActionBarDrawerToggle

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_settings)
        NetworkObserver(applicationContext).observe(layoutAdminSettings, this)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.item_admin))
        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region View listeners.
        /**
         * SettingsUpdate: ClickListener.
         * Update the admin password with provided value
         */
        btnSettingsUpdate.setOnClickListener {
            launch {
                val newPassword = txtAdminPassword.text.trim().toString()
                if (newPassword.isNotEmpty()) {
                    updateAdminPassword(newPassword)

                    UIUtils().hideKeyboard(this@AppSettingsActivity, true)
                    txtAdminPassword.text.clear()
                    SnackbarHelper(it).makeAndShow("Instellingen opgeslagen!", Snackbar.LENGTH_SHORT)
                } else {
                    SnackbarHelper(it).makeAndShow("Je hebt geen (geldig) wachtwoord ingevuld!", Snackbar.LENGTH_SHORT)
                }
            }
        }
        //endregion
    }

    private suspend fun updateAdminPassword(password: String) {
        FirebaseHelper().setAdminPassword(password)
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