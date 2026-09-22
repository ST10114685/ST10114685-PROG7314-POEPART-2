package com.example.deliberate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deliberate.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>): List<Long>

    @Update
    suspend fun updateLesson(lesson: LessonEntity): Int

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity): Int

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLessonById(id: Long): Int

    @Query("SELECT * FROM lessons WHERE id = :id")
    fun getLessonByIdFlow(id: Long): Flow<LessonEntity?>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): LessonEntity?

    @Query("SELECT * FROM lessons ORDER BY createdAt DESC")
    fun getAllLessonsFlow(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteLessonsFlow(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY createdAt DESC")
    fun getLessonsForModuleFlow(moduleId: Long): Flow<List<LessonEntity>>

    @Query("UPDATE lessons SET isFavorite = :isFavorite WHERE id = :lessonId")
    suspend fun updateFavoriteStatus(lessonId: Long, isFavorite: Boolean)
}
