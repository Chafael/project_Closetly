package com.activity.closetly.project_closedly.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.activity.closetly.project_closedly.data.local.dao.GarmentDao
import com.activity.closetly.project_closedly.data.local.dao.OutfitDao
import com.activity.closetly.project_closedly.data.local.dao.UserDao
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GarmentEntity::class,
        OutfitEntity::class
    ],
    version = 3, // Incrementado para incluir OutfitEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun garmentDao(): GarmentDao
    abstract fun outfitDao(): OutfitDao
}