package com.example.buggame.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    @Query("SELECT * FROM scores ORDER BY score DESC, timestamp DESC")
    fun getAllScores(): Flow<List<ScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity): Long

    @Query("DELETE FROM scores")
    suspend fun clearAll()
}
