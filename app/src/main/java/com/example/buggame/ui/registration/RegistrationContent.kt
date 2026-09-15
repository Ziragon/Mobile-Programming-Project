package com.example.buggame.ui.registration

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.buggame.R
import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import com.example.buggame.data.model.ZodiacSign
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationContent(
    state: RegistrationState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onCourseChange: (Course) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onSubmitClick: () -> Unit,
    onResultDialogDismiss: () -> Unit
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

        OutlinedTextField(
            value = state.fullName,
            onValueChange = onNameChange,
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
        }

        Button(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрировать")
        }
    }

    if (state.isResultDialogVisible && state.submittedProfile != null) {
        ResultDialog(
            profile = state.submittedProfile,
            onDismiss = onResultDialogDismiss
        )
    }
}

@Composable
private fun ResultDialog(
    profile: PlayerProfile,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "Регистрация завершена",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ResultRow(label = "Игрок", value = profile.fullName)
                        ResultRow(label = "Пол", value = profile.gender.title)
                        ResultRow(label = "Курс", value = profile.course.title)
                        ResultRow(label = "Сложность", value = profile.difficulty.toString())
                        ResultRow(label = "Дата рождения", value = profile.birthDate.toString())
                        ResultRow(label = "Знак зодиака", value = profile.zodiacSign.title)
                    }

                    Image(
                        painter = painterResource(id = zodiacIconRes(profile.zodiacSign)),
                        contentDescription = profile.zodiacSign.title,
                        modifier = Modifier.size(96.dp)
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .widthIn(min = 160.dp)
                ) {
                    Text("Начать")
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

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