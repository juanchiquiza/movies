package com.users.onboarding.presentation.fragment

import com.users.onboarding.databinding.FragmentHomeBinding
import com.users.onboarding.presentation.viewmodel.MovieViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.users.onboarding.R
import com.users.onboarding.data.server.config.ApiState
import com.users.onboarding.domain.model.MoviePopular
import com.users.onboarding.domain.usecases.GetMoviesUseCaseResult
import com.users.onboarding.presentation.adapters.MovieAdapter
import com.users.onboarding.utils.extensions.collectWhenResumed
import com.users.onboarding.utils.extensions.handleErrorBase
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel by viewModel<MovieViewModel>()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setUpObserver()
        setupView()
        return binding.root
    }

    private fun setUpObserver() {
        viewModel.getMoviesPopular()
        collectWhenResumed(viewModel.getMoviesPopularData) { state ->
            binding.progressBar.isVisible = state == ApiState.Loading
            when (state) {

                is ApiState.Failure -> handleErrorBase(throwable = state) {
                    viewModel.getMoviesPopular()
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

    private fun setupView() {
        Glide
            .with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/users-36ebf.appspot.com/o/photos%2F4164.jpg?alt=media&token=64b856da-2d47-4cab-9a7b-e6c5e9c5d25a")
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.profileImage)
    }
}