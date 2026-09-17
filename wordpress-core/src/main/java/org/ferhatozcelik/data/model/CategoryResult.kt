package org.ferhatozcelik.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-30
 */

@Parcelize
data class CategoryResult(
    val id: Int? = null,
    val count: Int? = null,
    val name: String? = null,
    val description: String? = null
) : Parcelable

