package pion.tech.pionbase.data.repository.dataStore

import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.util.Result

interface DataStoreRepository {
    fun getIsPremium(): Flow<Result<Boolean>>

    fun setIsPremium(isPremium: Boolean): Flow<Result<Unit>>

    fun getToken(): Flow<Result<String?>>

    fun setToken(token: String): Flow<Result<Unit>>

    fun getIsFirstLaunch(): Flow<Result<Boolean>>

    fun setIsFirstLaunch(isFirstLaunch: Boolean): Flow<Result<Unit>>

    fun getLanguage(): Flow<Result<String>>

    fun setLanguage(localeCode: String): Flow<Result<Unit>>

    fun getAutoWallpaperEnabled(): Flow<Result<Boolean>>

    fun setAutoWallpaperEnabled(enabled: Boolean): Flow<Result<Unit>>

    fun getAutoWallpaperInterval(): Flow<Result<Long>>

    fun setAutoWallpaperInterval(intervalMinutes: Long): Flow<Result<Unit>>
}
