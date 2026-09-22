package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "focus_areas",
    foreignKeys = [
        ForeignKey(
            entity = LearningModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("moduleId")]
)
data class FocusAreaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleId: Long,
    val title: String,
    val description: String,
    val baselineRating: Int = 1,
    val targetRating: Int = 10,
    val currentRating: Int = 1,
    val status: String = "ACTIVE", // ACTIVE, MASTERED, PAUSED
    val createdAt: Long = System.currentTimeMillis()
)
