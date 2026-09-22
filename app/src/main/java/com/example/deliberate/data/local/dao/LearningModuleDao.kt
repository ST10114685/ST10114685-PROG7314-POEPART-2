package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.LearningModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningModuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: LearningModuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<LearningModuleEntity>): List<Long>

    @Update
    suspend fun updateModule(module: LearningModuleEntity): Int

    @Delete
    suspend fun deleteModule(module: LearningModuleEntity): Int

    @Query("DELETE FROM learning_modules WHERE id = :id")
    suspend fun deleteModuleById(id: Long): Int

    @Query("SELECT * FROM learning_modules WHERE id = :id")
    fun getModuleByIdFlow(id: Long): Flow<LearningModuleEntity?>

    @Query("SELECT * FROM learning_modules WHERE id = :id")
    suspend fun getModuleById(id: Long): LearningModuleEntity?

    @Query("SELECT * FROM learning_modules WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActiveModulesFlow(): Flow<List<LearningModuleEntity>>

    @Query("SELECT * FROM learning_modules ORDER BY updatedAt DESC")
    fun getAllModulesFlow(): Flow<List<LearningModuleEntity>>

    @Query("SELECT COUNT(*) FROM learning_modules WHERE isArchived = 0")
    fun getActiveModuleCountFlow(): Flow<Int>

    @Query("UPDATE learning_modules SET currentRating = :newRating, updatedAt = :updatedAt WHERE id = :moduleId")
    suspend fun updateModuleRating(moduleId: Long, newRating: Int, updatedAt: Long = System.currentTimeMillis())
}
