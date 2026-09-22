package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.FocusAreaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusAreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusArea(focusArea: FocusAreaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusAreas(focusAreas: List<FocusAreaEntity>): List<Long>

    @Update
    suspend fun updateFocusArea(focusArea: FocusAreaEntity): Int

    @Delete
    suspend fun deleteFocusArea(focusArea: FocusAreaEntity): Int

    @Query("DELETE FROM focus_areas WHERE id = :id")
    suspend fun deleteFocusAreaById(id: Long): Int

    @Query("SELECT * FROM focus_areas WHERE id = :id")
    fun getFocusAreaByIdFlow(id: Long): Flow<FocusAreaEntity?>

    @Query("SELECT * FROM focus_areas WHERE id = :id")
    suspend fun getFocusAreaById(id: Long): FocusAreaEntity?

    @Query("SELECT * FROM focus_areas WHERE moduleId = :moduleId ORDER BY createdAt DESC")
    fun getFocusAreasForModuleFlow(moduleId: Long): Flow<List<FocusAreaEntity>>

    @Query("SELECT * FROM focus_areas ORDER BY createdAt DESC")
    fun getAllFocusAreasFlow(): Flow<List<FocusAreaEntity>>

    @Query("UPDATE focus_areas SET currentRating = :newRating WHERE id = :focusAreaId")
    suspend fun updateFocusAreaRating(focusAreaId: Long, newRating: Int)
}
