package com.example.deliberate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_cycles")
data class PracticeCycleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val goal: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED
    val reviewNotes: String = "",
    val streakCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
