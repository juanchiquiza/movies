package com.users.onboarding.data.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.users.onboarding.data.remote.ApiError
import com.users.onboarding.data.remote.MoviePopularResponse
import com.users.onboarding.data.remote.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): NetworkResponse<MoviePopularResponse, ApiError>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String): NetworkResponse<MoviePopularResponse, ApiError>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String): NetworkResponse<MovieResponse, ApiError>
}