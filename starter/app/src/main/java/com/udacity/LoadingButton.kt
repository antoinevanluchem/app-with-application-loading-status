package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    //
    // ButtonState
    //

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        onButtonStateChangedTo(new)
    }

    init {
        buttonState = ButtonState.Completed
    }

    private var buttonText = resources.getString(R.string.button_name)
    private var currentLoadingPercentage = 0.0f

    private fun onButtonStateChangedTo(new: ButtonState) {
        when (new) {
            ButtonState.Clicked -> {
                isClickable = false
                buttonState = ButtonState.Loading
            }

            ButtonState.Loading -> {
                isClickable = false
                buttonText = context.resources.getString(R.string.button_loading)
                startLoadingAnimation()
            }

            ButtonState.Completed -> {
                isClickable = true
                buttonText = context.resources.getString(R.string.button_name)
            }
        }
    }

    private fun startLoadingAnimation() {
        ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            duration = 5000

            addUpdateListener { animation ->
                currentLoadingPercentage = animation.animatedValue as Float
                invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentLoadingPercentage = 0.0f
                    buttonState = ButtonState.Completed
                }
            })

            start()
        }
    }

    //
    // onDraw
    //
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawRectangle(it)
            drawLoadingRectangle(it)
            drawText(it)

        }
    }

    private fun drawRectangle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.white)

        val textHeight = paint.descent() - paint.ascent()
        val x = widthSize / 2.0f
        val y = (heightSize - textHeight) / 2.0f - paint.ascent()

        canvas.drawText(buttonText, x, y, paint)
    }

    private fun drawLoadingRectangle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, widthSize.toFloat() * currentLoadingPercentage, heightSize.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        buttonState = ButtonState.Clicked


        invalidate()
        return true
    }

}