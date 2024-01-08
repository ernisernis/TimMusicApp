package com.example.timmusicapp.view.state

import com.example.timmusicapp.model.Music


sealed class CategoryState
data class CategoryLoading(val loading: Boolean = false): CategoryState()
data class MusicContent<T: List<Music>>(val data: T): CategoryState()
data class MemoryContent<T: List<Music>>(val data: T): CategoryState()
data class FilesystemContent<T: List<Music>>(val data: T): CategoryState()
data class CategoryError(val t: Throwable): CategoryState()
