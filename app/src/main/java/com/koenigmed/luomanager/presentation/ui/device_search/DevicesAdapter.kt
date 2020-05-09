package com.koenigmed.luomanager.presentation.ui.device_search

import android.bluetooth.BluetoothDevice
import android.support.v7.util.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.koenigmed.luomanager.presentation.ui.global.DiffCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DevicesAdapter(
        clickListener: (BluetoothDevice) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private val disposables = CompositeDisposable()
    private var deviceDelegate: DeviceDelegate

    init {
        items = mutableListOf()
        deviceDelegate = DeviceDelegate(clickListener)
        delegatesManager.addDelegate(deviceDelegate)
    }

    fun setData(devices: List<BluetoothDevice>) {
        Timber.d("setData " + devices)
        val oldData = items.toMutableList()
        items.clear()
        items.addAll(devices)
        disposables.add(Observable.just("")
                .map { _ ->
                    DiffUtil.calculateDiff(DiffCallback(
                            items.map { it as BluetoothDevice },
                            oldData.map { it as BluetoothDevice }), false)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { diffResult -> diffResult.dispatchUpdatesTo(this) },
                        { Timber.e(it) }
                ))
    }
}