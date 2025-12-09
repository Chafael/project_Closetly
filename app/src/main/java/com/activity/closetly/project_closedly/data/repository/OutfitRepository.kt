package com.activity.closetly.project_closedly.data.repository

import com.activity.closetly.project_closedly.data.local.dao.OutfitDao
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutfitRepository @Inject constructor(
    private val outfitDao: OutfitDao
) {
    fun getAllOutfitsByUser(userId: String): Flow<List<OutfitEntity>> {
        return outfitDao.getAllOutfitsByUser(userId)
    }

    fun getOutfitsByOccasion(userId: String, occasion: String): Flow<List<OutfitEntity>> {
        return outfitDao.getOutfitsByOccasion(userId, occasion)
    }

    fun getFavoriteOutfits(userId: String): Flow<List<OutfitEntity>> {
        return outfitDao.getFavoriteOutfits(userId)
    }

    suspend fun getOutfitById(outfitId: String): OutfitEntity? {
        return outfitDao.getOutfitById(outfitId)
    }

    fun getOutfitCount(userId: String): Flow<Int> {
        return outfitDao.getOutfitCount(userId)
    }

    suspend fun insertOutfit(outfit: OutfitEntity) {
        outfitDao.insertOutfit(outfit)
    }

    suspend fun updateOutfit(outfit: OutfitEntity) {
        outfitDao.updateOutfit(outfit)
    }

    suspend fun deleteOutfit(outfit: OutfitEntity) {
        outfitDao.deleteOutfit(outfit)
    }

    suspend fun updateRating(outfitId: String, rating: Int) {
        outfitDao.updateRating(outfitId, rating)
    }

    suspend fun updateFavoriteStatus(outfitId: String, isFavorite: Boolean) {
        outfitDao.updateFavoriteStatus(outfitId, isFavorite)
    }
}