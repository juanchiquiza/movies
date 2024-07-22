package com.users.onboarding.utils.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.users.onboarding.data.remote.ApiError
import okhttp3.ResponseBody
import java.text.Normalizer

fun ResponseBody?.toApiError(): ApiError? {
    return Gson().fromJson(this?.string(), ApiError::class.java)
}


fun String.unAccent(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace("[^\\p{ASCII}]".toRegex(), "")
}
inline fun <reified T> String.fromJson(): List<T> = Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)
inline fun <reified T> String.fromJsonObjet(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)