package pion.tech.pionbase.feature.language

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository
import pion.tech.pionbase.util.Result

object LanguageManager {
    fun setLocale(localeCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(localeCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun init(context: Context, dataStoreRepository: DataStoreRepository) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStoreRepository.getLanguage().collect { result ->
                if (result is Result.Success) {
                    setLocale(result.data)
                }
            }
        }
    }
}
