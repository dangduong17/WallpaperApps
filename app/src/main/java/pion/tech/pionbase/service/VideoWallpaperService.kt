package pion.tech.pionbase.service

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import pion.tech.pionbase.util.Constant
import timber.log.Timber
import java.io.File

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null
        private var currentPath: String? = null

        private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constant.KEY_WALLPAPER_PATH || key == Constant.KEY_VIDEO_UPDATED_AT) {
                Timber.d("VideoWallpaperService: SharedPreferences updated ($key), reloading Video!")
                reloadVideo()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            getSharedPreferences(Constant.PREF_WALLPAPER, MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(prefChangeListener)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            reloadVideo()
        }

        private fun reloadVideo() {
            val holder = surfaceHolder ?: return
            val sharedPref = getSharedPreferences(Constant.PREF_WALLPAPER, MODE_PRIVATE)
            val path = sharedPref.getString(Constant.KEY_WALLPAPER_PATH, null)

            Timber.d("VideoWallpaperService: reloadVideo path: $path")

            if (path.isNullOrEmpty() || !File(path).exists()) {
                Timber.e("Video file path is invalid or file does not exist: $path")
                releaseMediaPlayer()
                return
            }

            if (path == currentPath && mediaPlayer != null) {
                if (!mediaPlayer!!.isPlaying && isVisible) {
                    mediaPlayer?.start()
                }
                return
            }

            releaseMediaPlayer()
            initMediaPlayer(path, holder)
        }

        private fun initMediaPlayer(path: String, holder: SurfaceHolder) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(path)
                    setSurface(holder.surface)
                    setVolume(0f, 0f)
                    isLooping = true
                    setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        if (isVisible) {
                            mp.start()
                        }
                    }
                    setOnErrorListener { _, what, extra ->
                        Timber.e("MediaPlayer Error: $what, $extra")
                        true
                    }
                }
                currentPath = path
            } catch (e: Exception) {
                Timber.e(e, "Error initializing MediaPlayer for video wallpaper")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                if (mediaPlayer == null) {
                    reloadVideo()
                } else {
                    mediaPlayer?.start()
                }
            } else {
                mediaPlayer?.pause()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            releaseMediaPlayer()
        }

        override fun onDestroy() {
            super.onDestroy()
            getSharedPreferences(Constant.PREF_WALLPAPER, MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(prefChangeListener)
            releaseMediaPlayer()
        }

        private fun releaseMediaPlayer() {
            try {
                mediaPlayer?.stop()
            } catch (_: Exception) {}
            try {
                mediaPlayer?.release()
            } catch (_: Exception) {}
            mediaPlayer = null
            currentPath = null
        }
    }
}
