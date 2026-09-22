package com.example.deliberate.data.repository

import com.example.deliberate.data.local.dao.LessonDao
import com.example.deliberate.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getAllLessons(): Flow<List<LessonEntity>>
    fun getFavoriteLessons(): Flow<List<LessonEntity>>
    fun getLessonsForModule(moduleId: Long): Flow<List<LessonEntity>>
    fun getLessonById(id: Long): Flow<LessonEntity?>
    suspend fun getLessonByIdDirect(id: Long): LessonEntity?
    suspend fun insertLesson(lesson: LessonEntity): Long
    suspend fun updateLesson(lesson: LessonEntity): Int
    suspend fun deleteLesson(lesson: LessonEntity): Int
    suspend fun deleteLessonById(id: Long): Int
    suspend fun updateFavoriteStatus(lessonId: Long, isFavorite: Boolean)
}

class LessonRepositoryImpl(
    private val dao: LessonDao
) : LessonRepository {

    override fun getAllLessons(): Flow<List<LessonEntity>> = dao.getAllLessonsFlow()

    override fun getFavoriteLessons(): Flow<List<LessonEntity>> = dao.getFavoriteLessonsFlow()

    override fun getLessonsForModule(moduleId: Long): Flow<List<LessonEntity>> =
        dao.getLessonsForModuleFlow(moduleId)

    override fun getLessonById(id: Long): Flow<LessonEntity?> = dao.getLessonByIdFlow(id)

    override suspend fun getLessonByIdDirect(id: Long): LessonEntity? = dao.getLessonById(id)

    override suspend fun insertLesson(lesson: LessonEntity): Long = dao.insertLesson(lesson)

    override suspend fun updateLesson(lesson: LessonEntity): Int = dao.updateLesson(lesson)

    override suspend fun deleteLesson(lesson: LessonEntity): Int = dao.deleteLesson(lesson)

    override suspend fun deleteLessonById(id: Long): Int = dao.deleteLessonById(id)

    override suspend fun updateFavoriteStatus(lessonId: Long, isFavorite: Boolean) =
        dao.updateFavoriteStatus(lessonId, isFavorite)
}
