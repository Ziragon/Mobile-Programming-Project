package com.example.buggame.data.remote

import android.util.Xml
import com.example.buggame.data.model.GoldRate
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object CbrMetalParser {

    private const val GOLD_CODE = "1"

    private fun moscowFormat(pattern: String) =
        SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
            isLenient = false
        }

    fun formatRequestDate(millis: Long): String =
        moscowFormat("dd/MM/yyyy").format(millis)

    fun parseLatestGold(input: InputStream): GoldRate? {
        val parser = Xml.newPullParser()
        parser.setInput(input, null)
        val dateFormat = moscowFormat("dd.MM.yyyy")

        var latest: GoldRate? = null
        var curDate: Long? = null
        var curCode: String? = null
        var curTag: String? = null
        var buy: Double? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    curTag = parser.name
                    if (parser.name == "Record") {
                        curCode = parser.getAttributeValue(null, "Code")
                        curDate = parser.getAttributeValue(null, "Date")
                            ?.let { runCatching { dateFormat.parse(it)?.time }.getOrNull() }
                        buy = null
                    }
                }
                XmlPullParser.TEXT -> {
                    if (curTag == "Buy") buy = parsePrice(parser.text)
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "Record") {
                        val d = curDate
                        val p = buy
                        if (curCode == GOLD_CODE && d != null && p != null &&
                            (latest == null || d >= latest.dateMillis)
                        ) {
                            latest = GoldRate(p, d)
                        }
                    }
                    curTag = null
                }
            }
            event = parser.next()
        }
        return latest
    }

    private fun parsePrice(raw: String): Double? =
        raw.filter { it.isDigit() || it == ',' || it == '.' }
            .replace(',', '.')
            .toDoubleOrNull()
}