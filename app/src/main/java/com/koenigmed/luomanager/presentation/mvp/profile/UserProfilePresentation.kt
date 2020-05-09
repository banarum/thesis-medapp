package com.koenigmed.luomanager.presentation.mvp.profile

import com.koenigmed.luomanager.presentation.mvp.profile_edit.UserDataPresentation

data class UserProfilePresentation(
        var userData: UserDataPresentation,
        var showFeels: Boolean,
        var showPainLevel: Boolean)