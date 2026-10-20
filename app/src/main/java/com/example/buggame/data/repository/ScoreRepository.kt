package com.example.buggame.data.repository

import com.example.buggame.data.local.ScoreDao
import com.example.buggame.data.local.toDomain
import com.example.buggame.data.local.toEntity
import com.example.buggame.data.model.ScoreRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScoreRepository(private val scoreDao: ScoreDao) {

    val allScores: Flow<List<ScoreRecord>> = scoreDao.getAllScores()
        .map { list -> list.map { it.toDomain() } }

    suspend fun saveScore(record: ScoreRecord): Long {
        return scoreDao.insertScore(record.toEntity())
    }

    suspend fun clearScores() {
        scoreDao.clearAll()
    }
}
