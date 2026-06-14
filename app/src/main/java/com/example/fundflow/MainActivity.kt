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

        val initialDarkTheme = runBlocking {
            settingsDataStore.isDarkTheme.first()
        }

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

        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.w("FCM_TEST", "Gagal mendapatkan token FCM", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            android.util.Log.d("FCM_TEST", "Token FCM Saat Ini: $token")
        }

        setContent {
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