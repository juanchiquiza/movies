package com.users.onboarding.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.users.onboarding.data.model.MovieEntity
import com.users.onboarding.data.movieDao.MovieDao
import com.users.onboarding.data.remote.ApiError
import com.users.onboarding.data.remote.MoviePopularResponse
import com.users.onboarding.data.network.TmApi

class MovieDbRepository(
    private val movieDao: MovieDao
) {

    suspend fun saveMovies(list: List<MovieEntity>): Boolean{
        movieDao.insertMovies(list)
        return true
    }

    suspend fun getMoviesFromDatabase(): List<MovieEntity> {
        return movieDao.getAllMovies()
    }

    suspend fun getMovieById(movieId: Int): MovieEntity? {
        return movieDao.getMovieById(movieId)
    }
}