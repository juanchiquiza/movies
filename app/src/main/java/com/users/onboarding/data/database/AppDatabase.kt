package com.users.onboarding.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.users.onboarding.data.model.MovieEntity
import com.users.onboarding.data.movieDao.MovieDao

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}