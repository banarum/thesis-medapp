package com.koenigmed.luomanager.presentation.ui.profile

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.koenigmed.luomanager.extension.getGraphDay
import java.util.*

class GraphXAxisValueFormatter() : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        //Timber.d("Value : " + value)
        return Date(value.toLong()).getGraphDay()
        //return DateUti l.getGraphDay(DateTime(value.toLong()))
    }

}