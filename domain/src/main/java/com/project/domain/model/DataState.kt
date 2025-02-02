package com.project.domain.model

sealed class DataState<T>(val data: T? = null, val message: String? = null, val code: Int? = null) {
    class Success<T>(data: T?) : DataState<T>(data)

    class Error<T>(code: Int, message: String, data: T? = null) : DataState<T>(data, message, code)

    class Exception<T>(val e: Throwable) : DataState<T>()

    class Loading<T>(val isLoading: Boolean = true) : DataState<T>(null)
}
