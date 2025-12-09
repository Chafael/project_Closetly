package com.activity.closetly.project_closedly.data.repository

import com.activity.closetly.project_closedly.data.local.dao.GarmentDao
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarmentRepository @Inject constructor(
    private val garmentDao: GarmentDao
) {
    fun getAllGarmentsByUser(userId: String): Flow<List<GarmentEntity>> {
        return garmentDao.getAllGarmentsByUser(userId)
    }

    fun getGarmentsByCategory(userId: String, category: String): Flow<List<GarmentEntity>> {
        return garmentDao.getGarmentsByCategory(userId, category)
    }

    fun getFavoriteGarments(userId: String): Flow<List<GarmentEntity>> {
        return garmentDao.getFavoriteGarments(userId)
    }

    fun getGarmentCount(userId: String): Flow<Int> {
        return garmentDao.getGarmentCount(userId)
    }

    suspend fun getGarmentById(garmentId: String): GarmentEntity? {
        return garmentDao.getGarmentById(garmentId)
    }

    suspend fun insertGarment(garment: GarmentEntity) {
        garmentDao.insertGarment(garment)
    }

    suspend fun updateGarment(garment: GarmentEntity) {
        garmentDao.updateGarment(garment)
    }

    suspend fun deleteGarment(garment: GarmentEntity) {
        garmentDao.deleteGarment(garment)
    }

    suspend fun updateFavoriteStatus(garmentId: String, isFavorite: Boolean) {
        garmentDao.updateFavoriteStatus(garmentId, isFavorite)
    }
}