package pion.tech.pionbase.service

import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import timber.log.Timber

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            val sharedPref = getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
            val path = sharedPref.getString("wallpaper_path", null)
            
            Timber.d("Wallpaper Service: onSurfaceCreated path: $path")
            
            if (path != null && mediaPlayer == null) {
                initMediaPlayer(path, holder)
            }
        }

        private fun initMediaPlayer(path: String, holder: SurfaceHolder) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(path)
                    setSurface(holder.surface)
                    isLooping = true
                    prepareAsync()
                    setOnPreparedListener { mp -> mp.start() }
                    setOnErrorListener { _, what, extra -> 
                        Timber.e("MediaPlayer Error: $what, $extra")
                        true 
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error initializing MediaPlayer")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (mediaPlayer == null) return
            if (visible) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
