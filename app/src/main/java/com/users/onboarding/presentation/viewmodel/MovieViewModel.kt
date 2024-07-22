package com.users.onboarding.presentation.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.users.onboarding.data.model.MovieEntity
import com.users.onboarding.data.repository.MovieDbRepository
import com.users.onboarding.data.server.config.ApiState
import com.users.onboarding.domain.usecases.GetMoviesUseCase
import com.users.onboarding.domain.usecases.GetMoviesUseCaseResult
import com.users.onboarding.domain.usecases.MoviesDbUseCase
import com.users.onboarding.domain.usecases.MoviesDbUseCaseResult
import com.users.onboarding.domain.usecases.UploadDocumentsUseCase
import com.users.onboarding.domain.usecases.UploadDocumentsUseCaseResult
import com.users.onboarding.infrastructure.toEntityMovieList
import com.users.onboarding.infrastructure.toMovieEntityList
import kotlinx.coroutines.launch

class MovieViewModel(
    private val useCase: GetMoviesUseCase,
    private val uploadDocumentsUseCase: UploadDocumentsUseCase,
    private val moviesDbUseCase: MoviesDbUseCase,
) : ViewModel() {

    val getMoviesPopularData: MutableLiveData<ApiState> = MutableLiveData()
    val moviesDbData: MutableLiveData<ApiState> = MutableLiveData()
    val getTopRatedData: MutableLiveData<ApiState> = MutableLiveData()
    val attachments = MutableLiveData<List<String>>(emptyList())
    val _uploadFiles: MutableLiveData<ApiState> = MutableLiveData()

    fun getMoviesPopular(context: Context) = viewModelScope.launch {
        getMoviesPopularData.postValue(ApiState.Loading)
        if(isInternetAvailable(context)) {
            when (val result = useCase.invokePopular()) {
                is GetMoviesUseCaseResult.Error -> {
                    getMoviesPopularData.postValue(ApiState.Failure(Throwable(result.error)))
                }

                is GetMoviesUseCaseResult.Success -> {
                    saveMovies(result.result.results.toMovieEntityList())
                    getMoviesPopularData.postValue(ApiState.Success(result))
                }
            }
        } else {
            getMoviesDb()
        }
    }

    private fun saveMovies(list: List<MovieEntity>) = viewModelScope.launch {
        moviesDbData.postValue(ApiState.Loading)
        when (val result = moviesDbUseCase.invoke(list)) {
            is MoviesDbUseCaseResult.Error -> {
            }

            is MoviesDbUseCaseResult.Success -> {
            }

            else -> {}
        }
    }

    private fun getMoviesDb() = viewModelScope.launch {
        moviesDbData.postValue(ApiState.Loading)
        when (val result = moviesDbUseCase.invokeGetMovies()) {
            is MoviesDbUseCaseResult.Error -> {
                moviesDbData.postValue(ApiState.Failure(Throwable(result.error)))
            }

            is MoviesDbUseCaseResult.SuccessGetMovies -> {
                moviesDbData.postValue(ApiState.Success(result.resultList.toEntityMovieList()))
            }

            else -> {}
        }
    }

    fun getMoviesTopRated() = viewModelScope.launch {
        getTopRatedData.postValue(ApiState.Loading)
        when (val result = useCase.invokeTopRated()) {
            is GetMoviesUseCaseResult.Error -> {
                getTopRatedData.postValue(ApiState.Failure(Throwable(result.error)))
            }

            is GetMoviesUseCaseResult.Success -> {
                getTopRatedData.postValue(ApiState.Success(result))
            }
        }
    }

    suspend fun uploadFileOfDocuments(): List<String> {
        _uploadFiles.postValue(ApiState.Loading)
        val listUrls = arrayListOf<String>()
        attachments.value?.forEachIndexed { index, file ->
            when (val result = uploadDocumentsUseCase.invoke(
                uri = file,
            )) {
                is UploadDocumentsUseCaseResult.Error -> {
                    _uploadFiles.value = ApiState.IsLoading(false)
                    _uploadFiles.value = ApiState.Failure(Throwable(result.error))
                }

                is UploadDocumentsUseCaseResult.Success -> {
                    _uploadFiles.value = ApiState.IsLoading(false)
                    if (result.listUrls.size > 1) {
                        listUrls.addAll(result.listUrls)
                    } else {
                        listUrls.add(result.listUrls.firstOrNull().orEmpty())
                    }
                }
            }
        }
        return listUrls
    }

    fun putImage(list: MutableList<String>) {
        attachments.value = list
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}