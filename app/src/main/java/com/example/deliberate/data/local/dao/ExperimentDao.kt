package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.ExperimentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiment(experiment: ExperimentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiments(experiments: List<ExperimentEntity>): List<Long>

    @Update
    suspend fun updateExperiment(experiment: ExperimentEntity): Int

    @Delete
    suspend fun deleteExperiment(experiment: ExperimentEntity): Int

    @Query("DELETE FROM experiments WHERE id = :id")
    suspend fun deleteExperimentById(id: Long): Int

    @Query("SELECT * FROM experiments WHERE id = :id")
    fun getExperimentByIdFlow(id: Long): Flow<ExperimentEntity?>

    @Query("SELECT * FROM experiments WHERE id = :id")
    suspend fun getExperimentById(id: Long): ExperimentEntity?

    @Query("SELECT * FROM experiments ORDER BY createdAt DESC")
    fun getAllExperimentsFlow(): Flow<List<ExperimentEntity>>

    @Query("SELECT * FROM experiments WHERE moduleId = :moduleId ORDER BY createdAt DESC")
    fun getExperimentsForModuleFlow(moduleId: Long): Flow<List<ExperimentEntity>>

    @Query("SELECT * FROM experiments WHERE status = :status ORDER BY createdAt DESC")
    fun getExperimentsByStatusFlow(status: String): Flow<List<ExperimentEntity>>
}
