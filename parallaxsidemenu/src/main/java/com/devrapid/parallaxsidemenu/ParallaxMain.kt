package com.devrapid.parallaxsidemenu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.devrapid.kotlinknifer.navigationBarHeiht

/**
 * @author  jieyi
 * @since   2/8/18
 */
class ParallaxMain @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(context, attrs, defStyleAttr) {
    var realActivity: View? = null
        set(value) {
            if (field != value) removeView(field)
            field = value
            addView(field)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.getDefaultSize(0, widthMeasureSpec)
        var height = View.getDefaultSize(0, heightMeasureSpec)

        height -= context.navigationBarHeiht()

        setMeasuredDimension(width, height)

        val contentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width)
        val contentHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, height)
        realActivity?.measure(contentWidth, contentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t
        realActivity?.layout(0, 0, width, height)
    }
}