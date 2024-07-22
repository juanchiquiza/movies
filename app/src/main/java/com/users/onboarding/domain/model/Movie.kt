package com.users.onboarding.domain.model

data class Movie(
    val id: Int?,
    val title: String?,
    val overview: String?,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val genres: List<Int>?,
    val runtime: Int?,
    val tagline: String?
)