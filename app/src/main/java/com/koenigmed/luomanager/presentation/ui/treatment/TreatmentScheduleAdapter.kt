package com.koenigmed.luomanager.presentation.ui.treatment

import android.support.v7.util.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.koenigmed.luomanager.presentation.ui.global.DiffCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalTime
import timber.log.Timber

class TreatmentScheduleAdapter(
) : ListDelegationAdapter<MutableList<Any>>() {

    private val disposables = CompositeDisposable()
    private var scheduleDelegate: TreatmentScheduleDelegate

    init {
        items = mutableListOf()
        scheduleDelegate = TreatmentScheduleDelegate()
        delegatesManager.addDelegate(scheduleDelegate)
    }

    fun setData(schedule: List<LocalTime>) {
        Timber.d("setData " + schedule)
        val oldData = items.toMutableList()
        items.clear()
        items.addAll(schedule)
        disposables.add(Observable.just("")
                .map { _ ->
                    DiffUtil.calculateDiff(DiffCallback(
                            items.map { it as LocalTime },
                            oldData.map { it as LocalTime }), false)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { diffResult -> diffResult.dispatchUpdatesTo(this) },
                        { Timber.e(it) }
                ))
    }

}