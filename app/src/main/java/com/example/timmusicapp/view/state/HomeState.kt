package com.example.timmusicapp.view.state

import com.example.timmusicapp.model.Music
import java.util.LinkedHashMap


sealed class HomeState
data class HomeLoading(val loading: Boolean = false) : HomeState()
data class HomeSuccess <T : LinkedHashMap<String,MutableList<Music>>>(val data: T): HomeState()
data class HomeError(val t: Throwable): HomeState()