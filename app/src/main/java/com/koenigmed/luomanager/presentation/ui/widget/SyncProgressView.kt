package com.koenigmed.luomanager.presentation.ui.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.showToast
import kotlinx.android.synthetic.main.content_sync_progress.view.*

class SyncProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.content_sync_progress, this)
    }

    fun showProgress(progress: Int) {
        syncDeviceImageView.background = context.getDrawable(R.drawable.ic_device)
        syncDeviceImageView.setOnClickListener {  }
        syncDeviceRetry.visibility = View.GONE
        syncDeviceProgressTextView.visibility = View.VISIBLE
        syncDeviceCheck.visibility = View.GONE

        syncDeviceProgressTextView.text = progress.toString() + "%"
        syncProgressCircleView.progress = progress.toFloat()
    }

    fun showFinished() {
        syncDeviceImageView.background = context.getDrawable(R.drawable.ic_device)
        syncDeviceImageView.setOnClickListener {  }
        syncDeviceRetry.visibility = View.GONE
        syncDeviceProgressTextView.visibility = View.GONE
        syncDeviceCheck.visibility = View.VISIBLE
    }

    fun showFailure(message: String, retryCallback: () -> Unit) {
        syncDeviceImageView.background = context.getDrawable(R.drawable.ic_device_sync_failure)
        syncDeviceRetry.visibility = View.VISIBLE
        syncDeviceImageView.setOnClickListener {
            retryCallback.invoke()
        }
        syncDeviceProgressTextView.visibility = View.GONE
        syncDeviceCheck.visibility = View.GONE

        syncProgressCircleView.progress = 0f
        context.showToast(message)
    }

}