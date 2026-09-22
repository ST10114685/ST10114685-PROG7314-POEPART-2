package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.ReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: ReflectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflections(reflections: List<ReflectionEntity>): List<Long>

    @Update
    suspend fun updateReflection(reflection: ReflectionEntity): Int

    @Delete
    suspend fun deleteReflection(reflection: ReflectionEntity): Int

    @Query("DELETE FROM reflections WHERE id = :id")
    suspend fun deleteReflectionById(id: Long): Int

    @Query("SELECT * FROM reflections WHERE id = :id")
    fun getReflectionByIdFlow(id: Long): Flow<ReflectionEntity?>

    @Query("SELECT * FROM reflections WHERE id = :id")
    suspend fun getReflectionById(id: Long): ReflectionEntity?

    @Query("SELECT * FROM reflections WHERE sessionId = :sessionId LIMIT 1")
    fun getReflectionForSessionFlow(sessionId: Long): Flow<ReflectionEntity?>

    @Query("SELECT * FROM reflections WHERE moduleId = :moduleId ORDER BY createdAt DESC")
    fun getReflectionsForModuleFlow(moduleId: Long): Flow<List<ReflectionEntity>>

    @Query("SELECT * FROM reflections ORDER BY createdAt DESC")
    fun getAllReflectionsFlow(): Flow<List<ReflectionEntity>>

    @Query("SELECT COUNT(*) FROM reflections")
    fun getReflectionCountFlow(): Flow<Int>

    @Query("SELECT AVG(rating) FROM reflections")
    fun getAverageRatingFlow(): Flow<Float?>

    @Query("SELECT * FROM reflections ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentReflectionsFlow(limit: Int): Flow<List<ReflectionEntity>>
}
