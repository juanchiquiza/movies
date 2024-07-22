package com.users.onboarding.domain.usecases

import com.haroldadmin.cnradapter.NetworkResponse
import com.users.onboarding.data.model.MovieEntity
import com.users.onboarding.data.repository.MovieDbRepository

sealed class MoviesDbUseCaseResult {
    data class Success(val result: Boolean) : MoviesDbUseCaseResult()
    data class SuccessGetMovies(val resultList: List<MovieEntity>) : MoviesDbUseCaseResult()
    data class Error(val error: String?) : MoviesDbUseCaseResult()
}

interface MoviesDbUseCase {
    suspend fun invoke(list: List<MovieEntity>): MoviesDbUseCaseResult
    suspend fun invokeGetMovies(): MoviesDbUseCaseResult
}

class MoviesDbUseCaseImpl(
    private val repository: MovieDbRepository,
) : MoviesDbUseCase {

    override suspend fun invoke(list: List<MovieEntity>): MoviesDbUseCaseResult {
        return when (val result =
            repository.saveMovies(list)) {
            true -> {
                MoviesDbUseCaseResult.Success(
                    result = result
                )
            }

            else -> {
                MoviesDbUseCaseResult.Error("Error")
            }
        }
    }

    override suspend fun invokeGetMovies(): MoviesDbUseCaseResult {
        return when (val result =
            repository.getMoviesFromDatabase()) {

            else -> {
                MoviesDbUseCaseResult.SuccessGetMovies(
                    resultList = result
                )
            }
        }
    }
}