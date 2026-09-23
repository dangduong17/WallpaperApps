package pion.tech.pionbase.app

import android.app.Application
import androidx.work.Configuration
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.khaipv.recovery.core.Recovery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.lifecycleCallback.ActivityLifecycleCallbacksImpl
import pion.tech.pionbase.di.appModules
import timber.log.Timber

class MyApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
            
    override fun onCreate() {
        super.onCreate()
        
        com.google.firebase.FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(appModules)
        }
        
        // Init Language and Theme
        CoroutineScope(Dispatchers.Main).launch {
            val dataStoreRepository: pion.tech.pionbase.data.repository.dataStoreRepository.DataStoreRepository = get()
            pion.tech.pionbase.feature.language.LanguageManager.init(this@MyApplication, dataStoreRepository)
            pion.tech.pionbase.util.ThemeManager.init(this@MyApplication, dataStoreRepository)
        }
        
        setupRemoteConfig()

        if (BuildConfig.DEBUG) {
            Recovery
                .getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity::class.java)
                .recoverEnabled(true)
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .init(this)

            Timber.plant(Timber.DebugTree())
        }
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())
    }

    private fun setupRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = get()
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds =
                    if (BuildConfig.DEBUG) {
                        REMOTE_CONFIG_FETCH_INTERVAL_DEBUG
                    } else {
                        REMOTE_CONFIG_FETCH_INTERVAL_RELEASE
                    }
            },
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    companion object {
        private const val REMOTE_CONFIG_FETCH_INTERVAL_DEBUG = 30L
        private const val REMOTE_CONFIG_FETCH_INTERVAL_RELEASE = 3600L
    }
}
