package com.koenigmed.luomanager.presentation.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.dpToPx
import com.koenigmed.luomanager.extension.getDimen
import com.koenigmed.luomanager.extension.getScreenWidth
import kotlinx.android.synthetic.main.item_profile_pain_level.view.*

class PainLevelView : ConstraintLayout {

    private var listener: OnItemClickListener? = null
    private var itemsCount = DEFAULT_ITEMS_COUNT

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet? = null) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs,
                    R.styleable.PainLevelView, 0, 0)
            try {
                itemsCount = a.getInteger(R.styleable.PainLevelView_itemsCount, DEFAULT_ITEMS_COUNT)
            } finally {
                a.recycle()
            }
        }
        val set = ConstraintSet()
        var id = 0
        for (i in 1..10) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_profile_pain_level, null)
            view.id = i
            addView(view, 0)
            set.clone(this)
            set.addToHorizontalChain(view.id, id, 0)
            set.setHorizontalChainStyle(view.id, LayoutParams.CHAIN_SPREAD_INSIDE)
            set.applyTo(this)
            val width = context.getScreenWidth() - 2 * context.getDimen(R.dimen.profile_feels_title_padding_start) - 2 * context.getDimen(R.dimen.profile_padding_side)
            val size = (width - (itemsCount - 1) * context.dpToPx(7f)) / itemsCount
            view.apply {
                layoutParams.width = size
                layoutParams.height = size
                itemProfilePainLevelImageView.layoutParams.width = size
                itemProfilePainLevelImageView.layoutParams.height = size
                itemProfilePainLevelDigit.text = i.toString()
                setOnClickListener {
                    listener?.onItemClick(view.id)
                    for (childPosition in 0 until childCount) {
                        setSelected(getChildAt(childPosition), false)
                    }
                    setSelected(view, true)
                }
            }
            id = view.id
        }
    }

    private fun setSelected(view: View, selected: Boolean) {
        if (selected) {
            view.itemProfilePainLevelImageView.background = context.getDrawable(R.drawable.ic_profile_pain_level_bg_selected)
            view.itemProfilePainLevelDigit.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, context!!.theme))
        } else {
            view.itemProfilePainLevelImageView.background = context.getDrawable(R.drawable.ic_profile_pain_level_bg)
            view.itemProfilePainLevelDigit.setTextColor(ResourcesCompat.getColor(resources, R.color.profile_pain_level_text_color, context!!.theme))
        }
    }

    fun setOnPainLevelClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    companion object {
        const val DEFAULT_ITEMS_COUNT = 10
    }
}