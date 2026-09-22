package pion.tech.pionbase.di

import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import pion.tech.pionbase.worker.AutoWallpaperWorker

val workerModule = module {
    worker { AutoWallpaperWorker(get(), get(), get()) }
}
