package com.users.onboarding.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.users.onboarding.databinding.DialogGeneralErrorBinding
import com.users.onboarding.utils.extensions.viewLifecycle

class GeneralErrorDialog(
    private val retry: (() -> Unit)? = null,
) : DialogFragment() {
    private var binding: DialogGeneralErrorBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGeneralErrorBinding.inflate(layoutInflater)
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        events()
    }

    private fun events() {
        with(binding) {
            buttonAccept.setOnClickListener {
                dismiss()
            }
            buttonRetry.setOnClickListener {
                dismiss()
                retry?.invoke()
            }
        }
    }

    companion object {
        fun newInstance(retry: () -> Unit) =
            GeneralErrorDialog(retry = retry)

        val TAG = GeneralErrorDialog::class.simpleName.toString()
    }
}