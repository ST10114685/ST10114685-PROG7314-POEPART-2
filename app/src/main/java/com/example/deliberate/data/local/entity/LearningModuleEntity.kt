package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_modules")
data class LearningModuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val iconName: String = "code",
    val colorHex: String = "#6750A4",
    val baselineRating: Int = 1,
    val targetRating: Int = 10,
    val currentRating: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
