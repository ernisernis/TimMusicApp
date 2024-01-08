package com.example.timmusicapp.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timmusicapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    title: String,
    leftIcon: ImageVector?,
    onLeftIconClick: () -> Unit,
    rightIcon: ImageVector?,
    onRightIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.9.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onLeftIconClick) {
                if (leftIcon != null) Icon(
                    imageVector = leftIcon,
                    contentDescription = stringResource(id = R.string.topbar_icon_description_1),
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(onClick = onRightIconClick) {
                if (rightIcon != null) Icon(
                    imageVector = rightIcon,
                    contentDescription = stringResource(id = R.string.topbar_icon_description_2),
                    tint = Color.Black
                )
            }
        }, modifier = Modifier.shadow(5.dp)
    )
}

@Composable
fun LoadingProgressIndicator(modifier: Modifier) {
    CircularProgressIndicator(modifier = modifier, strokeWidth = 2.dp)
}

fun Modifier.clickableRipple(
    onClick: () -> Unit
): Modifier = composed {
    composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                color = Color.Black
            ),
            onClick = onClick
        )
    }
}

