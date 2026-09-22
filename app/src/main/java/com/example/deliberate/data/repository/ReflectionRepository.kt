package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.ReflectionDao
import com.example.deliberate.data.local.entity.ReflectionEntity
import kotlinx.coroutines.flow.Flow

interface ReflectionRepository {
    fun getAllReflections(): Flow<List<ReflectionEntity>>
    fun getReflectionsForModule(moduleId: Long): Flow<List<ReflectionEntity>>
    fun getReflectionForSession(sessionId: Long): Flow<ReflectionEntity?>
    fun getReflectionById(id: Long): Flow<ReflectionEntity?>
    suspend fun getReflectionByIdDirect(id: Long): ReflectionEntity?
    suspend fun insertReflection(reflection: ReflectionEntity): Long
    suspend fun updateReflection(reflection: ReflectionEntity): Int
    suspend fun deleteReflection(reflection: ReflectionEntity): Int
    suspend fun deleteReflectionById(id: Long): Int
    fun getReflectionCount(): Flow<Int>
    fun getAverageRating(): Flow<Float?>
    fun getRecentReflections(limit: Int = 5): Flow<List<ReflectionEntity>>
}

class ReflectionRepositoryImpl(
    private val dao: ReflectionDao
) : ReflectionRepository {

    override fun getAllReflections(): Flow<List<ReflectionEntity>> = dao.getAllReflectionsFlow()

    override fun getReflectionsForModule(moduleId: Long): Flow<List<ReflectionEntity>> =
        dao.getReflectionsForModuleFlow(moduleId)

    override fun getReflectionForSession(sessionId: Long): Flow<ReflectionEntity?> =
        dao.getReflectionForSessionFlow(sessionId)

    override fun getReflectionById(id: Long): Flow<ReflectionEntity?> = dao.getReflectionByIdFlow(id)

    override suspend fun getReflectionByIdDirect(id: Long): ReflectionEntity? = dao.getReflectionById(id)

    override suspend fun insertReflection(reflection: ReflectionEntity): Long = dao.insertReflection(reflection)

    override suspend fun updateReflection(reflection: ReflectionEntity): Int = dao.updateReflection(reflection)

    override suspend fun deleteReflection(reflection: ReflectionEntity): Int = dao.deleteReflection(reflection)

    override suspend fun deleteReflectionById(id: Long): Int = dao.deleteReflectionById(id)

    override fun getReflectionCount(): Flow<Int> = dao.getReflectionCountFlow()

    override fun getAverageRating(): Flow<Float?> = dao.getAverageRatingFlow()

    override fun getRecentReflections(limit: Int): Flow<List<ReflectionEntity>> = dao.getRecentReflectionsFlow(limit)
}
