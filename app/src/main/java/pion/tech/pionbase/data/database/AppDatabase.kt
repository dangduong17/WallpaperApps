package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.model.dummy.DummyEntity
import pion.tech.pionbase.data.database.dao.DummyDAO
import pion.tech.pionbase.data.database.dao.WallpaperDao
import pion.tech.pionbase.data.database.dao.CategoryDao
import pion.tech.pionbase.data.model.wallpaper.WallpaperEntity
import pion.tech.pionbase.data.model.wallpaper.CategoryEntity

@Database(entities = [DummyEntity::class, WallpaperEntity::class, CategoryEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
