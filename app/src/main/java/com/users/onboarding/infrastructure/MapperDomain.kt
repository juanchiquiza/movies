package com.users.onboarding.infrastructure

import com.users.onboarding.data.model.MovieEntity
import com.users.onboarding.data.remote.MoviePopularResponse
import com.users.onboarding.data.remote.MovieResponse
import com.users.onboarding.domain.model.Movie
import com.users.onboarding.domain.model.MoviePopular

fun mapToDomain(body: MovieResponse): Movie {
    return Movie(
        id = body.id,
        title = body.title,
        overview = body.overview,
        posterUrl = body.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
        backdropUrl = body.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
        releaseDate = body.releaseDate,
        voteAverage = body.voteAverage,
        genres = body.genres?.map { it },
        runtime = body.runtime,
        tagline = body.tagline
    )
}

fun mapToDomainList(body: List<MovieResponse>): List<Movie> {
    return body.map {
        mapToDomain(it)
    }
}

fun mapToDomainMoviePopular(body: MoviePopularResponse): MoviePopular {
    return MoviePopular(
        page = body.page,
        results = mapToDomainList(body.results),
        totalPages = body.totalPages,
        totalResults = body.totalResults
    )
}

fun Movie.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = this.id ?: 0,
        title = this.title ?: "",
        overview = this.overview ?: "",
        posterPath = this.posterUrl ?: "",
        releaseDate = this.releaseDate ?: "",
    )
}

fun List<Movie>.toMovieEntityList(): List<MovieEntity> {
    return this.map { it.toMovieEntity() }
}

fun MovieEntity.toEntityMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = this.posterPath,
        releaseDate = this.releaseDate,
    )
}

fun List<MovieEntity>.toEntityMovieList(): List<Movie> {
    return this.map { it.toEntityMovie() }
}

