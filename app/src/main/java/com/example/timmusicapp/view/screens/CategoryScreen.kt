package com.example.timmusicapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.timmusicapp.LocalNavGraphViewModelStoreOwner
import com.example.timmusicapp.MusicName
import com.example.timmusicapp.MusicSubtitle
import com.example.timmusicapp.R
import com.example.timmusicapp.model.Music
import com.example.timmusicapp.ui.theme.backgroundMain
import com.example.timmusicapp.view.components.LoadingProgressIndicator
import com.example.timmusicapp.view.components.TopBarComponent
import com.example.timmusicapp.view.state.CategoryError
import com.example.timmusicapp.view.state.CategoryLoading
import com.example.timmusicapp.view.state.CategoryState
import com.example.timmusicapp.view.state.FilesystemContent
import com.example.timmusicapp.view.state.MemoryContent
import com.example.timmusicapp.view.state.MusicContent
import com.example.timmusicapp.view.state.MusicItemState
import com.example.timmusicapp.viewmodel.GlobalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(onNavigate: () -> Unit) {
    val globalViewModel: GlobalViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val categoryState by globalViewModel.categoryState.collectAsStateWithLifecycle()
    val topBarTitle = getTopBarTitle(categoryState = categoryState)

    Scaffold(
        topBar = { TopBarComponent(title = topBarTitle, leftIcon = Icons.Default.KeyboardArrowLeft, onLeftIconClick = onNavigate, rightIcon = null, onRightIconClick = {}) },
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(), color = backgroundMain
        ) {
            when (categoryState) {
                is CategoryLoading -> {
                    LoadingScreen()
                }
                is MusicContent<*> -> {
                    CategoryList(musicList = (categoryState as MusicContent<*>).data, showButtons = false, viewModel =  globalViewModel)
                }
                is MemoryContent<*> -> {
                    CategoryList(musicList = (categoryState as MemoryContent<*>).data, showButtons = true, viewModel =  globalViewModel)
                }
                is FilesystemContent<*> -> {
                    CategoryList(musicList = (categoryState as FilesystemContent<*>).data, showButtons = true, viewModel =  globalViewModel)
                }
                is CategoryError -> {
                    ErrorScreen(t = (categoryState as CategoryError).t )
                }
            }
        }
    }
}

@Composable
fun CategoryList(
    musicList: List<Music>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(10.dp),
    contentPadding: PaddingValues = PaddingValues(10.dp),
    showButtons: Boolean,
    viewModel: GlobalViewModel
) {
    LazyColumn(verticalArrangement = verticalArrangement, contentPadding = contentPadding, modifier = modifier.fillMaxWidth() ) {
        items(musicList) {music ->
            val context = LocalContext.current
            val musicState = if (viewModel.musicState[music.name] == null) {
                if (music.isSaved) {
                    MusicItemState.Delete
                } else {
                    MusicItemState.Save
                }
            } else {
                viewModel.musicState[music.name]
            }
           MusicHorizontal(music = music, showButtons = showButtons, state = musicState!!, onClick = { state ->
               viewModel.handleMusicClick(music.name, music, state, context)
           })
        }
    }
}

@Composable
fun MusicHorizontal(music: Music, showButtons: Boolean, state: MusicItemState, onClick: (MusicItemState) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)) {
        Row {
           AsyncImage(model = music.image, contentDescription = stringResource(id = R.string.icon_description_2), contentScale = ContentScale.Crop, modifier = Modifier.width(50.dp))
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 5.dp)) {
                MusicName(name = music.name)
                MusicSubtitle(text = music.sizeAndLength)
            }
            if (showButtons) {
                IconButton(onClick = { onClick(state) }) {
                    when (state) {
                        is MusicItemState.Save -> Icon(imageVector = Icons.Default.Add , contentDescription = stringResource(id = R.string.music_state_save))
                        is MusicItemState.Loading -> LoadingProgressIndicator(modifier = Modifier.size(24.dp))
                        is MusicItemState.Delete -> Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.music_state_delete))
                    }
                }
            }
        }
    }
}


@Composable
fun getTopBarTitle(categoryState: CategoryState): String {
    return when (categoryState) {
        is CategoryLoading -> stringResource(id = R.string.category_state_loading)
        is MusicContent<*> -> categoryState.data.first().genre
        is MemoryContent<*> -> stringResource(id = R.string.category_state_memory)
        is FilesystemContent<*> -> stringResource(id = R.string.category_state_filesystem)
        is CategoryError -> stringResource(id = R.string.category_state_error)
    }
}
