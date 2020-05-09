package com.koenigmed.luomanager.presentation.ui.widget

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class BottomSpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State?) {

        outRect.bottom = space
    }
}