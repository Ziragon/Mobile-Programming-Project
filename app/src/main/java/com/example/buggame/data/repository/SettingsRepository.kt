package com.example.buggame.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.buggame.data.model.GameSettings

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadSettings(): GameSettings {
        return GameSettings(
            gameSpeed = prefs.getFloat(KEY_GAME_SPEED, 1.0f),
            maxBugsOnScreen = prefs.getInt(KEY_MAX_BUGS, 10),
            bonusIntervalSeconds = prefs.getInt(KEY_BONUS_INTERVAL, 15),
            roundDurationSeconds = prefs.getInt(KEY_ROUND_DURATION, 60)
        )
    }

    fun saveSettings(settings: GameSettings) {
        prefs.edit()
            .putFloat(KEY_GAME_SPEED, settings.gameSpeed)
            .putInt(KEY_MAX_BUGS, settings.maxBugsOnScreen)
            .putInt(KEY_BONUS_INTERVAL, settings.bonusIntervalSeconds)
            .putInt(KEY_ROUND_DURATION, settings.roundDurationSeconds)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "bug_game_settings"
        private const val KEY_GAME_SPEED = "key_game_speed"
        private const val KEY_MAX_BUGS = "key_max_bugs"
        private const val KEY_BONUS_INTERVAL = "key_bonus_interval"
        private const val KEY_ROUND_DURATION = "key_round_duration"
    }
}