package pion.tech.pionbase.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pion.tech.pionbase.R
import pion.tech.pionbase.domain.usecase.wallpaper.SetRandomNextWallpaperUseCase
import pion.tech.pionbase.util.Result
import timber.log.Timber

class RandomWallpaperTileService : TileService(), KoinComponent {

    private val setRandomNextWallpaperUseCase: SetRandomNextWallpaperUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartListening() {
        super.onStartListening()
        updateTileState(Tile.STATE_INACTIVE)
    }

    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

        if (tile.state == Tile.STATE_UNAVAILABLE) return

        updateTileState(Tile.STATE_UNAVAILABLE, getString(R.string.qs_tile_changing_wallpaper))

        serviceScope.launch {
            val result = setRandomNextWallpaperUseCase().first()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(
                            applicationContext,
                            R.string.qs_tile_wallpaper_changed_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Error -> {
                        Timber.e(result.error, "RandomWallpaperTileService error")
                        Toast.makeText(
                            applicationContext,
                            R.string.qs_tile_wallpaper_changed_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                updateTileState(Tile.STATE_INACTIVE, getString(R.string.qs_tile_random_wallpaper))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun updateTileState(state: Int, subtitle: String? = null) {
        try {
            val tile = qsTile ?: return
            tile.state = state
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && subtitle != null) {
                tile.subtitle = subtitle
            }
            tile.updateTile()
        } catch (e: Exception) {
            Timber.e(e, "Error updating tile state")
        }
    }
}
