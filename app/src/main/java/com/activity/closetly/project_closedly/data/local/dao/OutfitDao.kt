package com.activity.closetly.project_closedly.data.local.dao

import androidx.room.*
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllOutfitsByUser(userId: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE userId = :userId AND occasion = :occasion ORDER BY createdAt DESC")
    fun getOutfitsByOccasion(userId: String, occasion: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteOutfits(userId: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    suspend fun getOutfitById(outfitId: String): OutfitEntity?

    @Query("SELECT COUNT(*) FROM outfits WHERE userId = :userId")
    fun getOutfitCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("UPDATE outfits SET rating = :rating WHERE id = :outfitId")
    suspend fun updateRating(outfitId: String, rating: Int)

    @Query("UPDATE outfits SET isFavorite = :isFavorite WHERE id = :outfitId")
    suspend fun updateFavoriteStatus(outfitId: String, isFavorite: Boolean)
}