package pion.tech.pionbase.service

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper

class LiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        timber.log.Timber.d("DEBUG: LiveWallpaperService onCreateEngine")
        return GifWallpaperEngine()
    }

    inner class GifWallpaperEngine : Engine() {
        private var gifDrawable: com.bumptech.glide.load.resource.gif.GifDrawable? = null
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(false)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            timber.log.Timber.d("DEBUG: onSurfaceCreated")
            loadGif()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            timber.log.Timber.d("DEBUG: onSurfaceDestroyed")
            visible = false
            gifDrawable?.stop()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                gifDrawable?.start()
                drawGif()
            } else {
                gifDrawable?.stop()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawGif()
        }

        private fun loadGif() {
            val sharedPreferences = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
            val filePath = sharedPreferences.getString("selected_gif_path", null) ?: return
            val gifFile = java.io.File(filePath)
            
            if (!gifFile.exists() || gifFile.length() == 0L) {
                timber.log.Timber.e("DEBUG: File không tồn tại hoặc rỗng tại $filePath")
                return
            }

            Glide.with(applicationContext)
                .asGif()
                .load(gifFile)
                .into(object : CustomTarget<com.bumptech.glide.load.resource.gif.GifDrawable>() {
                    override fun onResourceReady(
                        resource: com.bumptech.glide.load.resource.gif.GifDrawable,
                        transition: Transition<in com.bumptech.glide.load.resource.gif.GifDrawable>?
                    ) {
                        timber.log.Timber.d("DEBUG: Glide load GIF thành công từ File!")
                        gifDrawable = resource
                        resource.setLoopCount(com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER)
                        
                        resource.callback = object : Drawable.Callback {
                            override fun invalidateDrawable(who: Drawable) {
                                drawGif()
                            }
                            override fun scheduleDrawable(who: Drawable, what: Runnable, whenTime: Long) {
                                handler.postAtTime(what, whenTime)
                            }
                            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                                handler.removeCallbacks(what)
                            }
                        }
                        
                        if (visible) {
                            resource.start()
                            drawGif()
                        } else {
                            resource.stop()
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        timber.log.Timber.e("DEBUG: Glide load GIF thất bại từ File!")
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {
                        gifDrawable?.stop()
                        gifDrawable = null
                    }
                })
        }

        private fun drawGif() {
            if (!visible) return
            val holder = surfaceHolder ?: return
            if (!holder.surface.isValid) return
            
            val canvas = try {
                holder.lockCanvas()
            } catch (e: Exception) {
                timber.log.Timber.e(e, "Error locking canvas")
                null
            } ?: return

            try {
                canvas.drawColor(android.graphics.Color.BLACK)
                gifDrawable?.let { gif ->
                    val canvasWidth = canvas.width.toFloat()
                    val canvasHeight = canvas.height.toFloat()
                    val gifWidth = gif.intrinsicWidth.toFloat()
                    val gifHeight = gif.intrinsicHeight.toFloat()

                    if ((canvasWidth > 0f) && (canvasHeight > 0f) && (gifWidth > 0f) && (gifHeight > 0f)) {
                        val scale: Float
                        var dx = 0f
                        var dy = 0f

                        if (gifWidth * canvasHeight > canvasWidth * gifHeight) {
                            scale = canvasHeight / gifHeight
                            dx = (canvasWidth - gifWidth * scale) * 0.5f
                        } else {
                            scale = canvasWidth / gifWidth
                            dy = (canvasHeight - gifHeight * scale) * 0.5f
                        }

                        val matrix = android.graphics.Matrix()
                        matrix.setScale(scale, scale)
                        matrix.postTranslate(dx, dy)

                        canvas.concat(matrix)
                        gif.setBounds(0, 0, gif.intrinsicWidth, gif.intrinsicHeight)
                        gif.draw(canvas)
                    } else {
                        gif.setBounds(0, 0, canvas.width, canvas.height)
                        gif.draw(canvas)
                    }
                }
            } catch (e: Exception) {
                timber.log.Timber.e(e, "Error drawing GIF on canvas")
            } finally {
                try {
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: Exception) {
                    timber.log.Timber.e(e, "Error unlocking canvas")
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            gifDrawable?.stop()
        }
    }
}
