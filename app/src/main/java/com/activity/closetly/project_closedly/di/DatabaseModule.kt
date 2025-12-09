package com.activity.closetly.project_closedly.di

import android.content.Context
import androidx.room.Room
import com.activity.closetly.project_closedly.data.local.AppDatabase
import com.activity.closetly.project_closedly.data.local.MIGRATION_3_4
import com.activity.closetly.project_closedly.data.local.dao.GarmentDao
import com.activity.closetly.project_closedly.data.local.dao.OutfitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "closetly_database"
        )
            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration() // Borra y recrea DB si hay errores
            .build()
    }
    @Provides
    fun provideGarmentDao(database: AppDatabase): GarmentDao {
        return database.garmentDao()
    }

    @Provides
    fun provideOutfitDao(database: AppDatabase): OutfitDao {
        return database.outfitDao()
    }
}