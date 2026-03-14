package com.example.kitchenassistant.data

import com.example.kitchenassistant.BuildConfig
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient

lateinit var supabase: io.github.jan.supabase.SupabaseClient

fun initSupabaseClient(context: android.content.Context) {
    val settings = SharedPreferencesSettings(
        context.getSharedPreferences("supabase_session", android.content.Context.MODE_PRIVATE)
    )

    supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth) {
            sessionManager = SettingsSessionManager(settings)
            alwaysAutoRefresh = true
        }
    }
}
