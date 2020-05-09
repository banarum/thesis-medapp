package com.koenigmed.luomanager.extension

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.Toast


fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.dpToPx(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun Context.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.getDimen(dimenId: Int): Int {
    return resources.getDimensionPixelSize(dimenId)
}

fun Context.getDimension(dimenId: Int): Float {
    val out = TypedValue()
    resources.getValue(dimenId, out, true)
    return out.float
}

fun Context.getDrawable(drawableId: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, drawableId, theme)!!
}
