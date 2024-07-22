package com.users.onboarding.domain.model

data class Movie(
    val id: Int? = null,
    val title: String? = null,
    val overview: String? = null,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val genres: List<Int>? = null,
    val runtime: Int? = null,
    val tagline: String? = null,
)