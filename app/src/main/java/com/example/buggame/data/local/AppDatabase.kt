package com.example.buggame.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerEntity::class, ScoreEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun scoreDao(): ScoreDao
}