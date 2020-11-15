package com.ap.iwasthere.activities.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
import com.ap.iwasthere.activities.student.StudentSelectActivity
import com.ap.iwasthere.utils.UIUtils
import kotlinx.android.synthetic.main.admin_dashboard.*
import kotlinx.android.synthetic.main.student_select.navView

class DashboardActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard)

        //region UI
        toggle = UIUtils().setActionBarDrawerListener(this)
        UIUtils().configureSupportActionBar(this, getString(R.string.item_admin))
        UIUtils().adminNavigationActionListener(this, navView)
        //endregion

        //region View listeners.
        btnLogout.setOnClickListener {
            val intent = Intent(this, StudentSelectActivity::class.java)
            startActivity(intent)
            finish()
        }
        //endregion
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