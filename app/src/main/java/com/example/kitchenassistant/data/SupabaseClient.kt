package com.example.kitchenassistant.data

import com.example.kitchenassistant.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Auth)
}