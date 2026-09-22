package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_sessions",
    foreignKeys = [
        ForeignKey(
            entity = LearningModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FocusAreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["focusAreaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("moduleId"), Index("focusAreaId")]
)
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleId: Long,
    val focusAreaId: Long? = null,
    val title: String,
    val objective: String,
    val successCriteria: String,
    val targetDurationMinutes: Int = 30,
    val actualDurationSeconds: Long = 0,
    val status: String = "COMPLETED", // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = System.currentTimeMillis(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
