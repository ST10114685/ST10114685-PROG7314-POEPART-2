package com.example.deliberate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.deliberate.data.local.converters.Converters
import com.example.deliberate.data.local.dao.ExperimentDao
import com.example.deliberate.data.local.dao.FocusAreaDao
import com.example.deliberate.data.local.dao.LearningModuleDao
import com.example.deliberate.data.local.dao.LessonDao
import com.example.deliberate.data.local.dao.PracticeCycleDao
import com.example.deliberate.data.local.dao.PracticeSessionDao
import com.example.deliberate.data.local.dao.ReflectionDao
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.data.local.entity.LessonEntity
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.data.local.entity.ReflectionEntity

@Database(
    entities = [
        LearningModuleEntity::class,
        FocusAreaEntity::class,
        PracticeSessionEntity::class,
        ReflectionEntity::class,
        LessonEntity::class,
        ExperimentEntity::class,
        PracticeCycleEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DeliberateDatabase : RoomDatabase() {

    abstract fun learningModuleDao(): LearningModuleDao
    abstract fun focusAreaDao(): FocusAreaDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun reflectionDao(): ReflectionDao
    abstract fun lessonDao(): LessonDao
    abstract fun experimentDao(): ExperimentDao
    abstract fun practiceCycleDao(): PracticeCycleDao

    companion object {
        @Volatile
        private var INSTANCE: DeliberateDatabase? = null

        fun getInstance(context: Context): DeliberateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeliberateDatabase::class.java,
                    "deliberate_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
