package com.koenigmed.luomanager.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager


fun hideKeyboard(activity: Activity?, clearCurrentFocus: Boolean = true) {
    if (activity != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val window = activity.window
        if (window != null) {
            var currentFocus = window.currentFocus
            if (currentFocus == null) {
                currentFocus = window.decorView.findFocus()
            }
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                if (clearCurrentFocus) {
                    currentFocus.clearFocus()
                }
            }
        }
    }
}