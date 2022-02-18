package com.ibrajix.multiclock.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.ibrajix.multiclock.R

class CircularTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr)
{

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val r = measuredWidth.coerceAtLeast(measuredHeight)
        setMeasuredDimension(r, r)
    }

}