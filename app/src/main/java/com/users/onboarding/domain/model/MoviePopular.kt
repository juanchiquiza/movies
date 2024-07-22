package com.users.onboarding.domain.model

data class MoviePopular(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)