package pion.tech.pionbase.feature.quoteEditor.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.atan2

class StickerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Text Properties
    var text: String = "Your Quote Here"
        set(value) {
            field = value
            invalidate()
        }

    var textTypeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            textPaint.typeface = value
            invalidate()
        }

    var textSizeSp: Float = 28f
        set(value) {
            field = value
            textPaint.textSize = value * resources.displayMetrics.scaledDensity
            invalidate()
        }

    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    var textAlignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
        set(value) {
            field = value
            invalidate()
        }

    // Shadow Properties
    var shadowRadius: Float = 8f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowDx: Float = 3f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowDy: Float = 3f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowColor: Int = Color.BLACK
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    // Background Properties
    var textBgColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            bgPaint.color = value
            invalidate()
        }

    var textBgCornerRadius: Float = 16f
        set(value) {
            field = value
            invalidate()
        }

    var textBgPaddingDp: Float = 16f
        set(value) {
            field = value
            invalidate()
        }

    // Transform State
    private var matrixX = 0f
    private var matrixY = 0f
    private var scaleFactor = 1.0f
    private var rotationDegrees = 0f

    // Touch Handling State
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isTransforming = false

    private var previousAngle = 0f

    // Selection border
    var isSelectedSticker: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f * resources.displayMetrics.scaledDensity
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#80FFFFFF".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.4f, 4.0f)
            invalidate()
            return true
        }
    })

    init {
        updateShadow()
    }

    private fun updateShadow() {
        if (shadowRadius > 0) {
            textPaint.setShadowLayer(
                shadowRadius * resources.displayMetrics.density,
                shadowDx * resources.displayMetrics.density,
                shadowDy * resources.displayMetrics.density,
                shadowColor
            )
        } else {
            textPaint.clearShadowLayer()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (matrixX == 0f && matrixY == 0f && w > 0 && h > 0) {
            matrixX = w / 2f
            matrixY = h / 2f
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (text.isEmpty()) return

        canvas.save()
        canvas.translate(matrixX, matrixY)
        canvas.rotate(rotationDegrees)
        canvas.scale(scaleFactor, scaleFactor)

        val paddingPx = textBgPaddingDp * resources.displayMetrics.density
        val maxWidth = (width * 0.8f).coerceAtLeast(200f)

        @Suppress("DEPRECATION")
        val staticLayout = StaticLayout(
            text,
            textPaint,
            maxWidth.toInt(),
            textAlignment,
            1.0f,
            0.0f,
            false
        )

        val textWidth = staticLayout.width.toFloat()
        val textHeight = staticLayout.height.toFloat()

        val bounds = RectF(
            -textWidth / 2f - paddingPx,
            -textHeight / 2f - paddingPx,
            textWidth / 2f + paddingPx,
            textHeight / 2f + paddingPx
        )

        // Draw background if set
        if (textBgColor != Color.TRANSPARENT) {
            val cornerRadiusPx = textBgCornerRadius * resources.displayMetrics.density
            canvas.drawRoundRect(bounds, cornerRadiusPx, cornerRadiusPx, bgPaint)
        }

        // Draw selection dash border if active
        if (isSelectedSticker) {
            canvas.drawRoundRect(bounds, 12f, 12f, borderPaint)
        }

        // Draw text layout
        canvas.save()
        canvas.translate(-textWidth / 2f, -textHeight / 2f)
        staticLayout.draw(canvas)
        canvas.restore()

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                lastTouchX = event.x
                lastTouchY = event.y
                isSelectedSticker = true
                invalidate()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    isTransforming = true
                    previousAngle = getAngle(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isTransforming && event.pointerCount >= 2) {
                    val newAngle = getAngle(event)
                    rotationDegrees += (newAngle - previousAngle)
                    previousAngle = newAngle
                    invalidate()
                } else if (!scaleDetector.isInProgress) {
                    val pointerIndex = event.findPointerIndex(activePointerId)
                    if (pointerIndex != -1) {
                        val currX = event.getX(pointerIndex)
                        val currY = event.getY(pointerIndex)
                        val dx = currX - lastTouchX
                        val dy = currY - lastTouchY

                        matrixX += dx
                        matrixY += dy

                        lastTouchX = currX
                        lastTouchY = currY
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 2) {
                    isTransforming = false
                }
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isTransforming = false
            }

            MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isTransforming = false
            }
        }
        return true
    }

    private fun getAngle(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(deltaY, deltaX)
        return Math.toDegrees(radians).toFloat()
    }

    /**
     * Render the edited sticker text onto a background wallpaper bitmap
     */
    fun renderToBitmap(bgBitmap: Bitmap): Bitmap {
        val resultBitmap = bgBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)

        val scaleX = resultBitmap.width.toFloat() / width.toFloat()
        val scaleY = resultBitmap.height.toFloat() / height.toFloat()

        canvas.save()
        canvas.scale(scaleX, scaleY)

        canvas.translate(matrixX, matrixY)
        canvas.rotate(rotationDegrees)
        canvas.scale(scaleFactor, scaleFactor)

        val paddingPx = textBgPaddingDp * resources.displayMetrics.density
        val maxWidth = (width * 0.8f).coerceAtLeast(200f)

        @Suppress("DEPRECATION")
        val staticLayout = StaticLayout(
            text,
            textPaint,
            maxWidth.toInt(),
            textAlignment,
            1.0f,
            0.0f,
            false
        )

        val textWidth = staticLayout.width.toFloat()
        val textHeight = staticLayout.height.toFloat()

        val bounds = RectF(
            -textWidth / 2f - paddingPx,
            -textHeight / 2f - paddingPx,
            textWidth / 2f + paddingPx,
            textHeight / 2f + paddingPx
        )

        if (textBgColor != Color.TRANSPARENT) {
            val cornerRadiusPx = textBgCornerRadius * resources.displayMetrics.density
            canvas.drawRoundRect(bounds, cornerRadiusPx, cornerRadiusPx, bgPaint)
        }

        canvas.save()
        canvas.translate(-textWidth / 2f, -textHeight / 2f)
        staticLayout.draw(canvas)
        canvas.restore()

        canvas.restore()
        return resultBitmap
    }
}
