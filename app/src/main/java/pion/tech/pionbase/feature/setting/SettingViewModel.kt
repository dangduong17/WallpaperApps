package pion.tech.pionbase.feature.setting

import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.domain.usecase.settings.GetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.util.Result

data class SettingUiState(
    val autoWallpaperEnabled: Boolean = false,
    val autoWallpaperInterval: Long = 60L
)

class SettingViewModel(
    private val getSettingsUseCase: GetAutoWallpaperSettingsUseCase,
    private val setSettingsUseCase: SetAutoWallpaperSettingsUseCase
) : BaseViewModel<SettingUiState, Nothing>(SettingUiState()) {

    init {
        loadSettings()
    }

    private fun loadSettings() {
        launchIO {
            getSettingsUseCase.getEnabled().collect { result ->
                if (result is Result.Success) {
                    setState { copy(autoWallpaperEnabled = result.data) }
                }
            }
        }
        launchIO {
            getSettingsUseCase.getInterval().collect { result ->
                if (result is Result.Success) {
                    setState { copy(autoWallpaperInterval = result.data) }
                }
            }
        }
    }

    fun setAutoWallpaperEnabled(enabled: Boolean) {
        launchIO {
            setSettingsUseCase.setEnabled(enabled).collect {
                if (it is Result.Success) {
                    setState { copy(autoWallpaperEnabled = enabled) }
                }
            }
        }
    }

    fun setAutoWallpaperInterval(interval: Long) {
        launchIO {
            setSettingsUseCase.setInterval(interval).collect {
                if (it is Result.Success) {
                    setState { copy(autoWallpaperInterval = interval) }
                }
            }
        }
    }
}
