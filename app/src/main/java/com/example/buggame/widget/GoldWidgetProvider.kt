package com.example.buggame.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.buggame.R
import com.example.buggame.data.model.GoldRate
import com.example.buggame.data.repository.GoldRateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class GoldWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val repository: GoldRateRepository by inject()

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        Log.d(TAG, "onUpdate ids=${ids.toList()}")
        updateAll(context, repository.rate.value)
        refreshAsync()
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action=${intent.action}")
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) refreshAsync()
    }

    private fun refreshAsync() {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.refresh()
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        private const val TAG = "GoldWidget"
        private const val ACTION_REFRESH = "com.example.buggame.widget.ACTION_REFRESH"

        fun updateAll(context: Context, rate: GoldRate?) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, GoldWidgetProvider::class.java)
            )
            Log.d(TAG, "updateAll ids=${ids.toList()} rate=$rate")
            ids.forEach { manager.updateAppWidget(it, buildViews(context, rate)) }
        }

        private fun buildViews(context: Context, rate: GoldRate?): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_gold)

            if (rate != null) {
                views.setTextViewText(
                    R.id.widget_rate,
                    String.format(Locale.forLanguageTag("ru"), "%,.0f", rate.pricePerGram)
                )
                val fmt = SimpleDateFormat("dd.MM", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("Europe/Moscow")
                }
                views.setTextViewText(R.id.widget_date, "₽/г · ${fmt.format(rate.dateMillis)}")
            } else {
                views.setTextViewText(R.id.widget_rate, "—")
                views.setTextViewText(R.id.widget_date, "нет данных")
            }

            val intent = Intent(context, GoldWidgetProvider::class.java).setAction(ACTION_REFRESH)
            val pi = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pi)
            return views
        }
    }
}