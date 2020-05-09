package com.koenigmed.luomanager.presentation.ui.receipt

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.koenigmed.luomanager.presentation.mvp.receipt.MyoProgramDownloadPresentation
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class DownloadProgramsAdapter(
        downloadListener: (String) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private val disposables = CompositeDisposable()

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(DownloadProgramDelegate(downloadListener))
    }

    fun setData(programs: List<MyoProgramDownloadPresentation>) {
        Timber.d("setData " + programs)
        val oldData = items.toMutableList()
        items.clear()
        items.addAll(programs)
        notifyDataSetChanged()
        /*disposables.add(Observable.just("")
                .map { _ ->
                    DiffUtil.calculateDiff(MyoProgramsDownloadDiffCallback(
                            items.map { it as MyoProgramDownloadPresentation },
                            oldData.map { it as MyoProgramDownloadPresentation }), false)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { diffResult -> diffResult.dispatchUpdatesTo(this) },
                        { Timber.e(it) }
                ))*/
    }
}