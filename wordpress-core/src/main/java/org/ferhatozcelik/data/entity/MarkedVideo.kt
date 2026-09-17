package org.ferhatozcelik.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import org.ferhatozcelik.data.local.Converters
import java.util.*

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-28
 */

@Parcelize
@Entity(tableName = "marked_video_table")
data class MarkedVideo(
    @TypeConverters(Converters::class)
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var videoId: String? = null,
    val videoTitle: String?,
    val videoDescription: String?,
    val videoUrl: String?,
    val channelTitle: String?,
    val videoThumbnailUrl: String,
    val isBookmark: Boolean?,
    val videoCreateAtDate: Date?
) : Parcelable