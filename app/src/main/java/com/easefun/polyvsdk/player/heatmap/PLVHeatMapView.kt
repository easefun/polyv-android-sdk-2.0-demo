package com.easefun.polyvsdk.player.heatmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

/**
 * @author Hoshiiro
 */
class PLVHeatMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastWidth = 0
    private var lastHeight = 0
    private val heatMapPath = Path()
    private val heatMapPaint = Paint().apply { color = 0x4CFFFFFF }
    private var heatMapData: List<Double> = EXAMPLE_HEAT_MAP_DATA

    fun setHeatMapData(heatMapData: List<Double>) {
        this.heatMapData = heatMapData
        rebuildPath()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width
            lastHeight = height
            rebuildPath()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPath(heatMapPath, heatMapPaint)
    }

    private fun rebuildPath() {
        heatMapPath.reset()
        val data = heatMapData.normalized()
        if (data.isEmpty()) {
            return
        }
        if (data.size == 1) {
            heatMapPath.addRect(0F, 0F, width.toFloat(), height.toFloat(), Path.Direction.CCW)
            return
        }
        val space = width.toFloat() / (data.size - 1)
        val points = data.mapIndexed { index, value ->
            PointF(index * space, height * (1 - value).toFloat())
        }
        heatMapPath.moveTo(points[0].x, points[0].y)
        points.windowed(2).forEach {
            val left = it[0]
            val right = it[1]
            val x1 = left.x + space / 2
            val y1 = left.y
            val x2 = right.x - space / 2
            val y2 = right.y
            val x3 = right.x
            val y3 = right.y
            heatMapPath.cubicTo(x1, y1, x2, y2, x3, y3)
        }
        heatMapPath.lineTo(width.toFloat(), height.toFloat())
        heatMapPath.lineTo(0F, height.toFloat())
        heatMapPath.close()
    }

}

class PLVHeatMapMask @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        paint.shader = LinearGradient(
            0F,
            height.toFloat(),
            0F,
            0F,
            0x33000000,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
    }
}

private fun List<Double>.normalized(): List<Double> {
    val max = this.maxOrNull() ?: return this
    return this.map { it / max }
}

private val EXAMPLE_HEAT_MAP_DATA = listOf(
    548.0,
    840.0,
    91.0,
    282.0,
    381.0,
    340.0,
    40.0,
    80.0,
    18.0,
    254.0,
    767.0,
    126.0,
    561.0,
    86.0,
    287.0,
    900.0,
    968.0,
    194.0,
    753.0,
    729.0,
    524.0,
    325.0,
    332.0,
    704.0,
    586.0,
    414.0,
    852.0,
    728.0,
    585.0,
    358.0
)