package org.ferhatozcelik.data.model

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-30
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleResult(
    val id: Int? = null,
    val date: String? = null,
    val author: Int? = null,
    val categories: List<Int>? = null,
    val tags: List<Int>? = null,
    val featured_image_urls: FeaturedImageUrls? = null,
    val title: Rendered? = null,
    val author_info: AuthorInfo? = null,
    val content: Rendered? = null,
    val excerpt: Rendered? = null,
) : Parcelable

@Parcelize
data class Rendered(
    val rendered: String? = null,
) : Parcelable

@Parcelize
data class FeaturedImageUrls(
    val full: List<String>? = null,
    val thumbnail: List<String>? = null,
) : Parcelable

@Parcelize
data class AuthorInfo(
    val name: String? = null,
    val url: String? = null,
) : Parcelable
