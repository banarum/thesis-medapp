package com.koenigmed.luomanager.presentation.mvp.profile

data class MyoDataPresentation(val rightMyo: List<MyoDataPresentationItem>,
                          val leftMyo: List<MyoDataPresentationItem>) {
    fun isEmpty(): Boolean = rightMyo.isEmpty() || leftMyo.isEmpty()
}