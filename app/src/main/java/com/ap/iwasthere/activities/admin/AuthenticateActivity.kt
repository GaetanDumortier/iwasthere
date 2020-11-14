package com.ap.iwasthere.activities.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.ap.iwasthere.R
import com.ap.iwasthere.activities.student.StudentSelectActivity
import kotlinx.android.synthetic.main.student_select.*

class AuthenticateActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        //
        // UI
        //
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.title = getString(R.string.menu_admin)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
