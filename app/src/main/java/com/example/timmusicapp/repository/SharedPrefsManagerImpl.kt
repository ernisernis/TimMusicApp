package com.example.timmusicapp.repository

import android.content.Context
import com.example.timmusicapp.model.Music
import com.google.gson.Gson

class SharedPrefsManagerImpl: SharedPrefsManager {

    override fun saveMusicPrefs(context: Context, music: Music) {
        val sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(music)
        sharedPreferences.edit().apply {
            putString(music.name, json)
            apply()
        }

    }

    override fun deleteMusicPrefs(context: Context, name: String) {
        val sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            remove(name)
            apply()
        }
    }

    override fun isMusicSavedPrefs(context: Context, name: String): Boolean {
        val sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.contains(name)
    }


}