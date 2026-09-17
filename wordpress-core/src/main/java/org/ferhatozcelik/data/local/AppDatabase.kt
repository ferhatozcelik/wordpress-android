package org.ferhatozcelik.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.ferhatozcelik.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import org.ferhatozcelik.data.entity.*
import javax.inject.Inject
import javax.inject.Provider

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-28
 */

@Database(entities = [Article::class, MarkedArticle::class, MarkedVideo::class, Categories::class, Video::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    abstract fun getVideoDao(): VideoDao

    abstract fun getMarkedArticleDao(): MarkedArticleDao

    abstract fun getMarkedVideoDao(): MarkedVideoDao

    abstract fun getCategoryDao(): CategoryDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()

}