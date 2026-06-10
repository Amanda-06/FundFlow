package com.example.fundflow.core.network

import com.example.fundflow.core.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Suspend wrapper yang membungkus pemanggilan Retrofit/API
 * ke dalam Resource<T> (Loading / Success / Error).
 *
 * Contoh pemakaian di RemoteDataSource:
 *   val result = safeApiCall { apiService.getPublicHolidays(year, "ID") }
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Resource<T> = withContext(Dispatchers.IO) {
    try {
        Resource.Success(apiCall())
    } catch (e: HttpException) {
        val message = when (e.code()) {
            400  -> "Permintaan tidak valid (400)"
            401  -> "Tidak terautentikasi (401)"
            403  -> "Akses ditolak (403)"
            404  -> "Data tidak ditemukan (404)"
            500  -> "Kesalahan server (500)"
            else -> "Terjadi kesalahan HTTP: ${e.code()}"
        }
        Resource.Error(message)
    } catch (e: IOException) {
        Resource.Error("Tidak ada koneksi internet. Periksa jaringan Anda.")
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Terjadi kesalahan tak terduga.")
    }
}
