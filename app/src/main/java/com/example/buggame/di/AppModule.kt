package com.example.buggame.di

import com.example.buggame.ui.game.GameViewModel
import com.example.buggame.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GameViewModel)
    viewModelOf(::SettingsViewModel)

    // Сюда же в 5 и 7 лабе добавите Room и Retrofit:
    // single { Room.databaseBuilder(...).build() }
}