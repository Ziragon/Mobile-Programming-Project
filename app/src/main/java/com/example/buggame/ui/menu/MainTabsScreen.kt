package com.example.buggame.ui.menu


import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buggame.R
import com.example.buggame.ui.authors.AuthorsScreen
import com.example.buggame.ui.game.GameScreen
import com.example.buggame.ui.game.GameViewModel
import com.example.buggame.ui.leaderboard.LeaderboardScreen
import com.example.buggame.ui.registration.RegistrationScreen
import com.example.buggame.ui.rules.RulesScreen
import com.example.buggame.ui.settings.SettingsScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

enum class MainTab(val title: String, @DrawableRes val iconRes: Int?) {
    REGISTRATION("Регистрация", R.drawable.registration_icon),
    SETTINGS("Настройки", R.drawable.settings_icon),
    GAME("Игра", R.drawable.play),
    LEADERBOARD("Рекорды", R.drawable.podium),
    RULES("Правила", R.drawable.rules_icon),
    AUTHORS("Авторы", R.drawable.authors_icon)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen() {
    val tabs = MainTab.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    val gameViewModel: GameViewModel = koinViewModel()
    val gameState by gameViewModel.uiState.collectAsState()

    val isGameplayActive = gameState.isRunning

    Scaffold(
        topBar = {
            if (!isGameplayActive) {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val hasIcon = tab.iconRes != null
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()

                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = if (hasIcon) {
                                {
                                    Icon(
                                        painter = painterResource(id = tab.iconRes),
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else null,
                            text = if (!hasIcon) {
                                { Text(tab.title) }
                            } else null
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isGameplayActive,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (isGameplayActive) Modifier.statusBarsPadding() else Modifier
                )
        ) { page ->
            when (tabs[page]) {
                MainTab.REGISTRATION -> RegistrationScreen(
                    onRegistrationComplete = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(MainTab.SETTINGS.ordinal)
                        }
                    }
                )
                MainTab.SETTINGS -> SettingsScreen()
                MainTab.GAME -> GameScreen(viewModel = gameViewModel)
                MainTab.LEADERBOARD -> LeaderboardScreen()
                MainTab.RULES -> RulesScreen()
                MainTab.AUTHORS -> AuthorsScreen()
            }
        }
    }
}