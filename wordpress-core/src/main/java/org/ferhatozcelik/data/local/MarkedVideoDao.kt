package org.ferhatozcelik.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.ferhatozcelik.data.entity.MarkedVideo

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-29
 */

@Dao
interface MarkedVideoDao {

    @Query("SELECT * FROM marked_video_table ORDER BY videoCreateAtDate ASC")
    fun getMarkedVideos(): LiveData<List<MarkedVideo>>

    @Query("SELECT * FROM marked_video_table WHERE videoId= :videoId LIMIT 1")
    fun getItemById(videoId: String?): LiveData<MarkedVideo>

    @Query("SELECT videoId FROM marked_video_table")
    suspend fun getBookmarkArticle(): List<String>

    @Query("DELETE FROM marked_video_table WHERE videoId =:videoId")
    suspend fun deleteById(videoId: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(markedVideo: MarkedVideo)

}