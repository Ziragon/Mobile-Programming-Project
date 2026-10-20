package com.example.buggame.di

import androidx.room.Room
import com.example.buggame.data.local.AppDatabase
import com.example.buggame.data.repository.PlayerRepository
import com.example.buggame.ui.authors.AuthorsViewModel
import com.example.buggame.ui.game.GameViewModel
import com.example.buggame.ui.registration.RegistrationViewModel
import com.example.buggame.ui.rules.RulesViewModel
import com.example.buggame.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "buggame_database.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    single { get<AppDatabase>().playerDao() }
    singleOf(::PlayerRepository)

    viewModelOf(::AuthorsViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::RulesViewModel)
    viewModelOf(::SettingsViewModel)
}
