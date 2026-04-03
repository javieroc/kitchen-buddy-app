package com.connan.kitchenassistant

import android.app.Application
import com.connan.kitchenassistant.data.initSupabaseClient

class KitchenAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initSupabaseClient(this)
    }
}
