package com.example.buggame.ui.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buggame.R
import com.example.buggame.ui.authors.AuthorsScreen
import com.example.buggame.ui.game.GameScreen
import com.example.buggame.ui.registration.RegistrationScreen
import com.example.buggame.ui.rules.RulesScreen
import com.example.buggame.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

enum class MainTab(val title: String, @DrawableRes val iconRes: Int?) {
    REGISTRATION("Регистрация", R.drawable.registration_icon),
    SETTINGS("Настройки", R.drawable.settings_icon),
    GAME("Игра", null),
    RULES("Правила", R.drawable.rules_icon),
    AUTHORS("Авторы", R.drawable.authors_icon),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen() {
    val tabs = MainTab.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.statusBarsPadding()
            ) {
                tabs.forEachIndexed { index, tab ->
                    val hasIcon = tab.iconRes != null
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
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
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                MainTab.GAME -> GameScreen()
                MainTab.RULES -> RulesScreen()
                MainTab.AUTHORS -> AuthorsScreen()
            }
        }
    }
}