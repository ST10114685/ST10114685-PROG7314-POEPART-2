package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reflections",
    foreignKeys = [
        ForeignKey(
            entity = PracticeSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.SET_NULL
        ),
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
    indices = [Index("sessionId"), Index("moduleId"), Index("focusAreaId")]
)
data class ReflectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long? = null,
    val moduleId: Long,
    val focusAreaId: Long? = null,
    val rating: Int = 5, // 1 to 10
    val whatWentWell: String = "",
    val whatToImprove: String = "",
    val keyTakeaway: String = "",
    val customPromptsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
