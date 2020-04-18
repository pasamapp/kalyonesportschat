package com.pasamapp.kalyonesportschat.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * Created by Emre on 24.05.2018.
 */
class GridImageView : AppCompatImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}