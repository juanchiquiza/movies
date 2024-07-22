package com.users.onboarding.koin

import com.users.onboarding.domain.usecases.GetMoviesUseCase
import com.users.onboarding.domain.usecases.GetMoviesUseCaseImpl
import com.users.onboarding.domain.usecases.MoviesDbUseCase
import com.users.onboarding.domain.usecases.MoviesDbUseCaseImpl
import com.users.onboarding.domain.usecases.UploadDocumentsUseCase
import com.users.onboarding.domain.usecases.UploadDocumentsUseCaseImpl
import com.users.onboarding.presentation.viewmodel.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mappers = module {

}
val useCases = module {
    factory<GetMoviesUseCase> {
        GetMoviesUseCaseImpl(
            repository = get(),
        )
    }
    factory<UploadDocumentsUseCase> {
        UploadDocumentsUseCaseImpl(
            repository = get(),
            utilsWithContextRequired = get()
        )
    }
    factory<MoviesDbUseCase> {
        MoviesDbUseCaseImpl(
            repository = get(),
        )
    }
}
val viewModels = module {
    viewModel {
        MovieViewModel(
            useCase = get(),
            uploadDocumentsUseCase = get(),
            moviesDbUseCase = get(),
        )
    }
}

val features = mappers + useCases + viewModels + coreModule