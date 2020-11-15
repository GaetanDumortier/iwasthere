package com.ap.iwasthere.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.ap.iwasthere.R
import com.ap.iwasthere.activities.admin.AuthenticateActivity
import com.ap.iwasthere.activities.student.StudentSelectActivity
import com.google.android.material.navigation.NavigationView

/**
 * A class responsible for handling actions related to UI views and elements.
 *
 * @author Gaetan Dumortier
 * @since 14 November 2020
 */
class UIUtils {
    /**
     * Toggle the keyboard for a given activity.
     *
     * @param activity the activity to check and toggle the keyboard for
     * @param hide whether to hide the keyboard or not
     */
    fun hideKeyboard(activity: Activity, hide: Boolean) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (hide) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                imm.showSoftInput(view, 0)
            }
        }
    }

    /**
     * Get a new ActionBarDrawerToggle for the provided activity and
     * add a listener for the toggle.
     *
     * @param activity the activity to add the listener for
     * @return the ActionBarDrawerToggle to be re-used in override functions
     */
    fun setActionBarDrawerListener(activity: AppCompatActivity): ActionBarDrawerToggle {
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawerLayout)

        val toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        return toggle
    }

    /**
     * Configure the support actionbar for a provided activity
     *
     * @param activity the activity to modify the support actionbar for
     * @param title the title of the support actionbar
     */
    fun configureSupportActionBar(activity: AppCompatActivity, title: String) {
        activity.supportActionBar?.title = title
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Loop through all the children of a provided view, and disable all inputs
     * when we are not connected to the internet.
     *
     * @param view the view to check
     * @param enabled whether to enable the children or not
     */
    fun enableAllInput(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                enableAllInput(child, enabled)
            }
        }
    }

    /**
     * Define a click listener for the navigation view. Clicking an item in the navigation
     * will toggle the appropriate action defined in the when-block.
     *
     * When adding or modifying menu-items, make sure to change them here accordingly.
     *
     * @param activity the activity to add a listener for
     * @param navView the NavigationView resource
     */
    fun navigationActionsListener(activity: AppCompatActivity, navView: NavigationView) {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_admin -> {
                    //if (view!!.sourceLayoutResId != R.layout.admin_authenticate) {
                    val intent = Intent(activity, AuthenticateActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                    //}
                }
                R.id.item_start -> {
                    //if (view!!.sourceLayoutResId != R.layout.student_select) {
                    val intent = Intent(activity, StudentSelectActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                    //}
                }
            }
            true
        }
    }

    /**
     * Equal to the method above (navigationActionsListener), but handle specific items
     * defined in the admin navigation (nav_drawer_menu_admin)
     *
     * @param activity the activity to add a listener for
     * @param navView the NavigationView resource
     */
    fun adminNavigationActionListener(activity: AppCompatActivity, navView: NavigationView) {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_start -> {
                    val intent = Intent(activity, StudentSelectActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
                R.id.admin_item_students -> {
                    // TODO: implement activity to StudentsOverviewActivity
                }
                R.id.admin_item_signatures -> {
                    // TODO: implement activity to SignaturesOverviewActivity
                }
                R.id.admin_item_student_add -> {
                    // TODO: implement activity to StudentAddActivity
                }
                R.id.admin_item_app_settings -> {
                    // TODO: implement activity to AppSettingsActivity
                }
                R.id.admin_item_app_sync -> {
                    // TODO: add activity for this and add callback in FirebaseHelper to show snackbar when done?
                }
            }
            true
        }
    }
}