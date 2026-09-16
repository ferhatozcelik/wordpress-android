package org.ferhatozcelik.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.ferhatozcelik.data.entity.Video

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-29
 */

@Dao
interface VideoDao {

    @Query("SELECT * FROM video_table")
    fun getVideo(): LiveData<List<Video>>

    @Query("SELECT * FROM video_table WHERE videoId= :videoId LIMIT 1")
    fun getItemById(videoId: String?): LiveData<Video>

    @Query("DELETE FROM video_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(video: List<Video>)

    @Transaction
    suspend fun flushInsert(video: List<Video>) {
        deleteAll()
        insertAll(video)
    }

}