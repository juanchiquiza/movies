package com.users.onboarding.domain.usecases

import com.haroldadmin.cnradapter.NetworkResponse
import com.users.onboarding.data.repository.MovieRepository
import com.users.onboarding.domain.model.MoviePopular
import com.users.onboarding.infrastructure.mapToDomainMoviePopular

sealed class GetMoviesUseCaseResult {
    data class Success(val result: MoviePopular) : GetMoviesUseCaseResult()
    data class Error(val error: String?) : GetMoviesUseCaseResult()
}

interface GetMoviesUseCase {
    suspend fun invokePopular(): GetMoviesUseCaseResult
    suspend fun invokeTopRated(): GetMoviesUseCaseResult
}

class GetMoviesUseCaseImpl(
    private val repository: MovieRepository,
) : GetMoviesUseCase {

    override suspend fun invokePopular(): GetMoviesUseCaseResult {
        val apiKey = "7580f843c1defc79cdcb8487b39e4f99"
        return when (val result =
            repository.getMovies(apiKey)) {
            is NetworkResponse.Success -> {
                GetMoviesUseCaseResult.Success(
                    mapToDomainMoviePopular(result.body)
                )
            }

            is NetworkResponse.ServerError -> {
                val serverError = result.body?.toString()
                GetMoviesUseCaseResult.Error(serverError)
            }

            is NetworkResponse.NetworkError ->
                GetMoviesUseCaseResult.Error("error")

            else -> {
                GetMoviesUseCaseResult.Error("Error")
            }
        }
    }

    override suspend fun invokeTopRated(): GetMoviesUseCaseResult {
        val apiKey = "7580f843c1defc79cdcb8487b39e4f99"
        return when (val result =
            repository.getTopRatedMovies(apiKey)) {
            is NetworkResponse.Success -> {
                GetMoviesUseCaseResult.Success(
                    mapToDomainMoviePopular(result.body)
                )
            }

            is NetworkResponse.ServerError -> {
                val serverError = result.body?.toString()
                GetMoviesUseCaseResult.Error(serverError)
            }

            is NetworkResponse.NetworkError ->
                GetMoviesUseCaseResult.Error("error")

            else -> {
                GetMoviesUseCaseResult.Error("Error")
            }
        }
    }
}