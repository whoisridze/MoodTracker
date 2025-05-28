package com.whoisridze.moodtracker.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.whoisridze.moodtracker.R

class StreakFireView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var currentStreak = 0
    private var maxStreak = 7
    private var fillLevel = 0f

    private val grayPaint = Paint().apply {
        val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    private val outlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        color = ContextCompat.getColor(context, R.color.blurryBackground)
        isAntiAlias = true
    }

    private val coloredDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_fire)!!.mutate()
    }

    private val grayDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_fire)!!.mutate().apply {
            colorFilter = grayPaint.colorFilter
        }
    }

    private val outlineDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_fire)!!.mutate().apply {
            setTint(ContextCompat.getColor(context, R.color.blurryBackground))
        }
    }

    init {
        setImageDrawable(null)
    }

    fun setStreak(streak: Int) {
        currentStreak = streak.coerceIn(0, maxStreak)
        fillLevel = currentStreak / maxStreak.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return

        val bounds = getBoundsForDrawables()
        grayDrawable.bounds = bounds
        coloredDrawable.bounds = bounds
        outlineDrawable.bounds = bounds

        canvas.save()
        outlinePaint.alpha = 120
        canvas.scale(1.05f, 1.05f, width / 2f, height / 2f)
        outlineDrawable.draw(canvas)
        canvas.restore()

        grayDrawable.draw(canvas)

        if (currentStreak <= 0) return

        val fillHeight = (height * fillLevel).toInt()
        canvas.save()
        canvas.clipRect(0, height - fillHeight, width, height)
        coloredDrawable.draw(canvas)
        canvas.restore()
    }

    private fun getBoundsForDrawables(): android.graphics.Rect {
        val drawableWidth = coloredDrawable.intrinsicWidth
        val drawableHeight = coloredDrawable.intrinsicHeight

        val scale = minOf(
            width.toFloat() / drawableWidth,
            height.toFloat() / drawableHeight
        )

        val scaledWidth = (drawableWidth * scale).toInt()
        val scaledHeight = (drawableHeight * scale).toInt()

        val left = (width - scaledWidth) / 2
        val top = (height - scaledHeight) / 2

        return android.graphics.Rect(
            left,
            top,
            left + scaledWidth,
            top + scaledHeight
        )
    }
}