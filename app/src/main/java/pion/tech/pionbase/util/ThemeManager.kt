package pion.tech.pionbase.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository

object ThemeManager {
    const val MODE_SYSTEM = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    fun applyThemeMode(themeMode: Int) {
        val nightMode = when (themeMode) {
            MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    fun applyDynamicColorsIfEnabled(application: Application, enabled: Boolean) {
        if (enabled && DynamicColors.isDynamicColorAvailable()) {
            DynamicColors.applyToActivitiesIfAvailable(application)
        }
    }

    fun init(application: Application, dataStoreRepository: DataStoreRepository) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStoreRepository.getThemeMode().collect { result ->
                if (result is Result.Success) {
                    applyThemeMode(result.data)
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            dataStoreRepository.getDynamicColorEnabled().collect { result ->
                if (result is Result.Success) {
                    applyDynamicColorsIfEnabled(application, result.data)
                }
            }
        }
    }
}
