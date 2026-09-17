package org.ferhatozcelik.data.model

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-29
 */

sealed class APIResponse<T>(
    val result: T? = null
) {
    class Success<T>(result: T? = null) : APIResponse<T>(result)
    class Error<T>(result: T? = null) : APIResponse<T>(result)
    class Loading<T> : APIResponse<T>()
}