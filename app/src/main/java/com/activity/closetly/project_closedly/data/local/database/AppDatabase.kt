package com.activity.closetly.project_closedly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.activity.closetly.project_closedly.data.local.dao.GarmentDao
import com.activity.closetly.project_closedly.data.local.dao.OutfitDao
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity

@Database(
    entities = [GarmentEntity::class, OutfitEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun garmentDao(): GarmentDao
    abstract fun outfitDao(): OutfitDao
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE garments ADD COLUMN rating INTEGER NOT NULL DEFAULT 0")
    }
}