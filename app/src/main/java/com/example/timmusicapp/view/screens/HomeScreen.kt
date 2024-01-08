package com.example.timmusicapp

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.timmusicapp.model.Music
import com.example.timmusicapp.ui.theme.backgroundMain
import com.example.timmusicapp.view.components.TopBarComponent
import com.example.timmusicapp.view.components.clickableRipple
import com.example.timmusicapp.view.screens.ErrorScreen
import com.example.timmusicapp.view.screens.LoadingScreen
import com.example.timmusicapp.view.state.HomeError
import com.example.timmusicapp.view.state.HomeLoading
import com.example.timmusicapp.view.state.HomeSuccess
import com.example.timmusicapp.viewmodel.GlobalViewModel
import java.util.LinkedHashMap


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: () -> Unit) {
    val globalViewModel: GlobalViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val homeState by globalViewModel.homeStatus.collectAsStateWithLifecycle()
    val totalFileSystemTime by globalViewModel.totalFileSystemTime.asIntState()
    val context = LocalContext.current

    val onCategoryClicked = { categoryName: String ->
       globalViewModel.getSongCategory(categoryName)
       onNavigate()
    }

    val onMemoryClicked = {
        globalViewModel.getMemory(context)
        onNavigate()
    }

    val onFileSystemClicked = {
        globalViewModel.getFileSystem(context)
        onNavigate()
    }

    Scaffold(
        topBar = { TopBarComponent(title = stringResource(id = R.string.app_name), leftIcon = Icons.Default.Menu, onLeftIconClick = {}, rightIcon = Icons.Default.Search, onRightIconClick = {})  },
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(), color = backgroundMain
        ) {
            when (homeState) {
                is HomeLoading -> {
                    LoadingScreen()
                }
                is HomeSuccess<*> -> {
                    HomeSuccess(musicList = (homeState as HomeSuccess<*>).data, onCategoryClicked, onMemoryClicked,onFileSystemClicked, globalViewModel.getTotalMusicTime(), totalFileSystemTime, onGetTotalFileSystemTime = { ctx -> globalViewModel.getTotalFileSystemTime(ctx)} )

                }
                is HomeError -> {
                    ErrorScreen(t = (homeState as HomeError).t)
                }
            }
        }
    }

}

@Composable
fun HomeSuccess(musicList: LinkedHashMap<String, MutableList<Music>>, onCategoryClick: (String) -> Unit, onMemoryClick: () -> Unit, onFileSystemClick: () -> Unit, totalMusicTime: String, totalFileSystemTime: Int, onGetTotalFileSystemTime: (Context) -> Unit) {

    val context = LocalContext.current
    onGetTotalFileSystemTime(context)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 10.dp, horizontal = 10.dp), modifier = Modifier
        .fillMaxWidth()) {
        musicList.forEach { (key, musicList) ->
            item {
                MusicComponent(key, musicList, onCategoryClick)
            }
        }
        item { 
            StorageComponent(onMemoryClick, onFileSystemClick ,totalMusicTime, totalFileSystemTime)
        }
    }
}

@Composable
fun StorageComponent(onMemoryClick: () -> Unit, onFileSystemClick: () -> Unit ,totalMusicTime: String, totalFileSystemTime: Int) {
    Card(modifier = Modifier
        .background(backgroundMain)
        .fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier
            .padding(vertical = 10.dp)){
            ComponentTitle(title = stringResource(id = R.string.component_title_1))
            StorageItem(title = stringResource(id = R.string.component_item_title_1), totalMusicTime, onMemoryClick)
            Divider(thickness = 1.dp)
            StorageItem(title = stringResource(id = R.string.component_item_title_2),"${totalFileSystemTime/60}m ${totalFileSystemTime%60}s", onFileSystemClick)
        }
    }
}

@Composable
fun StorageItem(title: String,time: String, onItemClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .height(50.dp)
        .fillMaxWidth()
        .clickableRipple { onItemClick() }) {
        Text(text = title, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 10.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = time, fontSize = 15.sp, modifier = Modifier.padding(start = 10.dp))
        IconButton(onClick = { onItemClick() }) {
           Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = stringResource(id = R.string.icon_description_1))
        }
    }
}

@Composable
fun MusicComponent(title: String, list: MutableList<Music>, onCategoryClick: (String) -> Unit) {

    val randomFiveSongs = list.take(5)

    Card(modifier = Modifier
        .background(backgroundMain)
        .fillMaxWidth()
        .clickableRipple { onCategoryClick(title) }, elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 10.dp)) {
            ComponentTopBar(title, onCategoryClick)
            ComponentContent(randomFiveSongs)
        }
    }

}
@Composable
fun ComponentTopBar(title: String, onCategoryClick: (String) -> Unit) {
    Row(modifier = Modifier
        .background(Color.Transparent)
        .padding(bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        ComponentTitle(title = title)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onCategoryClick(title) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = stringResource(id = R.string.icon_description_1), tint = Color.Black, modifier = Modifier.size(32.dp))
        }
    }
}


@Composable
fun ComponentTitle(title: String) {
    Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black, modifier = Modifier.padding(start = 10.dp))
}


@Composable
fun ComponentContent(list: List<Music>) {
    Row(modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        list.forEach { music ->
            MusicCard(music.image, music.name, music.sizeString, music.lengthString)
        }
    }
}

@Composable
fun MusicCard(image: Int, name: String, sizeFormatted: String, lengthFormatted: String ) {
    Card(modifier = Modifier.width(140.dp)) {
        AsyncImage(model = image, contentDescription = stringResource(id = R.string.icon_description_2), contentScale = ContentScale.Crop, modifier = Modifier.height(170.dp))
        MusicName(name = name)
        Row(
            Modifier
                .padding(vertical = 5.dp)
                .fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
            MusicSubtitle(text = sizeFormatted)
            MusicSubtitle(text = lengthFormatted)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicName(name: String) {
    Text(text = name, maxLines = 1, modifier = Modifier
        .basicMarquee(iterations = Int.MAX_VALUE), fontWeight = FontWeight.Bold)
}

@Composable
fun MusicSubtitle(text: String) {
    Text(text = text, fontSize = 12.sp)
}
