package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.FocusAreaDao
import com.example.deliberate.data.local.entity.FocusAreaEntity
import kotlinx.coroutines.flow.Flow

interface FocusAreaRepository {
    fun getAllFocusAreas(): Flow<List<FocusAreaEntity>>
    fun getFocusAreasForModule(moduleId: Long): Flow<List<FocusAreaEntity>>
    fun getFocusAreaById(id: Long): Flow<FocusAreaEntity?>
    suspend fun getFocusAreaByIdDirect(id: Long): FocusAreaEntity?
    suspend fun insertFocusArea(focusArea: FocusAreaEntity): Long
    suspend fun updateFocusArea(focusArea: FocusAreaEntity): Int
    suspend fun deleteFocusArea(focusArea: FocusAreaEntity): Int
    suspend fun deleteFocusAreaById(id: Long): Int
    suspend fun updateFocusAreaRating(focusAreaId: Long, rating: Int)
}

class FocusAreaRepositoryImpl(
    private val dao: FocusAreaDao
) : FocusAreaRepository {

    override fun getAllFocusAreas(): Flow<List<FocusAreaEntity>> = dao.getAllFocusAreasFlow()

    override fun getFocusAreasForModule(moduleId: Long): Flow<List<FocusAreaEntity>> =
        dao.getFocusAreasForModuleFlow(moduleId)

    override fun getFocusAreaById(id: Long): Flow<FocusAreaEntity?> = dao.getFocusAreaByIdFlow(id)

    override suspend fun getFocusAreaByIdDirect(id: Long): FocusAreaEntity? = dao.getFocusAreaById(id)

    override suspend fun insertFocusArea(focusArea: FocusAreaEntity): Long = dao.insertFocusArea(focusArea)

    override suspend fun updateFocusArea(focusArea: FocusAreaEntity): Int = dao.updateFocusArea(focusArea)

    override suspend fun deleteFocusArea(focusArea: FocusAreaEntity): Int = dao.deleteFocusArea(focusArea)

    override suspend fun deleteFocusAreaById(id: Long): Int = dao.deleteFocusAreaById(id)

    override suspend fun updateFocusAreaRating(focusAreaId: Long, rating: Int) =
        dao.updateFocusAreaRating(focusAreaId, rating)
}
