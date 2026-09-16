package org.ferhatozcelik.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.ferhatozcelik.data.entity.Categories

/**
 *
 * @author ferhatozcelik
 * @since 2023-03-29
 */

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table ORDER BY categoryName ASC")
    fun getCategories(): LiveData<List<Categories>>

    @Query("UPDATE category_table SET isActive=:isActive")
    suspend fun updateResetActive(isActive: Boolean)

    @Query("SELECT * FROM category_table WHERE isActive=:isActive")
    suspend fun setCategoriesActive(isActive: Boolean): List<Categories>

    @Query("SELECT * FROM category_table WHERE categoryId= :categoryId LIMIT 1")
    suspend fun getItemById(categoryId: Int): Categories?

    @Query("DELETE FROM category_table")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: Categories)

    @Update
    suspend fun update(categories: Categories)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Categories>)

    @Query("SELECT * FROM category_table WHERE categoryId= :categoryId")
    suspend fun getItemByIdList(categoryId: Int): List<Categories>

    @Transaction
    suspend fun insertOrUpdate(categories: List<Categories>) {
        deleteAll()
        for (it in categories) {
            val category: List<Categories> = getItemByIdList(it.categoryId!!)
            if (category.isEmpty()) {
                insert(it)
            } else {
                update(it)
            }
        }
    }

}