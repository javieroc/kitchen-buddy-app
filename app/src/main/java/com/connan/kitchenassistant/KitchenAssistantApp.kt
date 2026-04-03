package com.connan.kitchenassistant

import android.app.Application
import com.connan.kitchenassistant.data.initSupabaseClient
import com.connan.kitchenassistant.data.chat.initChatCache

class KitchenAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initSupabaseClient(this)
        initChatCache(this)
    }
}
