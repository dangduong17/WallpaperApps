package pion.tech.pionbase.feature.setting

import kotlinx.coroutines.launch
import com.google.android.material.color.DynamicColors
import pion.tech.pionbase.base.BaseViewModel
import pion.tech.pionbase.base.launchIO
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.domain.usecase.language.GetLanguageUseCase
import pion.tech.pionbase.domain.usecase.settings.ClearCacheUseCase
import pion.tech.pionbase.domain.usecase.settings.FormatCacheSizeUseCase
import pion.tech.pionbase.domain.usecase.settings.GetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.GetBatterySaverSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.GetCacheSizeUseCase
import pion.tech.pionbase.domain.usecase.settings.GetThemeSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetAutoWallpaperSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetBatterySaverSettingsUseCase
import pion.tech.pionbase.domain.usecase.settings.SetThemeSettingsUseCase
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.ThemeManager

data class SettingUiState(
    val autoWallpaperEnabled: Boolean = false,
    val autoWallpaperInterval: Long = 60L,
    val currentLanguageCode: String = "",
    val themeMode: Int = ThemeManager.MODE_SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val isDynamicColorAvailable: Boolean = false,
    val batterySaverEnabled: Boolean = false,
    val cacheSizeFormatted: String = "0 B",
    val isClearingCache: Boolean = false,
)

class SettingViewModel(
    private val getSettingsUseCase: GetAutoWallpaperSettingsUseCase,
    private val setSettingsUseCase: SetAutoWallpaperSettingsUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val setThemeSettingsUseCase: SetThemeSettingsUseCase,
    private val getBatterySaverSettingsUseCase: GetBatterySaverSettingsUseCase,
    private val setBatterySaverSettingsUseCase: SetBatterySaverSettingsUseCase,
    private val getCacheSizeUseCase: GetCacheSizeUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val formatCacheSizeUseCase: FormatCacheSizeUseCase,
) : BaseViewModel<SettingUiState, Nothing>(SettingUiState()) {

    init {
        setState { copy(isDynamicColorAvailable = DynamicColors.isDynamicColorAvailable()) }
        loadSettings()
        loadCacheSize()
    }

    private fun loadSettings() {
        launchIO {
            launch {
                getSettingsUseCase.getEnabled().collect { result ->
                    if (result is Result.Success) setState { copy(autoWallpaperEnabled = result.data) }
                }
            }
            launch {
                getSettingsUseCase.getInterval().collect { result ->
                    if (result is Result.Success) setState { copy(autoWallpaperInterval = result.data) }
                }
            }
            launch {
                getLanguageUseCase().collect { result ->
                    if (result is Result.Success) setState { copy(currentLanguageCode = result.data) }
                }
            }
            launch {
                getThemeSettingsUseCase.getThemeMode().collect { result ->
                    if (result is Result.Success) setState { copy(themeMode = result.data) }
                }
            }
            launch {
                getThemeSettingsUseCase.getDynamicColorEnabled().collect { result ->
                    if (result is Result.Success) setState { copy(dynamicColorEnabled = result.data) }
                }
            }
            launch {
                getBatterySaverSettingsUseCase().collect { result ->
                    if (result is Result.Success) setState { copy(batterySaverEnabled = result.data) }
                }
            }
        }
    }

    fun loadCacheSize() {
        launchIO {
            getCacheSizeUseCase().collect { result ->
                if (result is Result.Success) {
                    val formatted = formatCacheSizeUseCase(result.data)
                    setState { copy(cacheSizeFormatted = formatted) }
                }
            }
        }
    }

    fun clearCache(onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        setState { copy(isClearingCache = true) }
        launchIO {
            clearCacheUseCase().collect { result ->
                if (result is Result.Success) {
                    loadCacheSize()
                    setState { copy(isClearingCache = false) }
                    launchMain { onSuccess() }
                } else {
                    setState { copy(isClearingCache = false) }
                    launchMain { onError() }
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

    fun setThemeMode(themeMode: Int) {
        launchIO {
            setThemeSettingsUseCase.setThemeMode(themeMode).collect { result ->
                if (result is Result.Success) {
                    setState { copy(themeMode = themeMode) }
                }
            }
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        launchIO {
            setThemeSettingsUseCase.setDynamicColorEnabled(enabled).collect { result ->
                if (result is Result.Success) {
                    setState { copy(dynamicColorEnabled = enabled) }
                }
            }
        }
    }

    fun setBatterySaverEnabled(enabled: Boolean) {
        launchIO {
            setBatterySaverSettingsUseCase(enabled).collect { result ->
                if (result is Result.Success) {
                    setState { copy(batterySaverEnabled = enabled) }
                }
            }
        }
    }
}
