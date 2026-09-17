package org.ferhatozcelik.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.ferhatozcelik.data.local.*
import javax.inject.Singleton

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-31
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: AppDatabase.Callback): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "local_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideArticleDao(db: AppDatabase): ArticleDao {
        return db.getArticleDao()
    }

    @Provides
    fun provideVideoDao(db: AppDatabase): VideoDao {
        return db.getVideoDao()
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao {
        return db.getCategoryDao()
    }

    @Provides
    fun provideMarkedArticleDao(db: AppDatabase): MarkedArticleDao {
        return db.getMarkedArticleDao()
    }

    @Provides
    fun provideMarkedVideoDao(db: AppDatabase): MarkedVideoDao {
        return db.getMarkedVideoDao()
    }
}