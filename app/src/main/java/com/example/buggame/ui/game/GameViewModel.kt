package com.example.buggame.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.data.model.GameSettings
import com.example.buggame.data.model.InsectSpawn
import com.example.buggame.data.model.InsectType
import com.example.buggame.data.model.TiltBonusSpawn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameViewModel : ViewModel() {

    private val _tiltBonusActivatedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val tiltBonusActivatedEvent: SharedFlow<Unit> = _tiltBonusActivatedEvent.asSharedFlow()
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var spawnJob: Job? = null
    private var nextInsectId = 0L

    private var settings: GameSettings = GameSettings()

    private companion object {
        const val MISS_PENALTY = 3
        const val TILT_BONUS_SPAWN_INTERVAL_SECONDS = 15L
        const val TILT_BONUS_LIFETIME_SECONDS = 6L
        const val TILT_MODE_DURATION_SECONDS = 10L
    }

    private var bonusSpawnJob: Job? = null

    private fun startBonusSpawning() {
        bonusSpawnJob?.cancel()
        bonusSpawnJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay((settings.bonusIntervalSeconds * 1000L).milliseconds)

                val current = _uiState.value
                if (current.isRunning && current.insects.size < settings.maxBugsOnScreen) {
                    val bonus = InsectSpawn(
                        id = ++nextInsectId,
                        type = InsectType.entries.random(),
                        isBonus = true
                    )
                    _uiState.value = current.copy(insects = current.insects + bonus)
                }
            }
        }
    }

    private var tiltBonusSpawnJob: Job? = null
    private var tiltBonusLifetimeJob: Job? = null
    private var tiltModeJob: Job? = null
    private var nextTiltBonusId = 0L

    private fun scheduleNextTiltBonusSpawn() {
        tiltBonusSpawnJob?.cancel()
        tiltBonusSpawnJob = viewModelScope.launch {
            delay((TILT_BONUS_SPAWN_INTERVAL_SECONDS * 1000L).milliseconds)

            val current = _uiState.value
            if (current.isRunning) {
                spawnTiltBonus()
            }
        }
    }

    private fun spawnTiltBonus() {
        val bonus = TiltBonusSpawn(
            id = ++nextTiltBonusId,
            relativeX = Random.nextFloat(),
            relativeY = Random.nextFloat()
        )
        _uiState.value = _uiState.value.copy(tiltBonus = bonus)

        tiltBonusLifetimeJob?.cancel()
        tiltBonusLifetimeJob = viewModelScope.launch {
            delay((TILT_BONUS_LIFETIME_SECONDS * 1000L).milliseconds)

            val current = _uiState.value
            if (current.tiltBonus?.id == bonus.id) {
                _uiState.value = current.copy(tiltBonus = null)
                scheduleNextTiltBonusSpawn()
            }
        }
    }

    private fun restartTiltBonusLifetimeTimer(bonusId: Long) {
        tiltBonusLifetimeJob?.cancel()
        tiltBonusLifetimeJob = viewModelScope.launch {
            delay((TILT_BONUS_LIFETIME_SECONDS * 1000L).milliseconds)
            val current = _uiState.value
            if (current.tiltBonus?.id == bonusId) {
                _uiState.value = current.copy(tiltBonus = null)
                scheduleNextTiltBonusSpawn()
            }
        }
    }

    fun onTiltBonusTapped() {
        val current = _uiState.value
        if (!current.isRunning || current.tiltBonus == null) return

        tiltBonusLifetimeJob?.cancel()
        _uiState.value = current.copy(
            tiltBonus = null,
            isTiltModeActive = true
        )
        restartTiltModeTimer()
        _tiltBonusActivatedEvent.tryEmit(Unit)
    }
    private fun restartTiltModeTimer() {
        tiltModeJob?.cancel()
        tiltModeJob = viewModelScope.launch {
            delay((TILT_MODE_DURATION_SECONDS * 1000L).milliseconds)
            _uiState.value = _uiState.value.copy(isTiltModeActive = false)
            scheduleNextTiltBonusSpawn()
        }
    }

    fun updateSettings(newSettings: GameSettings) {
        settings = newSettings
        val current = _uiState.value
        if (!current.isRunning && !current.isMidRound) {
            _uiState.value = current.copy(timeLeftSeconds = newSettings.roundDurationSeconds)
        }
    }

    fun onStartClicked() {
        val current = _uiState.value
        if (current.isRunning) return

        _uiState.value = if (current.isMidRound) {
            current.copy(isRunning = true)
        } else {
            GameUiState(
                isRunning = true,
                isMidRound = true,
                timeLeftSeconds = settings.roundDurationSeconds
            )
        }
        startTimer()
        startSpawning()
        startBonusSpawning()

        val resumed = _uiState.value
        when {
            resumed.isTiltModeActive -> restartTiltModeTimer()
            resumed.tiltBonus != null -> restartTiltBonusLifetimeTimer(resumed.tiltBonus.id)
            else -> scheduleNextTiltBonusSpawn()
        }
    }

    fun onPauseClicked() {
        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun onResetClicked() {
        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
        _uiState.value = GameUiState(timeLeftSeconds = settings.roundDurationSeconds)
    }

    fun onRoundOverDialogDismissed() {
        _uiState.value = _uiState.value.copy(
            isRoundOver = false,
            timeLeftSeconds = settings.roundDurationSeconds
        )
    }

    fun onInsectTapped(insectId: Long) {
        val current = _uiState.value
        if (!current.isRunning) return
        val insect = current.insects.find { it.id == insectId } ?: return

        _uiState.value = current.copy(
            insects = current.insects.filterNot { it.id == insectId },
            score = (current.score + insect.points).coerceAtLeast(0)
        )
    }

    fun onMissTapped() {
        val current = _uiState.value
        if (!current.isRunning) return

        _uiState.value = current.copy(
            score = (current.score - MISS_PENALTY).coerceAtLeast(0)
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.timeLeftSeconds > 0) {
                delay(1000.milliseconds)
                val current = _uiState.value
                if (!current.isRunning) break

                val newTime = (current.timeLeftSeconds - 1).coerceAtLeast(0)
                val roundOver = newTime <= 0
                _uiState.value = current.copy(
                    timeLeftSeconds = newTime,
                    isRunning = !roundOver,
                    isRoundOver = roundOver,
                    isMidRound = !roundOver,
                    insects = if (roundOver) emptyList() else current.insects,
                    tiltBonus = if (roundOver) null else current.tiltBonus,
                    isTiltModeActive = !roundOver && current.isTiltModeActive
                )

                if (roundOver) {
                    spawnJob?.cancel()
                    bonusSpawnJob?.cancel()
                    tiltBonusSpawnJob?.cancel()
                    tiltBonusLifetimeJob?.cancel()
                    tiltModeJob?.cancel()
                }
            }
        }
    }

    private fun startSpawning() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(settings.insectSpawnIntervalMillis.milliseconds)

                val current = _uiState.value
                if (current.isRunning && current.insects.size < settings.maxBugsOnScreen) {
                    val newInsect = InsectSpawn(
                        id = ++nextInsectId,
                        type = InsectType.entries.random()
                    )
                    _uiState.value = current.copy(insects = current.insects + newInsect)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
    }
}