package com.example.kitchenassistant

import android.app.Application
import com.example.kitchenassistant.data.initSupabaseClient

class KitchenAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initSupabaseClient(this)
    }
}
