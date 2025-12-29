package com.easefun.polyvsdk.player.marker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.ViewGroup
import com.easefun.polyvsdk.util.PolyvScreenUtils

/**
 * @author Hoshiiro
 */
class PLVProgressMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var markers: List<PLVProgressMarker> = emptyList()
    private var duration: Long = 0

    fun setMarkers(markers: List<PLVProgressMarker>) {
        this.markers = markers
        layoutMarkers()
    }

    fun setDuration(duration: Long) {
        this.duration = duration
        layoutMarkers()
    }

    private fun layoutMarkers() {
        removeAllViews()
        markers.filter { it.time < duration }
            .forEach { marker ->
                val lp = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.topToTop = LayoutParams.PARENT_ID
                lp.bottomToBottom = LayoutParams.PARENT_ID
                lp.startToStart = LayoutParams.PARENT_ID
                lp.endToEnd = LayoutParams.PARENT_ID
                lp.horizontalBias = marker.time / duration.toFloat()
                addView(PLVProgressMarkerItemView(context, marker), lp)
            }
    }

}

private class PLVProgressMarkerItemView(
    context: Context,
    private val marker: PLVProgressMarker
) : AppCompatTextView(context, null, 0) {

    private val paddingHorizon = PolyvScreenUtils.dip2px(context, 6F)
    private val paddingVertical = PolyvScreenUtils.dip2px(context, 4F)
    private val radius = PolyvScreenUtils.dip2px(context, 12F)
    private val arrowWidth = PolyvScreenUtils.dip2px(context, 10F)
    private val arrowHeight = PolyvScreenUtils.dip2px(context, 8F)
    private val path = Path()
    private val paint = Paint()

    init {
        text = marker.title
        setTextColor(marker.textColor)
        textSize = 12F
        paint.color = marker.backgroundColor
        setOnClickListener { marker.onClick(marker) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measuredWidth + 2 * paddingHorizon,
            measuredHeight + 2 * paddingVertical + arrowHeight
        )
    }

    override fun onDraw(canvas: Canvas?) {
        val roundRectHeight = height - arrowHeight
        path.reset()
        path.moveTo(0F, radius.toFloat())
        path.arcTo(0F, 0F, radius.toFloat(), radius.toFloat(), 180F, 90F, false)
        path.lineTo(width.toFloat() - radius, 0F)
        path.arcTo(width.toFloat() - radius.toFloat(), 0F, width.toFloat(), radius.toFloat(), 270F, 90F, false)
        path.lineTo(width.toFloat(), roundRectHeight.toFloat() - radius.toFloat())
        path.arcTo(
            width.toFloat() - radius.toFloat(),
            roundRectHeight.toFloat() - radius.toFloat(),
            width.toFloat(),
            roundRectHeight.toFloat(),
            0F,
            90F,
            false
        )
        path.lineTo(width.toFloat() / 2 + arrowWidth.toFloat() / 2, roundRectHeight.toFloat())
        path.lineTo(width.toFloat() / 2, height.toFloat())
        path.lineTo(width.toFloat() / 2 - arrowWidth.toFloat() / 2, roundRectHeight.toFloat())
        path.lineTo(radius.toFloat(), roundRectHeight.toFloat())
        path.arcTo(
            0F,
            roundRectHeight.toFloat() - radius.toFloat(),
            radius.toFloat(),
            roundRectHeight.toFloat(),
            90F,
            90F,
            false
        )
        path.close()
        canvas?.drawPath(path, paint)

        canvas?.save()
        canvas?.translate(paddingHorizon.toFloat(), paddingVertical.toFloat())
        super.onDraw(canvas)
        canvas?.restore()
    }

}

data class PLVProgressMarker(
    val time: Long,
    val title: String,
    val customId: String,
    @ColorInt val backgroundColor: Int = 0x99000000.toInt(),
    @ColorInt val textColor: Int = Color.WHITE,
    var onClick: (marker: PLVProgressMarker) -> Unit = {}
)