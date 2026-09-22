package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.PracticeSessionDao
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.Flow

interface PracticeSessionRepository {
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>
    fun getSessionsForModule(moduleId: Long): Flow<List<PracticeSessionEntity>>
    fun getSessionsForFocusArea(focusAreaId: Long): Flow<List<PracticeSessionEntity>>
    fun getCompletedSessions(): Flow<List<PracticeSessionEntity>>
    fun getSessionById(id: Long): Flow<PracticeSessionEntity?>
    suspend fun getSessionByIdDirect(id: Long): PracticeSessionEntity?
    suspend fun insertSession(session: PracticeSessionEntity): Long
    suspend fun updateSession(session: PracticeSessionEntity): Int
    suspend fun deleteSession(session: PracticeSessionEntity): Int
    suspend fun deleteSessionById(id: Long): Int
    fun getCompletedSessionCount(): Flow<Int>
    fun getTotalPracticeTimeSeconds(): Flow<Long>
    fun getSessionsSince(startTime: Long): Flow<List<PracticeSessionEntity>>
}

class PracticeSessionRepositoryImpl(
    private val dao: PracticeSessionDao
) : PracticeSessionRepository {

    override fun getAllSessions(): Flow<List<PracticeSessionEntity>> = dao.getAllSessionsFlow()

    override fun getSessionsForModule(moduleId: Long): Flow<List<PracticeSessionEntity>> =
        dao.getSessionsForModuleFlow(moduleId)

    override fun getSessionsForFocusArea(focusAreaId: Long): Flow<List<PracticeSessionEntity>> =
        dao.getSessionsForFocusAreaFlow(focusAreaId)

    override fun getCompletedSessions(): Flow<List<PracticeSessionEntity>> =
        dao.getCompletedSessionsFlow()

    override fun getSessionById(id: Long): Flow<PracticeSessionEntity?> = dao.getSessionByIdFlow(id)

    override suspend fun getSessionByIdDirect(id: Long): PracticeSessionEntity? = dao.getSessionById(id)

    override suspend fun insertSession(session: PracticeSessionEntity): Long = dao.insertSession(session)

    override suspend fun updateSession(session: PracticeSessionEntity): Int = dao.updateSession(session)

    override suspend fun deleteSession(session: PracticeSessionEntity): Int = dao.deleteSession(session)

    override suspend fun deleteSessionById(id: Long): Int = dao.deleteSessionById(id)

    override fun getCompletedSessionCount(): Flow<Int> = dao.getCompletedSessionCountFlow()

    override fun getTotalPracticeTimeSeconds(): Flow<Long> = dao.getTotalPracticeTimeSecondsFlow()

    override fun getSessionsSince(startTime: Long): Flow<List<PracticeSessionEntity>> =
        dao.getSessionsSinceFlow(startTime)
}
