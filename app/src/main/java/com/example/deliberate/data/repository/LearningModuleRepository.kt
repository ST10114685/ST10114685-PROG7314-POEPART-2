package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.LearningModuleDao
import com.example.deliberate.data.local.entity.LearningModuleEntity
import kotlinx.coroutines.flow.Flow

interface LearningModuleRepository {
    fun getAllModules(): Flow<List<LearningModuleEntity>>
    fun getActiveModules(): Flow<List<LearningModuleEntity>>
    fun getModuleById(id: Long): Flow<LearningModuleEntity?>
    suspend fun getModuleByIdDirect(id: Long): LearningModuleEntity?
    suspend fun insertModule(module: LearningModuleEntity): Long
    suspend fun updateModule(module: LearningModuleEntity): Int
    suspend fun deleteModule(module: LearningModuleEntity): Int
    suspend fun deleteModuleById(id: Long): Int
    suspend fun updateModuleRating(moduleId: Long, rating: Int)
    fun getActiveModuleCount(): Flow<Int>
}

class LearningModuleRepositoryImpl(
    private val dao: LearningModuleDao
) : LearningModuleRepository {

    override fun getAllModules(): Flow<List<LearningModuleEntity>> = dao.getAllModulesFlow()

    override fun getActiveModules(): Flow<List<LearningModuleEntity>> = dao.getAllActiveModulesFlow()

    override fun getModuleById(id: Long): Flow<LearningModuleEntity?> = dao.getModuleByIdFlow(id)

    override suspend fun getModuleByIdDirect(id: Long): LearningModuleEntity? = dao.getModuleById(id)

    override suspend fun insertModule(module: LearningModuleEntity): Long = dao.insertModule(module)

    override suspend fun updateModule(module: LearningModuleEntity): Int = dao.updateModule(module)

    override suspend fun deleteModule(module: LearningModuleEntity): Int = dao.deleteModule(module)

    override suspend fun deleteModuleById(id: Long): Int = dao.deleteModuleById(id)

    override suspend fun updateModuleRating(moduleId: Long, rating: Int) = dao.updateModuleRating(moduleId, rating)

    override fun getActiveModuleCount(): Flow<Int> = dao.getActiveModuleCountFlow()
}
