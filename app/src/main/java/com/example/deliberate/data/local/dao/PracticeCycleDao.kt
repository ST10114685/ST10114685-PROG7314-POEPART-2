package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeCycleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: PracticeCycleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycles(cycles: List<PracticeCycleEntity>): List<Long>

    @Update
    suspend fun updateCycle(cycle: PracticeCycleEntity): Int

    @Delete
    suspend fun deleteCycle(cycle: PracticeCycleEntity): Int

    @Query("DELETE FROM practice_cycles WHERE id = :id")
    suspend fun deleteCycleById(id: Long): Int

    @Query("SELECT * FROM practice_cycles WHERE id = :id")
    fun getCycleByIdFlow(id: Long): Flow<PracticeCycleEntity?>

    @Query("SELECT * FROM practice_cycles WHERE id = :id")
    suspend fun getCycleById(id: Long): PracticeCycleEntity?

    @Query("SELECT * FROM practice_cycles ORDER BY startDate DESC")
    fun getAllCyclesFlow(): Flow<List<PracticeCycleEntity>>

    @Query("SELECT * FROM practice_cycles WHERE status = 'ACTIVE' ORDER BY startDate DESC LIMIT 1")
    fun getActiveCycleFlow(): Flow<PracticeCycleEntity?>

    @Query("UPDATE practice_cycles SET streakCount = :streakCount WHERE id = :cycleId")
    suspend fun updateCycleStreak(cycleId: Long, streakCount: Int)
}
