package com.users.onboarding.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class FirebaseRepository(
    private val firebaseStorage: FirebaseStorage,
) {

    suspend fun uploadAttachmentsToFireStorage(
        path: String,
        bitmap: Bitmap?,
        isPng: Boolean? = false
    ): UploadTask.TaskSnapshot {
        val stream = ByteArrayOutputStream()
        if (isPng == true) {
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        } else {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }
        return firebaseStorage.reference.child(path).putBytes(stream.toByteArray()).await()
    }
}