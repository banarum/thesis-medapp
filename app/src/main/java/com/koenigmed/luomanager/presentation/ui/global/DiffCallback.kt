package com.koenigmed.luomanager.presentation.ui.global

import android.support.v7.util.DiffUtil

class DiffCallback(
        private val newData: List<Any>,
        private val oldData: List<Any>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldData.size
    override fun getNewListSize() = newData.size

    //without optimization at the moment
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldData[oldItemPosition] === newData[newItemPosition]
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldData[oldItemPosition] == newData[newItemPosition]
}