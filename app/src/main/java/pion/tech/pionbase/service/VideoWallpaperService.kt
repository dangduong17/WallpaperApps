package pion.tech.pionbase.service

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.BatterySaverManager
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.Result
import timber.log.Timber
import java.io.File

class VideoWallpaperService : WallpaperService(), KoinComponent {

    private val dataStoreRepository: DataStoreRepository by inject()

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null
        private var currentPath: String? = null
        @Volatile
        private var isBatterySaverActive = false

        private val serviceScope = CoroutineScope(Dispatchers.IO)
        private var batteryJob: Job? = null

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

            observeBatterySaver()
        }

        private fun observeBatterySaver() {
            batteryJob?.cancel()
            batteryJob = serviceScope.launch {
                val batterySaverPrefFlow = dataStoreRepository.getBatterySaverEnabled().map {
                    (it as? Result.Success)?.data == true
                }
                val batteryStateFlow = BatterySaverManager.observeBatterySaverState(applicationContext)

                combine(batterySaverPrefFlow, batteryStateFlow) { prefEnabled, isLowOrPowerSave ->
                    prefEnabled && isLowOrPowerSave
                }.collect { active ->
                    Timber.d("VideoWallpaperService: isBatterySaverActive = $active")
                    isBatterySaverActive = active
                    if (active) {
                        mediaPlayer?.pause()
                    } else if (isVisible && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                        mediaPlayer?.start()
                    }
                }
            }
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
                if (!mediaPlayer!!.isPlaying && isVisible && !isBatterySaverActive) {
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
                        if (isVisible && !isBatterySaverActive) {
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
                } else if (!isBatterySaverActive) {
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
            batteryJob?.cancel()
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
