package com.example.timmusicapp.model

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class Music(
    val name: String,
    val size: Float,
    val lengthInSeconds: Int,
    val genre: String,
    @DrawableRes val image: Int,
    var isSaved: Boolean = false
) {
    val sizeString: String
        get() = "${size}MB"

    val lengthString: String
        get() = "${lengthInSeconds/60}m ${lengthInSeconds%60}s"

    val sizeAndLength: String
        get() = "$sizeString - $lengthString"
}
