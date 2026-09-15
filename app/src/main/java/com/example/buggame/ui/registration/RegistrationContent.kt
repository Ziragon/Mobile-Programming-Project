package com.example.buggame.ui.registration

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.buggame.R
import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.ZodiacSign
import java.time.LocalDate

/**
 * Чистая вёрстка экрана регистрации.
 * Ничего не знает о ViewModel — только принимает state и коллбэки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationContent(
    state: RegistrationState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onCourseChange: (Course) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onSubmitClick: () -> Unit
) {
    var courseMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Регистрация игрока",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // ---------- ФИО ----------
        OutlinedTextField(
            value = state.fullName,
            onValueChange = onNameChange,
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // ---------- Пол (RadioButton) ----------
        Column {
            Text(text = "Пол", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Gender.entries.forEach { g ->
                    RadioButton(
                        selected = state.gender == g,
                        onClick = { onGenderChange(g) }
                    )
                    Text(text = g.title, modifier = Modifier.padding(end = 16.dp))
                }
            }
        }

        // ---------- Курс (выпадающий список) ----------
        ExposedDropdownMenuBox(
            expanded = courseMenuExpanded,
            onExpandedChange = { courseMenuExpanded = !courseMenuExpanded }
        ) {
            OutlinedTextField(
                value = state.course.title,
                onValueChange = {},
                readOnly = true,
                label = { Text("Курс") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = courseMenuExpanded,
                onDismissRequest = { courseMenuExpanded = false }
            ) {
                Course.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.title) },
                        onClick = {
                            onCourseChange(option)
                            courseMenuExpanded = false
                        }
                    )
                }
            }
        }

        // ---------- Уровень сложности (SeekBar → Slider) ----------
        Column {
            Text(
                text = "Уровень сложности: ${state.difficulty}",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = state.difficulty.toFloat(),
                onValueChange = { onDifficultyChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8
            )
        }

        // ---------- Дата рождения (CalendarView) ----------
        Column {
            Text(text = "Дата рождения", style = MaterialTheme.typography.labelLarge)
            AndroidView(
                factory = { context ->
                    val themedContext = ContextThemeWrapper(context, R.style.CalendarViewStyle)
                    CalendarView(themedContext).apply {
                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ---------- Кнопка регистрации ----------
        Button(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрировать")
        }

        HorizontalDivider()

        // ---------- Результат (TextView) ----------
        if (state.summaryText.isNotBlank()) {
            Text(
                text = state.summaryText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // ---------- Знак зодиака (ImageBox) ----------
        state.submittedProfile?.let { profile ->
            Image(
                painter = painterResource(id = zodiacIconRes(profile.zodiacSign)),
                contentDescription = profile.zodiacSign.title,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}

/**
 * Маппинг знака зодиака на drawable-иконку.
 * Замени имена ресурсов на свои реальные файлы в res/drawable.
 */
private fun zodiacIconRes(sign: ZodiacSign): Int = when (sign) {
    ZodiacSign.ARIES -> R.drawable.zodiac_aries
    ZodiacSign.TAURUS -> R.drawable.zodiac_taurus
    ZodiacSign.GEMINI -> R.drawable.zodiac_gemini
    ZodiacSign.CANCER -> R.drawable.zodiac_cancer
    ZodiacSign.LEO -> R.drawable.zodiac_leo
    ZodiacSign.VIRGO -> R.drawable.zodiac_virgo
    ZodiacSign.LIBRA -> R.drawable.zodiac_libra
    ZodiacSign.SCORPIO -> R.drawable.zodiac_scorpio
    ZodiacSign.SAGITTARIUS -> R.drawable.zodiac_sagittarius
    ZodiacSign.CAPRICORN -> R.drawable.zodiac_capricorn
    ZodiacSign.AQUARIUS -> R.drawable.zodiac_aquarius
    ZodiacSign.PISCES -> R.drawable.zodiac_pisces
}