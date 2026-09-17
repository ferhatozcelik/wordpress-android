package org.ferhatozcelik.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-30
 */

@Parcelize
data class VideoResult(
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Items>? = null
) : Parcelable

@Parcelize
data class PageInfo(
    val totalResults: Int? = null,
    val resultsPerPage: Int? = null
) : Parcelable

@Parcelize
data class Items(
    val snippet: Snippet? = null
) : Parcelable

@Parcelize
data class ResourceId(
    val videoId: String? = null,
    val kind: String? = null
) : Parcelable

@Parcelize
data class Snippet(
    val title: String? = null,
    val description: String? = null,
    val thumbnails: Thumbnails? = null,
    val channelTitle: String? = null,
    val publishedAt: String? = null,
    val resourceId: ResourceId? = null
) : Parcelable

@Parcelize
data class Thumbnails(
    val high: High? = null,
) : Parcelable

@Parcelize
data class High(
    val url: String? = null,
) : Parcelable