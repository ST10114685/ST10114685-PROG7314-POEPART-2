package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.ExperimentDao
import com.example.deliberate.data.local.dao.PracticeCycleDao
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import kotlinx.coroutines.flow.Flow

interface ExperimentRepository {
    fun getAllExperiments(): Flow<List<ExperimentEntity>>
    fun getExperimentsForModule(moduleId: Long): Flow<List<ExperimentEntity>>
    fun getExperimentsByStatus(status: String): Flow<List<ExperimentEntity>>
    fun getExperimentById(id: Long): Flow<ExperimentEntity?>
    suspend fun getExperimentByIdDirect(id: Long): ExperimentEntity?
    suspend fun insertExperiment(experiment: ExperimentEntity): Long
    suspend fun updateExperiment(experiment: ExperimentEntity): Int
    suspend fun deleteExperiment(experiment: ExperimentEntity): Int
    suspend fun deleteExperimentById(id: Long): Int
}

class ExperimentRepositoryImpl(
    private val dao: ExperimentDao
) : ExperimentRepository {

    override fun getAllExperiments(): Flow<List<ExperimentEntity>> = dao.getAllExperimentsFlow()

    override fun getExperimentsForModule(moduleId: Long): Flow<List<ExperimentEntity>> =
        dao.getExperimentsForModuleFlow(moduleId)

    override fun getExperimentsByStatus(status: String): Flow<List<ExperimentEntity>> =
        dao.getExperimentsByStatusFlow(status)

    override fun getExperimentById(id: Long): Flow<ExperimentEntity?> = dao.getExperimentByIdFlow(id)

    override suspend fun getExperimentByIdDirect(id: Long): ExperimentEntity? = dao.getExperimentById(id)

    override suspend fun insertExperiment(experiment: ExperimentEntity): Long = dao.insertExperiment(experiment)

    override suspend fun updateExperiment(experiment: ExperimentEntity): Int = dao.updateExperiment(experiment)

    override suspend fun deleteExperiment(experiment: ExperimentEntity): Int = dao.deleteExperiment(experiment)

    override suspend fun deleteExperimentById(id: Long): Int = dao.deleteExperimentById(id)
}

interface PracticeCycleRepository {
    fun getAllCycles(): Flow<List<PracticeCycleEntity>>
    fun getActiveCycle(): Flow<PracticeCycleEntity?>
    fun getCycleById(id: Long): Flow<PracticeCycleEntity?>
    suspend fun getCycleByIdDirect(id: Long): PracticeCycleEntity?
    suspend fun insertCycle(cycle: PracticeCycleEntity): Long
    suspend fun updateCycle(cycle: PracticeCycleEntity): Int
    suspend fun deleteCycle(cycle: PracticeCycleEntity): Int
    suspend fun deleteCycleById(id: Long): Int
    suspend fun updateCycleStreak(cycleId: Long, streakCount: Int)
}

class PracticeCycleRepositoryImpl(
    private val dao: PracticeCycleDao
) : PracticeCycleRepository {

    override fun getAllCycles(): Flow<List<PracticeCycleEntity>> = dao.getAllCyclesFlow()

    override fun getActiveCycle(): Flow<PracticeCycleEntity?> = dao.getActiveCycleFlow()

    override fun getCycleById(id: Long): Flow<PracticeCycleEntity?> = dao.getCycleByIdFlow(id)

    override suspend fun getCycleByIdDirect(id: Long): PracticeCycleEntity? = dao.getCycleById(id)

    override suspend fun insertCycle(cycle: PracticeCycleEntity): Long = dao.insertCycle(cycle)

    override suspend fun updateCycle(cycle: PracticeCycleEntity): Int = dao.updateCycle(cycle)

    override suspend fun deleteCycle(cycle: PracticeCycleEntity): Int = dao.deleteCycle(cycle)

    override suspend fun deleteCycleById(id: Long): Int = dao.deleteCycleById(id)

    override suspend fun updateCycleStreak(cycleId: Long, streakCount: Int) =
        dao.updateCycleStreak(cycleId, streakCount)
}
