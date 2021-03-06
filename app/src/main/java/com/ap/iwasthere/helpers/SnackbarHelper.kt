package com.ap.iwasthere.helpers

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * A small Snackbar helper class to make my life just a tiny bit easier.
 *
 * @author Gaetan Dumortier
 * @since 12 November 2020
 */
class SnackbarHelper(private val view: View) {
    fun makeAndShow(text: CharSequence, duration: Int = BaseTransientBottomBar.LENGTH_INDEFINITE) {
        val snackbar = Snackbar.make(
            view,
            text,
            duration
        )

        val layout = snackbar.view as Snackbar.SnackbarLayout
        layout.minimumWidth = view.width
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

        snackbar.show()
    }

    fun make(text: String, duration: Int = BaseTransientBottomBar.LENGTH_INDEFINITE): Snackbar {
        return Snackbar.make(view, text, duration)
    }
}