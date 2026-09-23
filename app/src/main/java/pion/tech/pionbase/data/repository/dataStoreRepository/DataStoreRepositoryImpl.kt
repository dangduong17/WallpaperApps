package pion.tech.pionbase.data.repository.dataStoreRepository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result
import pion.tech.pionbase.util.ThemeManager

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : BaseRepository(), DataStoreRepository {
    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunchKey")
    private val languageKey = stringPreferencesKey("languageKey")
    private val autoWallpaperEnabledKey = booleanPreferencesKey("autoWallpaperEnabledKey")
    private val autoWallpaperIntervalKey = longPreferencesKey("autoWallpaperIntervalKey")
    private val themeModeKey = intPreferencesKey("themeModeKey") // 0: SYSTEM, 1: LIGHT, 2: DARK
    private val dynamicColorEnabledKey = booleanPreferencesKey("dynamicColorEnabledKey")

    override fun getIsPremium(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isPremiumKey] ?: false
        }

    override fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isPremiumKey] = isPremium
            }
        }

    override fun getToken(): Flow<Result<String?>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[tokenKey]
        }

    override fun setToken(token: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[tokenKey] = token
            }
        }

    override fun getIsFirstLaunch(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[isFirstLaunchKey] ?: true
        }

    override fun setIsFirstLaunch(isFirstLaunch: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[isFirstLaunchKey] = isFirstLaunch
            }
        }

    override fun getLanguage(): Flow<Result<String>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[languageKey] ?: "en"
        }

    override fun setLanguage(localeCode: String): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[languageKey] = localeCode
            }
        }

    override fun getAutoWallpaperEnabled(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[autoWallpaperEnabledKey] ?: false
        }

    override fun setAutoWallpaperEnabled(enabled: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[autoWallpaperEnabledKey] = enabled
            }
        }

    override fun getAutoWallpaperInterval(): Flow<Result<Long>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[autoWallpaperIntervalKey] ?: 60L // Default 60 mins
        }

    override fun setAutoWallpaperInterval(intervalMinutes: Long): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[autoWallpaperIntervalKey] = intervalMinutes
            }
        }

    override fun getThemeMode(): Flow<Result<Int>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[themeModeKey] ?: ThemeManager.MODE_SYSTEM
        }

    override fun setThemeMode(themeMode: Int): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[themeModeKey] = themeMode
            }
        }

    override fun getDynamicColorEnabled(): Flow<Result<Boolean>> =
        dataStore.data.executeDataWithFlowCall { prefs ->
            prefs[dynamicColorEnabledKey] ?: true // Default: true
        }

    override fun setDynamicColorEnabled(enabled: Boolean): Flow<Result<Unit>> =
        executeDataCall {
            dataStore.edit {
                it[dynamicColorEnabledKey] = enabled
            }
        }
}
