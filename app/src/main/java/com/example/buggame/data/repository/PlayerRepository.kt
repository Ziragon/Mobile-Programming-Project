package com.example.buggame.data.repository

import com.example.buggame.data.local.PlayerDao
import com.example.buggame.data.local.toDomain
import com.example.buggame.data.local.toEntity
import com.example.buggame.data.model.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class PlayerRepository(private val playerDao: PlayerDao) {

    private val _currentPlayer = MutableStateFlow<PlayerProfile?>(null)
    val currentPlayer: StateFlow<PlayerProfile?> = _currentPlayer.asStateFlow()

    val allPlayers: Flow<List<PlayerProfile>> = playerDao.getAllPlayers()
        .map { list -> list.map { it.toDomain() } }

    suspend fun registerAndSelectPlayer(profile: PlayerProfile): PlayerProfile {
        val generatedId = playerDao.insertPlayer(profile.toEntity())
        val savedProfile = profile.copy(id = generatedId)
        _currentPlayer.value = savedProfile
        return savedProfile
    }

    fun selectPlayer(profile: PlayerProfile) {
        _currentPlayer.value = profile
    }

    suspend fun deletePlayer(profile: PlayerProfile) {
        playerDao.deletePlayer(profile.toEntity())
        if (_currentPlayer.value?.id == profile.id) {
            _currentPlayer.value = null
        }
    }
}
