package com.example.fundflow

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.navigation.AppNavGraph
import com.example.fundflow.ui.theme.FundFlowTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsDataStoreEntryPoint {
    fun settingsDataStore(): SettingsDataStore
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = runCatching {
            val entryPoint = EntryPointAccessors.fromApplication(
                newBase.applicationContext,
                SettingsDataStoreEntryPoint::class.java
            )
            runBlocking { entryPoint.settingsDataStore().language.first() }
        }.getOrDefault("id")

        super.attachBaseContext(applyLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FIX BUG 3:
        // Baca isDarkTheme secara sinkron SATU KALI di sini, hanya untuk
        // menentukan warna status bar awal di enableEdgeToEdge().
        // Setelah ini, tema dikelola secara reaktif oleh StateFlow di setContent.
        val initialDarkTheme = runBlocking {
            settingsDataStore.isDarkTheme.first()
        }

        // Terapkan SystemBarStyle yang sesuai agar warna icon status bar
        // (putih untuk dark mode, hitam untuk light mode) sudah benar sejak awal.
        enableEdgeToEdge(
            statusBarStyle = if (initialDarkTheme) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
        )

        setContent {
            // Observe preferensi tema secara reaktif dari DataStore.
            // Saat isDarkTheme berubah, FundFlowTheme akan recompose
            // dan seluruh UI yang memakai MaterialTheme.colorScheme
            // akan ikut berubah secara otomatis.
            val isDarkTheme by settingsDataStore.isDarkTheme
                .collectAsStateWithLifecycle(initialValue = initialDarkTheme)

            FundFlowTheme(darkTheme = isDarkTheme) {
                AppNavGraph(
                    onLanguageChanged = { recreateActivity() }
                )
            }
        }
    }

    private fun recreateActivity() {
        recreate()
    }

    companion object {
        fun applyLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
                config.setLayoutDirection(locale)
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
            }
            return context.createConfigurationContext(config)
        }
    }
}