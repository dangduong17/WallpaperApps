package pion.tech.pionbase.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository

object ThemeManager {
    const val MODE_SYSTEM = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    @Volatile
    var isDynamicColorEnabled = true
        private set

    fun applyThemeMode(themeMode: Int) {
        val nightMode = when (themeMode) {
            MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        isDynamicColorEnabled = enabled
    }

    fun init(application: Application, dataStoreRepository: DataStoreRepository) {
        if (DynamicColors.isDynamicColorAvailable()) {
            val options = DynamicColorsOptions.Builder()
                .setPrecondition { _, _ -> isDynamicColorEnabled }
                .build()
            DynamicColors.applyToActivitiesIfAvailable(application, options)
        }

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
                    isDynamicColorEnabled = result.data
                }
            }
        }
    }
}
