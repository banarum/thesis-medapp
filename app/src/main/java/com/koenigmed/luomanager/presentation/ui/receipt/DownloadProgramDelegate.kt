package com.koenigmed.luomanager.presentation.ui.receipt

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getIcon
import com.koenigmed.luomanager.extension.inflate
import com.koenigmed.luomanager.presentation.mvp.receipt.DownloadStatus
import com.koenigmed.luomanager.presentation.mvp.receipt.MyoProgramDownloadPresentation
import kotlinx.android.synthetic.main.item_program_download.view.*

class DownloadProgramDelegate(
        private val downloadListener: (String) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            DownloadProgramViewHolder(parent.inflate(R.layout.item_program_download))

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
            items[position] is MyoProgramDownloadPresentation

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as DownloadProgramViewHolder).bind(items[position] as MyoProgramDownloadPresentation)
    }

    private inner class DownloadProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(program: MyoProgramDownloadPresentation) {
            with(itemView) {
                title.text = program.name
                var downloadStatus = program.downloadStatus
                image.setImageResource(downloadStatus.getIcon())

                if (downloadStatus == DownloadStatus.NOT_DOWNLOADED) {
                    image.setOnClickListener {
                        image.setOnClickListener(null)
                        downloadListener.invoke(program.id)
                        downloadStatus = DownloadStatus.IN_PROGRESS
                        image.setImageResource(downloadStatus.getIcon())
                    }
                } else {
                    image.setOnClickListener(null)
                }
            }
        }
    }
}