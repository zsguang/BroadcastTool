package com.zsg.broadcasttool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.zsg.broadcasttool.utils.ToastUtil

class AppContext : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        val KEY_HISTORY_BROADCASTS: String = "key_history_broadcasts"
        val KEY_HISTORY_RECEIVES: String = "key_history_receives"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        ToastUtil.init(context)
    }

}