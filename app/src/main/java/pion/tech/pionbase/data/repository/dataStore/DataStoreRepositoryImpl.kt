package pion.tech.pionbase.data.repository.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.repository.BaseRepository
import pion.tech.pionbase.util.Result

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : BaseRepository(), DataStoreRepository {
    private val isPremiumKey = booleanPreferencesKey("isPremiumKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunchKey")

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
}
