package org.ferhatozcelik.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.ferhatozcelik.data.local.Converters
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-28
 */

@Parcelize
@Entity(tableName = "video_table")
data class Video(
    @TypeConverters(Converters::class)
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var videoId: String? = null,
    val videoTitle: String?,
    val videoDescription: String?,
    val videoUrl: String?,
    val channelTitle: String?,
    val videoThumbnailUrl: String,
    val isBookmark: Boolean?,
    val videoCreateAtDate: Date?
) : Parcelable