package com.example.buggame.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.adaptive.AdaptiveController
import com.example.buggame.adaptive.AdaptiveParams
import com.example.buggame.adaptive.DdaConfig
import com.example.buggame.data.model.BonusInsect
import com.example.buggame.data.model.GameSettings
import com.example.buggame.data.model.InsectSpawn
import com.example.buggame.data.model.InsectType
import com.example.buggame.data.model.ScoreRecord
import com.example.buggame.data.model.TiltBonusSpawn
import com.example.buggame.data.model.getDifficultyName
import com.example.buggame.data.repository.GoldRateRepository
import com.example.buggame.data.repository.PlayerRepository
import com.example.buggame.data.repository.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(
    private val playerRepository: PlayerRepository,
    private val scoreRepository: ScoreRepository,
    private val goldRateRepository: GoldRateRepository
) : ViewModel() {

    private companion object {
        const val TILT_BONUS_SPAWN_INTERVAL_SECONDS = 15L
        const val TILT_BONUS_LIFETIME_SECONDS = 6L
        const val TILT_MODE_DURATION_SECONDS = 10L

        const val GOLD_BUG_SPAWN_INTERVAL_SECONDS = 20L
    }

    private val _tiltBonusActivatedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val tiltBonusActivatedEvent: SharedFlow<Unit> = _tiltBonusActivatedEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val adaptive = AdaptiveController()
    private val _adaptiveParams = MutableStateFlow(adaptive.params)
    val adaptiveParams: StateFlow<AdaptiveParams> = _adaptiveParams.asStateFlow()

    private val spawnTimes = HashMap<Long, Long>()
    private var pausedAt = 0L
    private var adaptationJob: Job? = null

    private fun nowMs(): Long = System.nanoTime() / 1_000_000L

    private var timerJob: Job? = null
    private var spawnJob: Job? = null
    private var bonusSpawnJob: Job? = null
    private var nextInsectId = 0L
    private var settings: GameSettings = GameSettings()

    private var tiltBonusSpawnJob: Job? = null
    private var tiltBonusLifetimeJob: Job? = null
    private var tiltModeJob: Job? = null
    private var nextTiltBonusId = 0L

    init {
        playerRepository.currentPlayer
            .onEach { player ->
                _uiState.update { it.copy(activePlayerName = player?.fullName?.ifBlank { "Игрок" } ?: "Игрок") }
            }
            .launchIn(viewModelScope)

        playerRepository.currentPlayer
            .map { it?.fullName }
            .distinctUntilChanged()
            .drop(1)
            .onEach { onResetAdaptationClicked() }
            .launchIn(viewModelScope)

        viewModelScope.launch { goldRateRepository.refresh() }
    }

    fun onSaveScoreClicked() {
        val current = _uiState.value
        val record = ScoreRecord(
            playerName = current.activePlayerName,
            score = current.score,
            difficultyLevel = settings.gameSpeed.toInt(),
            difficultyName = getDifficultyName(settings.gameSpeed),
            durationSeconds = settings.roundDurationSeconds
        )

        viewModelScope.launch {
            scoreRepository.saveScore(record)
            onResetClicked()
        }
    }

    private fun startBonusSpawning() {
        bonusSpawnJob?.cancel()
        bonusSpawnJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay((GOLD_BUG_SPAWN_INTERVAL_SECONDS * 1000L).milliseconds)

                val current = _uiState.value
                if (!current.isRunning) break

                val points = goldRateRepository.rate.value?.bugPoints ?: BonusInsect.points

                val goldenBug = InsectSpawn(
                    id = ++nextInsectId,
                    type = InsectType.entries.random(),
                    isBonus = true,
                    bonusPoints = points
                )
                spawnTimes[goldenBug.id] = nowMs()
                _uiState.value = current.copy(insects = current.insects + goldenBug)
            }
        }
    }

    private fun scheduleNextTiltBonusSpawn() {
        tiltBonusSpawnJob?.cancel()
        tiltBonusSpawnJob = viewModelScope.launch {
            delay((TILT_BONUS_SPAWN_INTERVAL_SECONDS * 1000L).milliseconds)
            if (_uiState.value.isRunning) {
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

        if (current.isMidRound) {
            if (pausedAt != 0L) {
                val pauseLength = nowMs() - pausedAt
                spawnTimes.replaceAll { _, spawnedAt -> spawnedAt + pauseLength }
                pausedAt = 0L
            }
        } else {
            adaptive.startRound()
            spawnTimes.clear()
            pausedAt = 0L
        }

        viewModelScope.launch { goldRateRepository.refresh() }

        _uiState.value = if (current.isMidRound) {
            current.copy(isRunning = true)
        } else {
            GameUiState(
                isRunning = true,
                isMidRound = true,
                timeLeftSeconds = settings.roundDurationSeconds,
                activePlayerName = current.activePlayerName
            )
        }
        startTimer()
        startSpawning()
        startBonusSpawning()
        startAdaptation()

        val resumed = _uiState.value
        when {
            resumed.isTiltModeActive -> restartTiltModeTimer()
            resumed.tiltBonus != null -> restartTiltBonusLifetimeTimer(resumed.tiltBonus.id)
            else -> scheduleNextTiltBonusSpawn()
        }
    }

    fun onPauseClicked() {
        pausedAt = nowMs()
        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        adaptationJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun onResetClicked() {
        adaptive.startRound()
        spawnTimes.clear()
        pausedAt = 0L

        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        adaptationJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
        _uiState.value = GameUiState(
            timeLeftSeconds = settings.roundDurationSeconds,
            activePlayerName = _uiState.value.activePlayerName
        )
    }

    fun onInsectTapped(insectId: Long) {
        val current = _uiState.value
        if (!current.isRunning) return
        val insect = current.insects.find { it.id == insectId } ?: return

        val spawnedAt = spawnTimes.remove(insectId)
        adaptive.onHit(spawnedAt?.let { nowMs() - it })

        _uiState.value = current.copy(
            insects = current.insects.filterNot { it.id == insectId },
            score = (current.score + insect.points).coerceAtLeast(0)
        )
    }

    fun onMissTapped() {
        val current = _uiState.value
        if (!current.isRunning) return

        val penalty = adaptive.params.missPenalty
        adaptive.onMiss()
        _adaptiveParams.value = adaptive.params

        _uiState.value = current.copy(
            score = (current.score - penalty).coerceAtLeast(0)
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
                    adaptationJob?.cancel()
                    tiltBonusSpawnJob?.cancel()
                    tiltBonusLifetimeJob?.cancel()
                    tiltModeJob?.cancel()
                    spawnTimes.clear()
                    pausedAt = 0L
                    _adaptiveParams.value = adaptive.finishRound()
                }
            }
        }
    }

    private fun startSpawning() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                val params = adaptive.params
                val intervalMs = params.adjustSpawnInterval(settings.insectSpawnIntervalMillis)
                delay(intervalMs.milliseconds)

                val current = _uiState.value
                val maxBugs = adaptive.params.adjustMaxBugs(settings.maxBugsOnScreen)
                if (current.isRunning && current.insects.size < maxBugs) {
                    val newInsect = InsectSpawn(
                        id = ++nextInsectId,
                        type = InsectType.entries.random()
                    )
                    spawnTimes[newInsect.id] = nowMs()
                    _uiState.value = current.copy(insects = current.insects + newInsect)
                }
            }
        }
    }

    private fun startAdaptation() {
        adaptationJob?.cancel()
        adaptationJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(DdaConfig.TICK_MS.milliseconds)
                val s = _uiState.value
                if (!s.isRunning) break

                val maxBugs = adaptive.params.adjustMaxBugs(settings.maxBugsOnScreen)
                _adaptiveParams.value = adaptive.tick(s.insects.size, maxBugs)
            }
        }
    }

    fun onResetAdaptationClicked() {
        val current = _uiState.value
        if (current.isRunning || current.isMidRound) return
        adaptive.reset()
        _adaptiveParams.value = adaptive.params
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        spawnJob?.cancel()
        bonusSpawnJob?.cancel()
        adaptationJob?.cancel()
        tiltBonusSpawnJob?.cancel()
        tiltBonusLifetimeJob?.cancel()
        tiltModeJob?.cancel()
    }
}