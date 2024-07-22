package com.users.onboarding.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri

class UtilsWithContextRequired(
    private val context: Context,
) {
    fun makeBitmapsFromPath(uriInString: String): MutableList<Bitmap> {
        val listBitmaps = mutableListOf<Bitmap>()
        getBitmapFromImage(uriInString)?.let { listBitmaps.add(it) }
        return listBitmaps
    }

    private fun getBitmapFromImage(uriInString: String?): Bitmap? {
        return if (!uriInString.isNullOrEmpty()) {
            BitmapFactory.decodeStream(context.applicationContext.contentResolver.openInputStream(uriInString.toUri()))
        } else null
    }
}