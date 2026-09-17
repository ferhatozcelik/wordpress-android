package org.ferhatozcelik.ui.fragments.videos

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.ferhatozcelik.BuildConfig
import org.ferhatozcelik.data.entity.Video
import org.ferhatozcelik.repository.VideoRepository
import org.ferhatozcelik.util.NetworkUtil
import org.ferhatozcelik.util.StringExtension.Companion.htmlToString
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 *
 * @author ferhatozcelik
 * @since 2023-04-02
 */

@HiltViewModel
class VideosViewModel @Inject constructor(private val videoRepository: VideoRepository, @ApplicationContext private val context: Context) : ViewModel() {

    var isProgressBarStatus: MutableLiveData<Boolean> = MutableLiveData()
    var totalResults: MutableLiveData<Int> = MutableLiveData()
    var nextPageToken: String? = null

    fun getVideo() = videoRepository.getVideo()

    fun getVideoData(pageToken: String?, flush: Boolean) = viewModelScope.launch {
        if (flush){
            isProgressBarStatus.postValue(true)
        }
        safeArticleData(pageToken, flush)
    }

    fun saveBookmarks(video: Video) = viewModelScope.launch {
        videoRepository.insertBookmark(video)
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun safeArticleData(pageToken: String?, flush: Boolean) {
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = videoRepository.getVideos(BuildConfig.YOUTUBE_API_KEY, BuildConfig.PLAYLIST_ID, pageToken)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        nextPageToken = resultResponse.nextPageToken
                        totalResults.postValue(resultResponse.pageInfo?.totalResults!!)
                        val tempList = mutableListOf<Video>()


                        for (item in resultResponse.items!!) {
                            val snippet = item.snippet ?: continue
                            val videoId = snippet.resourceId?.videoId ?: continue
                            val isBookmarks = videoId in videoRepository.getBookmarkArticle()
                            tempList.add(
                                Video(
                                    videoId = videoId,
                                    videoTitle = snippet.title?.htmlToString(),
                                    videoDescription = snippet.description?.htmlToString(),
                                    videoUrl = "https://www.youtube.com/watch?v=" + videoId,
                                    videoThumbnailUrl = snippet.thumbnails?.high?.url!!,
                                    channelTitle = snippet.channelTitle,
                                    isBookmark = isBookmarks,
                                    videoCreateAtDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(snippet.publishedAt.toString()
                                    )
                                )
                            )
                        }

                        videoRepository.flushArticleInsert(tempList.toList(), flush)
                        isProgressBarStatus.postValue(false)
                    }
                }
            } else {
                isProgressBarStatus.postValue(false)
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
            isProgressBarStatus.postValue(false)
            when (ex) {
                is IOException -> {
                    Toast.makeText(context, "IOException: " + ex.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Exception: " + ex.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}