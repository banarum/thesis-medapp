package com.koenigmed.luomanager.presentation.ui.treatment

import android.content.Context
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.koenigmed.luomanager.presentation.mvp.treatment.MyoProgramHistoryPresentation
import org.threeten.bp.LocalDate

class TreatmentHistoryAdapter(context: Context) : ListDelegationAdapter<MutableList<Any>>() {

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(TreatmentDateDelegate(context))
        delegatesManager.addDelegate(TreatmentTaskDelegate(context))
    }

    fun setData(dateTreatmentMap: Map<LocalDate, List<MyoProgramHistoryPresentation>>) {
        items.clear()
        for (date in dateTreatmentMap.keys) {
            val dayHistory = dateTreatmentMap[date]!!
            if (dayHistory.isNotEmpty()) {
                items.add(date)
                for (task in dayHistory) {
                    items.add(task)
                }
            }
        }
        notifyDataSetChanged()
    }

}