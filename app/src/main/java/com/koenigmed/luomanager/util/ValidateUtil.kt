package com.koenigmed.luomanager.util

import android.text.TextUtils
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.system.IResourceManager

fun isValidEmail(target: CharSequence): Boolean {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun isPasswordValid(password: String, resourceManager: IResourceManager)
        = password.trim().length < resourceManager.getInteger(R.integer.auth_password_min_length)