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
@Entity(tableName = "category_table")
data class Categories(
    @TypeConverters(Converters::class)
    @PrimaryKey(autoGenerate = true)
    var categoryId: Int? = null,
    val categoryCount: Int? = null,
    val categoryName: String? = null,
    val categoryDescription: String? = null,
    var isActive: Boolean = false,
    val createAt: Long = System.currentTimeMillis()) : Parcelable