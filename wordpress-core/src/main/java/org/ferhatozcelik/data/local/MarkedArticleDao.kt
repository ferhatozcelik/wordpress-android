package org.ferhatozcelik.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.ferhatozcelik.data.entity.MarkedArticle

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-29
 */

@Dao
interface MarkedArticleDao {

    @Query("SELECT * FROM marked_article_table ORDER BY articleCreateAtDate DESC")
    fun getMarkedArticle(): LiveData<List<MarkedArticle>>

    @Query("SELECT articleId FROM marked_article_table")
    suspend fun getBookmarkArticle(): List<Int>

    @Query("SELECT * FROM marked_article_table WHERE articleId= :articleId LIMIT 1")
    fun getItemById(articleId: Int): LiveData<MarkedArticle>

    @Query("DELETE FROM marked_article_table WHERE articleId =:articleId")
    suspend fun deleteById(articleId: Int?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(markedArticle: MarkedArticle)

}