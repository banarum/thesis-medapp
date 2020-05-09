package com.koenigmed.luomanager.presentation.flow

import android.content.Context
import android.content.Intent
import com.koenigmed.luomanager.presentation.ui.main.MainActivity
import com.koenigmed.luomanager.util.SequenceUtil

object Screens {
    const val MAIN_FLOW = "main_flow"

    const val MAIN_SCREEN = "main_screen"
    const val PROFILE_EDIT_SCREEN = "profile_edit_screen"
    const val DEVICE_SEARCH_SCREEN = "device_search_screen"
    const val PROGRAM_SCREEN = "program_screen"
    const val PROGRAM_CREATE_RECEIPT_SCREEN = "program_create_receipt_screen"
    const val PROGRAM_DOWNLOAD_RECEIPT_SCREEN = "program_download_receipt_screen"
    const val FAQ = "FAQ"
    const val SYNC_SCREEN = "syncTime"

    val RESULT_CODE_PROFILE_EDIT = SequenceUtil.nextInt
    val RESULT_CODE_DEVICE_PROGRAM_SET = SequenceUtil.nextInt
    val RESULT_CODE_PROGRAM_ADDED = SequenceUtil.nextInt

    fun getFlowIntent(context: Context, flowKey: String, data: Any?): Intent? = when (flowKey) {
        MAIN_FLOW -> MainActivity.getStartIntent(context)
        else -> null
    }
}