package com.example.buggame.ui.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.buggame.ui.authors.AuthorsScreen
import com.example.buggame.ui.rules.RulesScreen
import com.example.buggame.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    RULES("Правила"),
    AUTHORS("Авторы"),
    SETTINGS("Настройки")
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
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tab.title) }
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
                MainTab.RULES -> RulesScreen()
                MainTab.AUTHORS -> AuthorsScreen()
                MainTab.SETTINGS -> SettingsScreen()
            }
        }
    }
}
