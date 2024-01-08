package com.example.timmusicapp.view.state


sealed class MusicItemState {
    object Save : MusicItemState()
    object Delete: MusicItemState()
    object Loading: MusicItemState()
}

