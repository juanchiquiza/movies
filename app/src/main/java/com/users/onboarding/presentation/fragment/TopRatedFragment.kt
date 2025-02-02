package com.users.onboarding.presentation.fragment

import com.users.onboarding.presentation.viewmodel.MovieViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.users.onboarding.data.server.config.ApiState
import com.users.onboarding.databinding.FragmentTopRatedBinding
import com.users.onboarding.domain.model.MoviePopular
import com.users.onboarding.domain.usecases.GetMoviesUseCaseResult
import com.users.onboarding.presentation.adapters.MovieAdapter
import com.users.onboarding.utils.extensions.collectWhenResumed
import com.users.onboarding.utils.extensions.handleErrorBase
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopRatedFragment : BaseFragment() {

    private val viewModel by viewModel<MovieViewModel>()
    private lateinit var binding: FragmentTopRatedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopRatedBinding.inflate(layoutInflater)
        setUpObserver()
        return binding.root
    }

    private fun setUpObserver() {
        viewModel.getMoviesTopRated()
        collectWhenResumed(viewModel.getTopRatedData) { state ->
            binding.progressBar.isVisible = state == ApiState.Loading
            when (state) {

                is ApiState.Failure -> handleErrorBase(throwable = state) {
                    viewModel.getMoviesTopRated()
                }

                is ApiState.Success<*> -> {
                    val result = (state.data as GetMoviesUseCaseResult.Success).result
                    setUpRecycler(result)
                }

                else -> {}
            }
        }
    }

    private fun setUpRecycler(movie: MoviePopular?) {
        val adapter = movie?.results?.let {
            MovieAdapter(it)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = adapter
    }
}