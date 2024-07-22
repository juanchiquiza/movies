package com.users.onboarding.utils.extensions

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import com.google.gson.JsonSyntaxException
import com.users.onboarding.R
import com.users.onboarding.data.server.config.ApiState
import com.users.onboarding.presentation.dialogs.GeneralErrorDialog
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> Fragment.collectWhenResumed(state: MutableLiveData<T>, onCollect: (T) -> Unit) {
    state.observe(viewLifecycleOwner) {
        onCollect.invoke(it)
    }
}

fun Fragment.handleErrorBase(throwable: ApiState.Failure, actionRetry: (() -> Unit)? = null) {
    activity?.handleErrorBase(
        throwable = throwable,
        actionRetry = actionRetry,
    )
}

fun FragmentActivity.handleErrorBase(
    throwable: ApiState.Failure,
    actionRetry: (() -> Unit)? = null
) {
    when (throwable.msg) {
        is HttpException -> {
            val message = try {
                throwable.msg.response()?.errorBody().toApiError()?.toString()
            } catch (e: JsonSyntaxException) {
                throwable.msg.response()?.errorBody().toApiError()?.toString()
            } catch (e: JsonSyntaxException) {
                getString(R.string.general_error_oops)
            }

            showGeneralError(actionRetry = actionRetry)
        }

        is UnknownHostException -> {
            val message = getString(R.string.general_error_oops)
            showGeneralError(actionRetry = actionRetry)
        }

        is TimeoutException, is SocketTimeoutException -> {
            val message = getString(R.string.general_error_oops)
            showGeneralError(actionRetry = actionRetry)
        }

        else -> {
            when (val message =
                getMessageByContent(throwable.msg.message, context = applicationContext)) {
                getString(R.string.general_error_oops) -> {
                    showGeneralError(
                        actionRetry = actionRetry,
                    )
                }

                else -> {
                    showGeneralError(
                        actionRetry = actionRetry,
                    )
                }
            }
        }
    }

}

fun FragmentActivity.showGeneralError(
    actionRetry: (() -> Unit)? = null,
) {

    if (supportFragmentManager.fragments.firstOrNull { it.tag == GeneralErrorDialog.TAG } == null) {
        GeneralErrorDialog.newInstance(
            retry = { actionRetry?.invoke() },
        ).show(supportFragmentManager, GeneralErrorDialog.TAG)
    }
}

fun getMessageByContent(message: String?, context: Context): String? {
    return if (message.contentEquals("timeout") || message.contentEquals("Time-out")) {
        context.getString(R.string.general_error_oops)
    } else if (!message.isNullOrEmpty() && message.isDigitsOnly()) {
        context.getString(R.string.general_error_oops)
    } else {
        message
    }
}

fun <T> Fragment.viewLifecycle(): ReadWriteProperty<Fragment, T> =
    object : ReadWriteProperty<Fragment, T>, DefaultLifecycleObserver {

        private var binding: T? = null

        init {
            // Observe the view lifecycle of the Fragment.
            // The view lifecycle owner is null before onCreateView and after onDestroyView.
            // The observer is automatically removed after the onDestroy event.
            this@viewLifecycle
                .viewLifecycleOwnerLiveData
                .observe(this@viewLifecycle, Observer { owner: LifecycleOwner? ->
                    owner?.lifecycle?.addObserver(this)
                })
        }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }

        override fun getValue(
            thisRef: Fragment,
            property: KProperty<*>
        ): T {
            return this.binding ?: error("Called before onCreateView or after onDestroyView.")
        }

        override fun setValue(
            thisRef: Fragment,
            property: KProperty<*>,
            value: T
        ) {
            this.binding = value
        }
    }

