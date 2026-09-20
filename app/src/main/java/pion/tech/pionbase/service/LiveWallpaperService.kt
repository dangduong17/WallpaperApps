package pion.tech.pionbase.service

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import android.graphics.drawable.Drawable
import android.graphics.Canvas
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
            
            if (!gifFile.exists()) {
                timber.log.Timber.e("DEBUG: File không tồn tại tại $filePath")
                return
            }

            // Load bằng InputStream để bypass vấn đề quyền truy cập file path
            val inputStream = gifFile.inputStream()
            
            Glide.with(applicationContext)
                .asGif()
                .load(inputStream) // Load từ stream, không load từ path
                .into(object : CustomTarget<com.bumptech.glide.load.resource.gif.GifDrawable>() {
                    override fun onResourceReady(
                        resource: com.bumptech.glide.load.resource.gif.GifDrawable,
                        transition: Transition<in com.bumptech.glide.load.resource.gif.GifDrawable>?
                    ) {
                        timber.log.Timber.d("DEBUG: Glide load GIF thành công từ Stream!")
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
                        resource.start()
                        drawGif()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        timber.log.Timber.e("DEBUG: Glide load GIF thất bại từ Stream!")
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {
                        gifDrawable = null
                    }
                })
        }

        private fun drawGif() {
            val holder = surfaceHolder
            val canvas = holder.lockCanvas() ?: return
            try {
                canvas.drawColor(android.graphics.Color.BLACK)
                gifDrawable?.let { gif ->
                    val prefs = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                    
                    val values = FloatArray(9)
                    for (i in 0..8) {
                        values[i] = prefs.getFloat("matrix_$i", if (i == 0 || i == 4 || i == 8) 1f else 0f)
                    }
                    
                    val matrix = android.graphics.Matrix()
                    matrix.setValues(values)
                    
                    canvas.concat(matrix)
                    gif.setBounds(0, 0, gif.intrinsicWidth, gif.intrinsicHeight)
                    gif.draw(canvas)
                }
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            gifDrawable?.stop()
        }
    }
}
