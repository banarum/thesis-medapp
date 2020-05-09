package com.koenigmed.luomanager.presentation.ui.global

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : MvpAppCompatFragment() {
    companion object {
        private const val PROGRESS_TAG = "bf_progress"
    }

    abstract val layoutRes: Int

    protected val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { restoreState(it) }
    }

    open protected fun restoreState(state: Bundle) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(layoutRes, container, false)

    fun showProgress(isVisible: Boolean) {
        showProgressDialog(isVisible)
    }

    fun showMessage(message: String) {
        showSnackMessage(message)
    }

    protected fun showProgressDialog(progress: Boolean) {
        if (!isAdded) return

        val fragment = childFragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && !progress) {
            (fragment as ProgressDialog).dismissAllowingStateLoss()
            childFragmentManager.executePendingTransactions()
        } else if (fragment == null && progress) {
            ProgressDialog().show(childFragmentManager, PROGRESS_TAG)
            childFragmentManager.executePendingTransactions()
        }
    }

    protected fun showSnackMessage(message: String) {
        view?.let {
            val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)
            val messageTextView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            messageTextView.setTextColor(Color.WHITE)
            snackbar.show()
        }
    }

    open fun onBackPressed() {}

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    protected fun Disposable.connect() {
        disposables.add(this)
    }

}