package com.example.buggame.di

import android.content.Context
import androidx.room.Room
import com.example.buggame.data.local.AppDatabase
import com.example.buggame.data.remote.CbrApi
import com.example.buggame.data.remote.trustCbrCertificate
import com.example.buggame.data.repository.GoldRateRepository
import com.example.buggame.data.repository.PlayerRepository
import com.example.buggame.data.repository.ScoreRepository
import com.example.buggame.ui.authors.AuthorsViewModel
import com.example.buggame.ui.game.GameViewModel
import com.example.buggame.ui.leaderboard.LeaderboardViewModel
import com.example.buggame.ui.registration.RegistrationViewModel
import com.example.buggame.ui.rules.RulesViewModel
import com.example.buggame.ui.settings.SettingsViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "buggame_database.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    single { get<AppDatabase>().playerDao() }
    single { get<AppDatabase>().scoreDao() }

    single {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .trustCbrCertificate()
            .build()
        Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .client(client)
            .build()
            .create(CbrApi::class.java)
    }
    single {
        GoldRateRepository(
            api = get(),
            prefs = androidContext().getSharedPreferences("gold_rate", Context.MODE_PRIVATE),
            appContext = androidContext()
        )
    }

    singleOf(::PlayerRepository)
    singleOf(::ScoreRepository)

    viewModelOf(::AuthorsViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::RulesViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::LeaderboardViewModel)
}
