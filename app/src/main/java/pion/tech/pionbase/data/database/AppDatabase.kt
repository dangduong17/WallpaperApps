package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.model.dummy.DummyEntity
import pion.tech.pionbase.data.database.dao.DummyDao
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.database.dao.WallpaperHistoryDao
import pion.tech.pionbase.data.model.wallpaper.WallpaperEntity
import pion.tech.pionbase.data.model.wallpaper.CategoryEntity
import pion.tech.pionbase.data.model.history.WallpaperHistoryEntity

@Database(entities = [DummyEntity::class, WallpaperEntity::class, CategoryEntity::class, WallpaperHistoryEntity::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun categoryDao(): CategoryDao
    abstract fun wallpaperHistoryDao(): WallpaperHistoryDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
