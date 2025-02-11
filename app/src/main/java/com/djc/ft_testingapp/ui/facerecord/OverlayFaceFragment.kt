package com.djc.ft_testingapp.ui.facerecord

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Variable para almacenar el rectángulo de la cara detectada
    private var faceRect: Rect? = null

    // Configuración del Paint para dibujar el rectángulo (p.ej. verde y con cierto grosor)
    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8f  // Ajusta el grosor según lo necesites
    }


    fun setFaceRect(rect: Rect?) {
        faceRect = rect
        invalidate() // Fuerza el repintado de la vista
    }

    fun clear() {
        faceRect = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        faceRect?.let {
            canvas.drawRect(it, paint)
        }
    }
}