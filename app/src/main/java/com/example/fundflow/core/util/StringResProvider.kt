package com.example.fundflow.core.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface untuk mengakses string resource dari luar Context
 * (misalnya di ViewModel atau UseCase).
 *
 * Manfaat: ViewModel tetap bisa menampilkan string yang di-localize
 * tanpa harus memegang referensi Context secara langsung.
 */
interface StringResProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

@Singleton
class StringResProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StringResProvider {

    override fun getString(resId: Int): String =
        context.getString(resId)

    override fun getString(resId: Int, vararg formatArgs: Any): String =
        context.getString(resId, *formatArgs)
}
