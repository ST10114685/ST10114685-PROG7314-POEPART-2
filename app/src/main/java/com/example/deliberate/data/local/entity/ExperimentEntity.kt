package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "experiments",
    foreignKeys = [
        ForeignKey(
            entity = LearningModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("moduleId")]
)
data class ExperimentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleId: Long? = null,
    val title: String,
    val hypothesis: String,
    val actionItems: String,
    val metricToTrack: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L,
    val status: String = "ACTIVE", // DRAFT, ACTIVE, COMPLETED, ABANDONED
    val outcomeNotes: String = "",
    val rating: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
