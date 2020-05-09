package com.koenigmed.luomanager.presentation.ui.profile

import android.content.Context
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.LineDataSet
import com.koenigmed.luomanager.R
import java.util.*


object GraphUtil {

    /**
     * Returns colored max and min elements
     */
    fun getColors(context: Context, dataSet: LineDataSet): List<Int> {
        val colorRight = ContextCompat.getColor(context, R.color.profile_graph_right)
        val colorLeft = ContextCompat.getColor(context, R.color.profile_graph_left)
        val colors = ArrayList<Int>()

        val entries = dataSet.values

        for (entry in entries) {
            when {
                (entry.data as MyoType) == MyoType.RIGHT -> colors.add(colorRight)
                (entry.data as MyoType) == MyoType.LEFT -> colors.add(colorLeft)
            }
        }
        return colors
    }
}