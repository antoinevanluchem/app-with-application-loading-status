package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //
    // Paint
    //
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
                buttonText = context.resources.getString(R.string.button_loading)
                startLoadingAnimation()
            }

            ButtonState.Completed -> {
                isClickable = true
                buttonText = context.resources.getString(R.string.button_name)
            }
        }
    }

    private var valueAnimator: ValueAnimator? = null

    // The loading animation only loads until 75%. The last 25% will only be animated, once
    // `finishLoadingAnimation` is called. A better way would be to inject the
    // `currentLoadingPercentage` in the LoadingButton, but that is out of scope for this project.
    private fun startLoadingAnimation() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(0.0f, 0.75f).apply {
            duration = 10000

            addUpdateListener { animation ->
                currentLoadingPercentage = animation.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    // Finish the animation quickly
    fun finishLoadingAnimation() {
        val finishAnimator = ValueAnimator.ofFloat(currentLoadingPercentage, 1.0f).apply {
            duration = 500

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
        valueAnimator?.cancel()
        valueAnimator = finishAnimator
    }

    fun cancelLoadingAnimation() {
        valueAnimator?.cancel()
        currentLoadingPercentage = 0.0f
        buttonState = ButtonState.Completed
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
            drawLoadingCircle(it)
        }
    }

    private fun drawRectangle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawLoadingRectangle(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, widthSize.toFloat() * currentLoadingPercentage, heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.white)

        canvas.drawText(buttonText, xText, yText, paint)
    }

    private fun drawLoadingCircle(canvas: Canvas) {
        canvas.save()

        canvas.translate(dxCircle, dyCircle)
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(RectF(0f, 0f, paint.textSize, paint.textSize), 0F, currentLoadingPercentage * 360, true, paint)

        canvas.restore()
    }

    //
    // Coordinates and measurements
    //
    private var widthSize = 0
        set(value) {
            field = value

            // xText and dxCircle are relative to widthSize
            xText = widthSize / 2.0f
            dxCircle = widthSize / 2.0f + paint.measureText(R.string.button_loading.toString()) / 2.0f + paint.textSize / 2.0f
        }

    private var heightSize = 0
        set(value) {
            field = value

            // yText and dyCircle are relative to heightSize
            yText = (heightSize - textHeight) / 2.0f - paint.ascent()
            dyCircle = heightSize / 2.0f - paint.textSize / 2.0f
        }

    private var textHeight = paint.descent() - paint.ascent()

    private var xText = 0.0f
    private var yText = 0.0f

    private var dxCircle = 0.0f
    private var dyCircle = 0.0f

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

    //
    // Perform Click
    //
    fun setOnClickListener(listener: () -> Unit) {
        super.setOnClickListener {
            listener.invoke()
        }
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked

        // Call performClick after setting the ButtonState, such that the animation can be cancelled
        // by an onClickListener
        super.performClick()

        invalidate()
        return true
    }

}