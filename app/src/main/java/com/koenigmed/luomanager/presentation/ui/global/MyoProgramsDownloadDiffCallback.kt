package com.koenigmed.luomanager.presentation.ui.global

import android.support.v7.util.DiffUtil
import com.koenigmed.luomanager.presentation.mvp.receipt.MyoProgramDownloadPresentation

class MyoProgramsDownloadDiffCallback(
        private val newData: List<MyoProgramDownloadPresentation>,
        private val oldData: List<MyoProgramDownloadPresentation>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldData.size
    override fun getNewListSize() = newData.size

    //without optimization at the moment
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = true

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldData[oldItemPosition] == newData[newItemPosition]
}