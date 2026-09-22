package pion.tech.pionbase.service

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Movie
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import timber.log.Timber
import java.io.File

@Suppress("DEPRECATION")
class LiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        Timber.d("DEBUG: LiveWallpaperService onCreateEngine")
        return GifWallpaperEngine()
    }

    inner class GifWallpaperEngine : Engine() {
        private var renderThread: HandlerThread? = null
        private var renderHandler: Handler? = null

        private var movie: Movie? = null
        private var startTime: Long = 0L
        private var lastLoadedTimestamp: Long = -1L
        @Volatile
        private var isVisible = false

        private val frameRunnable = Runnable {
            drawFrame()
        }

        private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "selected_gif_path" || key == "gif_updated_at") {
                Timber.d("DEBUG: SharedPreferences updated ($key), reloading GIF movie!")
                renderHandler?.post {
                    loadGifMovie()
                }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(false)

            val thread = HandlerThread("GifWallpaperThread").apply { start() }
            renderThread = thread
            renderHandler = Handler(thread.looper)

            getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(prefChangeListener)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Timber.d("DEBUG: onSurfaceCreated")
            renderHandler?.post {
                loadGifMovie()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Timber.d("DEBUG: onVisibilityChanged -> $visible")
            this.isVisible = visible
            renderHandler?.post {
                if (visible) {
                    val sharedPreferences = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                    val filePath = sharedPreferences.getString("selected_gif_path", null)
                    val file = if (!filePath.isNullOrEmpty()) File(filePath) else null
                    if (file != null && file.exists() && file.lastModified() != lastLoadedTimestamp) {
                        Timber.d("DEBUG: File timestamp changed on visibility changed, reloading!")
                        loadGifMovie()
                    } else if (movie == null) {
                        loadGifMovie()
                    } else {
                        startTime = 0L
                        scheduleNextFrame(0L)
                    }
                } else {
                    renderHandler?.removeCallbacks(frameRunnable)
                }
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            renderHandler?.post {
                drawFrame()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Timber.d("DEBUG: onSurfaceDestroyed")
            this.isVisible = false
            renderHandler?.removeCallbacks(frameRunnable)
        }

        override fun onDestroy() {
            super.onDestroy()
            Timber.d("DEBUG: onDestroy Engine")
            getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(prefChangeListener)
            this.isVisible = false
            renderHandler?.removeCallbacks(frameRunnable)
            renderThread?.quitSafely()
            renderThread = null
            renderHandler = null
            movie = null
        }

        private fun loadGifMovie() {
            try {
                val sharedPreferences = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                val filePath = sharedPreferences.getString("selected_gif_path", null)
                if (filePath.isNullOrEmpty()) {
                    Timber.e("DEBUG: selected_gif_path is null or empty")
                    return
                }

                val gifFile = File(filePath)
                if (!gifFile.exists() || gifFile.length() == 0L) {
                    Timber.e("DEBUG: GIF file does not exist or is empty at $filePath")
                    return
                }

                val decoded = gifFile.inputStream().use { Movie.decodeStream(it) }
                    ?: Movie.decodeFile(gifFile.absolutePath)

                if (decoded != null && decoded.duration() > 0) {
                    this.movie = decoded
                    this.lastLoadedTimestamp = gifFile.lastModified()
                    Timber.d("DEBUG: GIF Loaded successfully via Movie! Duration: ${decoded.duration()}ms, Size: ${decoded.width()}x${decoded.height()}")
                    if (isVisible) {
                        startTime = 0L
                        scheduleNextFrame(0L)
                    }
                } else {
                    Timber.e("DEBUG: Failed to decode GIF with Movie")
                }
            } catch (e: Exception) {
                Timber.e(e, "DEBUG: Exception while loading GIF movie")
            }
        }

        private fun scheduleNextFrame(delayMs: Long) {
            renderHandler?.removeCallbacks(frameRunnable)
            if (isVisible && movie != null) {
                renderHandler?.postDelayed(frameRunnable, delayMs)
            }
        }

        private fun drawFrame() {
            if (!isVisible) return

            val mMovie = movie ?: return
            val holder = surfaceHolder ?: return
            if (!holder.surface.isValid) return

            val now = SystemClock.uptimeMillis()
            if (startTime == 0L) {
                startTime = now
            }

            val duration = mMovie.duration().let { if (it <= 0) 1000 else it }
            val relTime = ((now - startTime) % duration).toInt()
            mMovie.setTime(relTime)

            var canvas: android.graphics.Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK)

                    val movieWidth = mMovie.width().toFloat()
                    val movieHeight = mMovie.height().toFloat()
                    val canvasWidth = canvas.width.toFloat()
                    val canvasHeight = canvas.height.toFloat()

                    if (movieWidth > 0f && movieHeight > 0f && canvasWidth > 0f && canvasHeight > 0f) {
                        val scale: Float
                        var dx = 0f
                        var dy = 0f

                        if (movieWidth * canvasHeight > canvasWidth * movieHeight) {
                            scale = canvasHeight / movieHeight
                            dx = (canvasWidth - movieWidth * scale) * 0.5f
                        } else {
                            scale = canvasWidth / movieWidth
                            dy = (canvasHeight - movieHeight * scale) * 0.5f
                        }

                        canvas.save()
                        canvas.translate(dx, dy)
                        canvas.scale(scale, scale)
                        mMovie.draw(canvas, 0f, 0f)
                        canvas.restore()
                    } else {
                        mMovie.draw(canvas, 0f, 0f)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "DEBUG: Exception during lockCanvas or draw")
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        Timber.e(e, "DEBUG: Exception during unlockCanvasAndPost")
                    }
                }
            }

            if (isVisible) {
                scheduleNextFrame(33L) // ~30 FPS
            }
        }
    }
}
