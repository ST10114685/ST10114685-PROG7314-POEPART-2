package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = LearningModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ReflectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["reflectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("moduleId"), Index("reflectionId")]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleId: Long? = null,
    val focusAreaId: Long? = null,
    val reflectionId: Long? = null,
    val title: String,
    val lessonText: String,
    val category: String = "General",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
