package com.users.onboarding.presentation.fragment

import android.content.Intent
import com.users.onboarding.presentation.viewmodel.MovieViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.users.onboarding.data.server.config.ApiState
import com.users.onboarding.databinding.FragmentPhotosBinding
import com.users.onboarding.utils.extensions.collectWhenResumed
import com.users.onboarding.utils.extensions.handleErrorBase
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : BaseFragment() {

    private val viewModel by viewModel<MovieViewModel>()
    private lateinit var binding: FragmentPhotosBinding
    /*private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.data.let {
                       cameraResult(it.toString())
                    }
                }

                Activity.RESULT_CANCELED -> {
                    context?.displayToast("Fue cancelado")
                }

                else -> context?.displayToast("Surgió un problema al adjuntar la imagen.")
            }
        }*/

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val clipData = result.data?.clipData
            val uriList = mutableListOf<String>()
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uriList.add(uri.toString())
                }
            } else {
                result.data?.data?.let { uriList.add(it.toString()) }
            }
            cameraResult(uriList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(layoutInflater)
        /*setFragmentResultListener(CAMERA_RESULT) { _, bundle ->
            val uri = bundle.getString(URI_RESULT).toString()
            // val name = bundle.getString(NAME_RESULT).toString()
          //  cameraResult(uri)
        }*/
        setUpObserver()
        events()
        return binding.root
    }

    private fun setUpObserver() {
        collectWhenResumed(viewModel._uploadFiles) { state ->
            binding.progressBar.isVisible = state == ApiState.Loading
            when (state) {
                is ApiState.Failure -> handleErrorBase(throwable = state) {
                    lifecycleScope.launch {
                        viewModel.uploadFileOfDocuments()
                    }
                }

                is ApiState.Success<*> -> {
                    var success = true
                }

                else -> {}
            }
        }
    }

    private fun executeGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imagePickerLauncher.launch(intent)
    }

    private fun events() {
        with(binding) {
            btnSelectPhotos.setOnClickListener {
                executeGallery()
            }
            btnUploadPhotos.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.uploadFileOfDocuments()
                }
            }
        }
    }

    private fun cameraResult(uri: MutableList<String>) {
        viewModel.putImage(uri)
    }

    companion object {
        const val CAMERA_RESULT = "CAMERA_RESULT"
        const val IMAGE_RESULT = "IMAGE_RESULT"
        const val URI_RESULT = "URI_RESULT"
        const val NAME_RESULT = "NAME_RESULT"
        const val ATTACHMENT_UI = "ATTACHMENT_UI"

        val TAG = this::class.simpleName.toString()
    }
}