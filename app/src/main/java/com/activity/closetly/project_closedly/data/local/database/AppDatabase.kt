package com.activity.closetly.project_closedly.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.activity.closetly.project_closedly.data.local.dao.GarmentDao
import com.activity.closetly.project_closedly.data.local.dao.UserDao
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GarmentEntity::class
    ],
    version = 2, // Incrementado de 1 a 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun garmentDao(): GarmentDao
}