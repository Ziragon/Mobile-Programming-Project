package com.example.buggame.di

import com.example.buggame.ui.authors.AuthorsViewModel
import com.example.buggame.ui.game.GameViewModel
import com.example.buggame.ui.registration.RegistrationViewModel
import com.example.buggame.ui.rules.RulesViewModel
import com.example.buggame.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AuthorsViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::RulesViewModel)
    viewModelOf(::SettingsViewModel)
}
