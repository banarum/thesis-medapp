package com.koenigmed.luomanager.presentation.mvp.profile

import com.koenigmed.luomanager.presentation.ui.profile.MyoType
import org.threeten.bp.LocalDateTime

data class MyoDataPresentationItem(
        val type: MyoType,
        val date: LocalDateTime,
        val value: Double
)