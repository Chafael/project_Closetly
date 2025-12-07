package com.activity.closetly.project_closedly.data.local.dao

import androidx.room.*
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GarmentDao {
    @Query("SELECT * FROM garments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllGarmentsByUser(userId: String): Flow<List<GarmentEntity>>

    @Query("SELECT * FROM garments WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getGarmentsByCategory(userId: String, category: String): Flow<List<GarmentEntity>>

    @Query("SELECT * FROM garments WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteGarments(userId: String): Flow<List<GarmentEntity>>

    @Query("SELECT COUNT(*) FROM garments WHERE userId = :userId")
    fun getGarmentCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarment(garment: GarmentEntity)

    @Update
    suspend fun updateGarment(garment: GarmentEntity)

    @Delete
    suspend fun deleteGarment(garment: GarmentEntity)

    @Query("UPDATE garments SET isFavorite = :isFavorite WHERE id = :garmentId")
    suspend fun updateFavoriteStatus(garmentId: String, isFavorite: Boolean)
}