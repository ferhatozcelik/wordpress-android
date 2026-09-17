package org.ferhatozcelik.ui.fragments.article

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.ferhatozcelik.data.entity.Article
import org.ferhatozcelik.data.entity.Categories
import org.ferhatozcelik.repository.ArticleRepository
import org.ferhatozcelik.util.NetworkUtil.Companion.hasInternetConnection
import org.ferhatozcelik.util.StringExtension.Companion.htmlToString
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 *
 * @author ferhatozcelik
 * @since 2023-04-01
 */

@HiltViewModel
class ArticleViewModel @Inject constructor(private val articleRepository: ArticleRepository, @ApplicationContext private val context: Context) : ViewModel() {

    var listResults: List<Categories> = emptyList()
    var searchQuery: String? = null
    var categoriesQuery: String? = null
    var isProgressBarStatus: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getCategoriesData()
    }

    fun getArticle() = articleRepository.getArticle()

    fun getCategories() = articleRepository.getCategories()

    fun getArticleData(articlePage: Int, flush: Boolean) = viewModelScope.launch {
        if (flush) {
            isProgressBarStatus.postValue(true)
        }
        safeArticleData(articlePage, flush)
    }

    fun getCategoriesData() = viewModelScope.launch {
        safeCategoriesData()
    }

    fun categoryQuery(currentPage: Int, categoryId: Int?) = viewModelScope.launch {
        isProgressBarStatus.postValue(true)
        listResults = articleRepository.getCategoryActive(categoryId)
        val tempList = mutableListOf<Int>()
        tempList.clear()
        categoriesQuery = null
        if (listResults.isNotEmpty() && listResults[0].categoryId != 0) {
            for (i in listResults.indices) {
                tempList.add(listResults[i].categoryId!!)
            }
            categoriesQuery = if (tempList.isNotEmpty()) {
                tempList.toString().trim().replace("[", "").replace("]", "")
            } else {
                null
            }
        }
        safeArticleData(currentPage, true)

    }

    fun searchQuery(currentPage: Int, searchText: String?) = viewModelScope.launch {
        isProgressBarStatus.postValue(true)
        searchQuery = searchText
        safeArticleData(currentPage, true)
    }

    fun saveBookmarks(article: Article) = viewModelScope.launch {
        articleRepository.insertBookmark(article)
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun safeArticleData(articlePage: Int, flush: Boolean) {
        try {
            if (hasInternetConnection(context)) {
                val response = articleRepository.getArticle(articlePage, categoriesQuery, searchQuery)



                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val tempList = mutableListOf<Article>()
                        for (item in resultResponse) {
                            if (item.id != null) {
                                val categoryList = mutableListOf<Categories>()
                                if (item.categories != null) {
                                    for (categoryId in item.categories) {
                                        val categoriesItem = articleRepository.getCategory(categoryId)
                                        if (categoriesItem != null) {
                                            categoryList.add(categoriesItem)
                                        }
                                    }
                                }

                                val isBookmarks = item.id in articleRepository.getBookmarkArticle()

                                tempList.add(
                                    Article(
                                        articleId = item.id,
                                        articleTitle = item.title?.rendered?.htmlToString()?.replace("\\n", "")?.replace("\\\"", ""),
                                        articleCategory = categoryList,
                                        authorId = item.author,
                                        authorName = item.author_info?.name.toString(),
                                        authorUrl = item.author_info?.url.toString(),
                                        articleContent = item.content?.rendered.toString(),
                                        articleImageUrl = item.featured_image_urls?.full?.get(0).toString(),
                                        isBookmark = isBookmarks,
                                        articleCreateAtDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.date.toString()
                                        )
                                    )
                                )
                            }
                            articleRepository.flushArticleInsert(tempList.toList(), flush)
                        }
                        isProgressBarStatus.postValue(false)
                    }
                }
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                isProgressBarStatus.postValue(false)
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

    private suspend fun safeCategoriesData() {
        try {
            if (hasInternetConnection(context)) {
                val response = articleRepository.getAPICategories()
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val tempList = mutableListOf<Categories>()
                        tempList.add(
                            Categories(
                                categoryId = 0,
                                categoryCount = 0,
                                categoryName = "All Article",
                                categoryDescription = "All Article",
                                isActive = true,
                                createAt = System.currentTimeMillis()
                            )
                        )
                        for (item in resultResponse) {
                            val count = item.count
                            if (count != null && count > 0) {
                                tempList.add(
                                    Categories(
                                        categoryId = item.id,
                                        categoryCount = count,
                                        categoryName = item.name,
                                        categoryDescription = item.description,
                                        isActive = false,
                                        createAt = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                        articleRepository.insertOrUpdate(tempList)
                    }
                }
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
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