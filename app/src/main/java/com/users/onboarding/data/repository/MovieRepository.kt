package com.users.onboarding.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.users.onboarding.data.remote.ApiError
import com.users.onboarding.data.remote.MoviePopularResponse
import com.users.onboarding.data.network.TmApi

class MovieRepository(
    private val service: TmApi,
) {

    suspend fun getMovies(apiKey: String): NetworkResponse<MoviePopularResponse, ApiError> {
        return service.getPopularMovies(apiKey)
    }

    suspend fun getTopRatedMovies(apiKey: String): NetworkResponse<MoviePopularResponse, ApiError> {
        return service.getTopRatedMovies(apiKey)
    }
}