package com.example.buggame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.buggame.data.model.ScoreRecord

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerName: String,
    val score: Int,
    val difficultyLevel: Int,
    val difficultyName: String,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

fun ScoreEntity.toDomain(): ScoreRecord = ScoreRecord(
    id = id,
    playerName = playerName,
    score = score,
    difficultyLevel = difficultyLevel,
    difficultyName = difficultyName,
    durationSeconds = durationSeconds,
    timestamp = timestamp
)

fun ScoreRecord.toEntity(): ScoreEntity = ScoreEntity(
    id = id,
    playerName = playerName,
    score = score,
    difficultyLevel = difficultyLevel,
    difficultyName = difficultyName,
    durationSeconds = durationSeconds,
    timestamp = timestamp
)
