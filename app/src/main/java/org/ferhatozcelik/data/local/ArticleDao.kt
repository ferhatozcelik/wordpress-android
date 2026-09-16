package org.ferhatozcelik.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.ferhatozcelik.data.entity.Article

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-28
 */

@Dao
interface ArticleDao {

    @Query("SELECT * FROM article_table ORDER BY articleCreateAtDate DESC")
    fun getArticle(): LiveData<List<Article>>

    @Query("SELECT * FROM article_table WHERE articleId= :articleId LIMIT 1")
    fun getItemById(articleId: Int): LiveData<Article>

    @Query("DELETE FROM article_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(article: List<Article>)

    @Transaction
    suspend fun flushInsert(article: List<Article>) {
        deleteAll()
        insertAll(article)
    }

}