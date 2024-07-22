package com.users.onboarding.domain.usecases

import com.users.onboarding.data.repository.FirebaseRepository
import com.users.onboarding.utils.UtilsWithContextRequired
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

sealed class UploadDocumentsUseCaseResult {
    data class Success(val listUrls: List<String>) :
        UploadDocumentsUseCaseResult()

    data class Error(val error: String) : UploadDocumentsUseCaseResult()
}

interface UploadDocumentsUseCase {
    suspend fun invoke(
        uri: String,
    ): UploadDocumentsUseCaseResult
}

class UploadDocumentsUseCaseImpl(
    private val repository: FirebaseRepository,
    private val utilsWithContextRequired: UtilsWithContextRequired
) : UploadDocumentsUseCase {

    override suspend fun invoke(
        uri: String,
    ): UploadDocumentsUseCaseResult {
        return try {
            val bitmaps = utilsWithContextRequired.makeBitmapsFromPath(uriInString = uri)
            val listUrls = bitmaps.mapIndexed { index, bitmap ->
                val pathInFirebase = "photos/${Random.nextInt(1, 10000)}.jpg"

                val task = repository.uploadAttachmentsToFireStorage(
                    path = pathInFirebase,
                    bitmap = bitmap
                ).task
                val fileLink = task.result.metadata?.reference?.downloadUrl?.await()
                return@mapIndexed fileLink.toString()
            }

            UploadDocumentsUseCaseResult.Success(
                listUrls = listUrls
            )
        } catch (e: Exception) {
            UploadDocumentsUseCaseResult.Error(e.message.toString())
        }
    }
}