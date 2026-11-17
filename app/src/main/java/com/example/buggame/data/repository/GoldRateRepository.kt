package com.example.buggame.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.buggame.data.model.GoldRate
import com.example.buggame.data.remote.CbrApi
import com.example.buggame.data.remote.CbrMetalParser
import com.example.buggame.widget.GoldWidgetProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class GoldRateRepository(
    private val api: CbrApi,
    private val prefs: SharedPreferences,
    private val appContext: Context
) {
    private val _rate = MutableStateFlow(loadCached())
    val rate: StateFlow<GoldRate?> = _rate.asStateFlow()

    suspend fun refresh(): GoldRate? = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val bytes = api.getMetalPrices(
                dateFrom = CbrMetalParser.formatRequestDate(now - WINDOW_MILLIS),
                dateTo = CbrMetalParser.formatRequestDate(now)
            ).use { it.bytes() }

            val fresh = CbrMetalParser.parseLatestGold(bytes.inputStream())
            Log.d(TAG, "Курс загружен: $fresh")

            val cached = _rate.value
            if (fresh != null && (cached == null || fresh.dateMillis >= cached.dateMillis)) {
                save(fresh)
                _rate.value = fresh
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось обновить курс золота, используем последнее значение", e)
        }

        runCatching { GoldWidgetProvider.updateAll(appContext, _rate.value) }
            .onFailure { Log.w(TAG, "Не удалось обновить виджет", it) }

        _rate.value
    }

    private fun save(rate: GoldRate) {
        prefs.edit()
            .putString(KEY_PRICE, rate.pricePerGram.toString())
            .putLong(KEY_DATE, rate.dateMillis)
            .apply()
    }

    private fun loadCached(): GoldRate? {
        val price = prefs.getString(KEY_PRICE, null)?.toDoubleOrNull() ?: return null
        return GoldRate(price, prefs.getLong(KEY_DATE, 0L))
    }

    private companion object {
        const val TAG = "GoldRateRepository"
        const val KEY_PRICE = "price"
        const val KEY_DATE = "date"
        const val WINDOW_MILLIS = 10L * 24 * 60 * 60 * 1000
    }
}