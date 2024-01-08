package com.example.timmusicapp.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timmusicapp.view.state.CategoryError
import com.example.timmusicapp.view.state.CategoryLoading
import com.example.timmusicapp.view.state.CategoryState
import com.example.timmusicapp.view.state.FilesystemContent
import com.example.timmusicapp.view.state.HomeLoading
import com.example.timmusicapp.view.state.HomeState
import com.example.timmusicapp.view.state.HomeSuccess
import com.example.timmusicapp.view.state.MemoryContent
import com.example.timmusicapp.util.MockData
import com.example.timmusicapp.model.Music
import com.example.timmusicapp.view.state.MusicContent
import com.example.timmusicapp.view.state.MusicItemState
import com.example.timmusicapp.repository.SharedPrefsManager
import com.example.timmusicapp.repository.SharedPrefsManagerImpl
import com.example.timmusicapp.view.state.HomeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class GlobalViewModel : ViewModel(), SharedPrefsManager by SharedPrefsManagerImpl()  {

    private val _homeState = MutableStateFlow<HomeState>(HomeLoading(true))
    val homeStatus = _homeState.asStateFlow()

    private val _categoryState = MutableStateFlow<CategoryState>(CategoryLoading(true))
    val categoryState = _categoryState.asStateFlow()

    // Individual music item state
    private val _musicState = mutableStateMapOf<String, MusicItemState>()
    val musicState: SnapshotStateMap<String, MusicItemState> = _musicState

    private var totalMusicTime: Int = 0

    private val _totalFileSystemTime: MutableState<Int> = mutableIntStateOf(0)
    val totalFileSystemTime = _totalFileSystemTime

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val musicList = MockData.MusicMockData
                    val musicListMap = linkedMapOf<String, MutableList<Music>>()

                    musicList.forEach { music ->
                        totalMusicTime += music.lengthInSeconds
                        musicListMap.getOrPut(music.genre) { mutableListOf() }.add(music)
                    }

                    _homeState.value = HomeSuccess(musicListMap)
                } catch (throwable: Throwable) {
                    _homeState.value = HomeError(throwable)
                }
            }
        }
    }

    fun getSongCategory(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val homeState = _homeState.value

                if (homeState is HomeSuccess<*> && homeState.data.containsKey(name)) {
                    val filteredCategory = homeState.data[name]
                    if (filteredCategory != null) {
                        _categoryState.value = MusicContent(filteredCategory)
                    } else {
                        _categoryState.value = CategoryError(Exception("Category not found"))
                    }
                } else {
                    _categoryState.value = CategoryError(Exception("Category not found"))
                }
            }
        }
    }

    fun getMemory(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val homeState = _homeState.value

                if (homeState is HomeSuccess<*> && homeState.data.isNotEmpty()) {
                    val musicList: List<Music> = homeState.data.values.flatten()

                    // Check if music is saved in sharedPrefs
                    musicList.forEach { music ->
                       if (isMusicSavedPrefs(context = context, music.name))  {
                           music.isSaved = true
                       }
                    }

                    _categoryState.value = MemoryContent(musicList)
                } else {
                    _categoryState.value = CategoryError(Exception("Your songs are empty!"))
                }
            }
        }
    }

    fun getFileSystem(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val homeState = _homeState.value

                if (homeState is HomeSuccess<*> && homeState.data.isNotEmpty()) {
                    val musicList: List<Music> = homeState.data.values.flatten()

                    musicList.forEach { music ->
                        music.isSaved = isMusicSavedPrefs(context = context, music.name)
                    }

                    val filteredMusicList = musicList.filter { music ->
                       music.isSaved
                    }

                    _categoryState.value = FilesystemContent(filteredMusicList)

                } else {
                    _categoryState.value = CategoryError(Exception("Your songs are empty!"))
                }
            }
        }
    }

    fun getTotalMusicTime(): String {
        return "${totalMusicTime/60}m ${totalMusicTime%60}s"
    }

    fun getTotalFileSystemTime(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val homeState = _homeState.value

                if (homeState is HomeSuccess<*> && homeState.data.isNotEmpty()) {

                    val musicList: List<Music> = homeState.data.values.flatten()
                    var totalLength = 0

                    musicList.forEach { music ->
                        if (isMusicSavedPrefs(context = context, music.name))  {
                           totalLength += music.lengthInSeconds
                        }
                    }
                    _totalFileSystemTime.value = totalLength
                }
            }
        }
    }



    fun handleMusicClick(name: String, music: Music, state: MusicItemState, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                when (state) {
                    is MusicItemState.Save -> {
                        _musicState[name] = MusicItemState.Loading
                        saveMusicPrefs(context = context, music = music)
                        // Mock loading state
                        delay(3000)
                        _musicState[name] = MusicItemState.Delete
                    }
                    is MusicItemState.Loading -> {

                    }
                    is MusicItemState.Delete -> {
                        _musicState[name] = MusicItemState.Loading
                        deleteMusicPrefs(context = context, music.name)
                        // Mock loading state
                        delay(3000)
                        _musicState[name] = MusicItemState.Save
                    }
                }
            }
        }
    }

}