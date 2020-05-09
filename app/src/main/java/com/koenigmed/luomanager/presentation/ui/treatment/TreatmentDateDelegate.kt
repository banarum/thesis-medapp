package com.koenigmed.luomanager.presentation.ui.treatment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getHistoryDate
import com.koenigmed.luomanager.extension.inflate
import kotlinx.android.synthetic.main.item_treatment_history_date.view.*
import org.threeten.bp.LocalDate

class TreatmentDateDelegate(private val context: Context) : AdapterDelegate<MutableList<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            TreatmentDateViewHolder(parent.inflate(R.layout.item_treatment_history_date))

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
            items[position] is LocalDate

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as TreatmentDateViewHolder).bind(items[position] as LocalDate)
    }

    private inner class TreatmentDateViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(date: LocalDate) {
            with(itemView) {
                treatmentDateTextView.text = date.getHistoryDate(context)
            }
        }
    }

}