package com.koenigmed.luomanager.presentation.mvp.base

import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<V : BaseView> : MvpPresenter<V>() {

    protected val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.dispose()
    }

    protected fun Disposable.connect() {
        disposables.add(this)
    }
}