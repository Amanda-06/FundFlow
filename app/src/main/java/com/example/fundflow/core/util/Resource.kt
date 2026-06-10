package com.example.fundflow.core.util

/**
 * Sealed class untuk merepresentasikan hasil operasi async
 * (network call, database query, dsb).
 *
 * Pemakaian di ViewModel:
 *   _uiState.update { it.copy(isLoading = true) }
 *   when (val result = useCase()) {
 *       is Resource.Success -> _uiState.update { it.copy(data = result.data, isLoading = false) }
 *       is Resource.Error   -> _uiState.update { it.copy(error = result.message, isLoading = false) }
 *       is Resource.Loading -> Unit
 *   }
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}

/** Shortcut ekstensi untuk memeriksa state */
val <T> Resource<T>.isLoading: Boolean get() = this is Resource.Loading
val <T> Resource<T>.isSuccess: Boolean get() = this is Resource.Success
val <T> Resource<T>.isError:   Boolean get() = this is Resource.Error

/** Ambil data jika Success, atau null */
fun <T> Resource<T>.dataOrNull(): T? = (this as? Resource.Success)?.data

/** Ambil pesan error jika Error, atau null */
fun <T> Resource<T>.errorMessageOrNull(): String? = (this as? Resource.Error)?.message
