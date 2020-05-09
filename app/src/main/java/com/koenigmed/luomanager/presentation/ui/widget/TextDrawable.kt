package com.koenigmed.luomanager.presentation.ui.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt

class TextDrawable(private val text: String, @ColorInt backgroundColor: Int, textHeight: Int) : Drawable() {
    private val textPaint: Paint
    private val textBounds: Rect
    private val bgPaint: Paint
    private val bgBounds: RectF

    init {

        // Text
        this.textPaint = Paint()
        this.textBounds = Rect()
        textPaint.color = Color.WHITE
        textPaint.setARGB(255, 255, 255, 255)
        textPaint.isAntiAlias = true
        textPaint.isSubpixelText = true
        textPaint.textAlign = Paint.Align.CENTER // Important to centre horizontally in the background RectF
        textPaint.textSize = textHeight.toFloat()
        textPaint.typeface = Typeface.DEFAULT_BOLD
        // Map textPaint to a Rect in order to get its true height
        // ... a bit long-winded I know but unfortunately getTextSize does not seem to give a true height!
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        // Background
        this.bgPaint = Paint()
        bgPaint.isAntiAlias = true
        bgPaint.color = backgroundColor
        val rectHeight = (TEXT_PADDING * 2 + textHeight).toFloat()
        val rectWidth = TEXT_PADDING * 2 + textPaint.measureText(text)
        //float rectWidth   = TEXT_PADDING * 2 + textHeight;  // Square (alternative)
        // Create the background - use negative start x/y coordinates to centre align the icon
        this.bgBounds = RectF(rectWidth / -2, rectHeight / -2, rectWidth / 2, rectHeight / 2)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(bgBounds, ROUNDED_RECT_RADIUS.toFloat(), ROUNDED_RECT_RADIUS.toFloat(), bgPaint)
        // Position the text in the horizontal/vertical centre of the background RectF
        canvas.drawText(text, 0f, ((textBounds.bottom - textBounds.top) / 2).toFloat(), textPaint)
    }

    override fun setAlpha(alpha: Int) {
        bgPaint.alpha = alpha
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        bgPaint.colorFilter = cf
        textPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    companion object {

        private const val TEXT_PADDING = 3
        private const val ROUNDED_RECT_RADIUS = 5
    }
}

