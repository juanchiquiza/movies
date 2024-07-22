package com.users.onboarding.data.server.config

sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    class Warning(val msg: Throwable) : ApiState()
    class Success<T>(val data: T) : ApiState()
    object Empty : ApiState()
    class DynamicMessage(val msg: String? = null) : ApiState()
    data class IsLoading(val status: Boolean) : ApiState()
}