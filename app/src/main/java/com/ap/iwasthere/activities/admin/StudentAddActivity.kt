package com.ap.iwasthere.activities.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ap.iwasthere.R
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

        //region Database
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