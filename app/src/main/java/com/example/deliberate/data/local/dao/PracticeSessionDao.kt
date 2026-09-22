package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<PracticeSessionEntity>): List<Long>

    @Update
    suspend fun updateSession(session: PracticeSessionEntity): Int

    @Delete
    suspend fun deleteSession(session: PracticeSessionEntity): Int

    @Query("DELETE FROM practice_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long): Int

    @Query("SELECT * FROM practice_sessions WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<PracticeSessionEntity?>

    @Query("SELECT * FROM practice_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): PracticeSessionEntity?

    @Query("SELECT * FROM practice_sessions ORDER BY startTime DESC")
    fun getAllSessionsFlow(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE moduleId = :moduleId ORDER BY startTime DESC")
    fun getSessionsForModuleFlow(moduleId: Long): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE focusAreaId = :focusAreaId ORDER BY startTime DESC")
    fun getSessionsForFocusAreaFlow(focusAreaId: Long): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE status = 'COMPLETED' ORDER BY startTime DESC")
    fun getCompletedSessionsFlow(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT COUNT(*) FROM practice_sessions WHERE status = 'COMPLETED'")
    fun getCompletedSessionCountFlow(): Flow<Int>

    @Query("SELECT COALESCE(SUM(actualDurationSeconds), 0) FROM practice_sessions WHERE status = 'COMPLETED'")
    fun getTotalPracticeTimeSecondsFlow(): Flow<Long>

    @Query("SELECT * FROM practice_sessions WHERE startTime >= :startTime ORDER BY startTime DESC")
    fun getSessionsSinceFlow(startTime: Long): Flow<List<PracticeSessionEntity>>
}
