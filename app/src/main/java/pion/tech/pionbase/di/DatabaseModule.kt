package pion.tech.pionbase.di

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.dsl.module
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.data.database.AppDatabase
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.database.dao.DummyDAO
import pion.tech.pionbase.data.database.dao.WallpaperDao

val databaseModule =
    module {
        single<AppDatabase> {
            val builder =
                Room
                    .databaseBuilder(
                        get(),
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME,
                    ).enableMultiInstanceInvalidation()
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)

            if (BuildConfig.DEBUG) {
                builder.fallbackToDestructiveMigration()
            } else {
                builder.fallbackToDestructiveMigrationOnDowngrade()
            }

            builder.build()
        }

        single<DummyDAO> { get<AppDatabase>().dummyDAO() }
        single<WallpaperDao> { get<AppDatabase>().wallpaperDao() }
        single<CategoryDao> { get<AppDatabase>().categoryDao() }
    }
