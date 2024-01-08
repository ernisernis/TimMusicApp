package com.example.timmusicapp.repository

import android.content.Context
import com.example.timmusicapp.model.Music

interface SharedPrefsManager {
    fun saveMusicPrefs(context: Context, music: Music)
    fun deleteMusicPrefs(context: Context, name: String)
    fun isMusicSavedPrefs(context: Context, name: String): Boolean
}