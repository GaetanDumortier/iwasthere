package com.ap.iwasthere.models

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.ap.iwasthere.R
import com.ap.iwasthere.activities.student.StudentSignatureActivity

/**
 * A class which is responsible for drawing a Canvas, and adding a bitmap to it.
 * This way we can get the drawable content through the bitmap and save it for processing.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class CanvasView(context: Context?) : View(context) {
    private var bitmap: Bitmap? = null
    private var canvas: Canvas = Canvas()
    var path: Path = Path()
    private var bitmapPaint: Paint? = null
    private var paint: Paint? = null

    init {
        path = Path()
        paint = Paint()

        bitmapPaint = Paint(Paint.DITHER_FLAG)

        paint?.isAntiAlias = true
        paint?.isDither = true
        paint?.color = Color.BLACK
        paint?.style = Paint.Style.STROKE
        paint?.strokeJoin = Paint.Join.ROUND
        paint?.strokeCap = Paint.Cap.ROUND
        paint?.strokeWidth = 4f
    }

    fun getBitMap(view: View): Bitmap? {
        val bitMap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitMap)
        view.draw(canvas)

        return bitMap
    }

    fun clear() {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        path.reset()
        toggleDoneButtonState()
        invalidate()
    }

    @Override
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val pointX: Float = event!!.x
        val pointY: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(pointX, pointY)
                return true
            }
            MotionEvent.ACTION_MOVE -> path.lineTo(pointX, pointY)
            else -> return false
        }

        postInvalidate()
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap =
            Bitmap.createBitmap(
                this.width,
                this.height,
                Bitmap.Config.ARGB_8888
            )
        canvas = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, 0f, 0f, bitmapPaint)
        canvas.drawPath(path, paint!!)
        toggleDoneButtonState()
    }

    fun canvasIsEmpty(): Boolean {
        return path.isEmpty
    }

    private fun toggleDoneButtonState() {
        val doneButton = (context as StudentSignatureActivity).findViewById<Button>(R.id.btnSignatureDone)
        doneButton.isEnabled = !canvasIsEmpty()
    }

}