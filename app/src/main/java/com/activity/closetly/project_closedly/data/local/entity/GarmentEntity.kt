package com.activity.closetly.project_closedly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garments")
data class GarmentEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val category: String,
    val subcategory: String?,
    val color: String?,
    val brand: String?,
    val season: String?,
    val imageUrl: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)