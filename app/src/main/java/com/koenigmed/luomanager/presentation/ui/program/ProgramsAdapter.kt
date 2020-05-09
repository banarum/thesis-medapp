package com.koenigmed.luomanager.presentation.ui.program

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.presentation.ui.global.MyoProgramsDiffCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProgramsAdapter(
        switchListener: (String) -> Unit,
        deleteListener: (String) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private val disposables = CompositeDisposable()
    private var programDelegate: ProgramDelegate

    init {
        items = mutableListOf()
        programDelegate = ProgramDelegate(switchListener, deleteListener)
        delegatesManager.addDelegate(programDelegate)
    }

    fun setData(programs: List<MyoProgramPresentation>) {
        Timber.d("setData " + programs)
        val oldData = items.toMutableList()
        items.clear()
        items.addAll(programs)
        disposables.add(Observable.just("")
                .map { _ ->
                    DiffUtil.calculateDiff(MyoProgramsDiffCallback(
                            items.map { it as MyoProgramPresentation },
                            oldData.map { it as MyoProgramPresentation }), false)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { diffResult -> diffResult.dispatchUpdatesTo(this) },
                        { Timber.e(it) }
                ))
    }

    fun switchItem(programId: String) {
        items.find { (it as MyoProgramPresentation).id == programId }?.let {
            (it as MyoProgramPresentation).isSelected = true
        }
    }

    fun deleteItem(programId: String) {
        items.find { (it as MyoProgramPresentation).id == programId }?.let {
            items.remove(it)
            notifyDataSetChanged()
        }
    }

    fun onMultiSelectEnabled(multiSelect: Boolean) {
        programDelegate.multiSelect = multiSelect
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }
}