package org.ferhatozcelik.repository

import org.ferhatozcelik.data.entity.MarkedVideo
import org.ferhatozcelik.data.entity.Video
import org.ferhatozcelik.data.local.MarkedVideoDao
import org.ferhatozcelik.data.local.VideoDao
import org.ferhatozcelik.data.model.VideoResult
import org.ferhatozcelik.data.remote.AppApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-31
 */

@Singleton
class VideoRepository @Inject constructor(private val appApi: AppApi, private val videoDao: VideoDao,
                                          private val markedVideoDao: MarkedVideoDao) {

    /**
     *  Video
     */

    fun getVideo() = videoDao.getVideo()

    fun getMarkedVideos() = markedVideoDao.getMarkedVideos()

    fun getVideoById(id: String?) = videoDao.getItemById(id)

    fun getMarkedVideoById(id: String?) = markedVideoDao.getItemById(id)

    suspend fun flushArticleInsert(article: List<Video>, flush: Boolean) {
        if (flush){
            videoDao.flushInsert(article)
        }else{
            videoDao.insertAll(article)
        }
    }

    suspend fun getVideos(key: String, playListId: String, pageToken: String?): Response<VideoResult> {
        return appApi.getVideos(key, playListId, pageToken)
    }

    /**
     *  Video Bookmarks
     */

    suspend fun getBookmarkArticle() = markedVideoDao.getBookmarkArticle()

    suspend fun removeVideosBookmarks(markedVideo: MarkedVideo) {
        if (markedVideo.isBookmark == true) {
            markedVideoDao.deleteById(markedVideo.videoId)
        }
    }

    suspend fun insertBookmark(video: Video) {
        if (video.isBookmark == true) {
            markedVideoDao.deleteById(video.videoId)
            videoDao.update(video.copy(isBookmark = false))
        } else {
            markedVideoDao.insert(
                MarkedVideo(
                    videoId = video.videoId,
                    videoTitle = video.videoTitle,
                    videoDescription = video.videoDescription,
                    videoUrl = video.videoUrl,
                    channelTitle= video.channelTitle,
                    isBookmark = true,
                    videoThumbnailUrl= video.videoThumbnailUrl,
                    videoCreateAtDate= video.videoCreateAtDate
                )
            )
            videoDao.update(video.copy(isBookmark = true))
        }
    }

}