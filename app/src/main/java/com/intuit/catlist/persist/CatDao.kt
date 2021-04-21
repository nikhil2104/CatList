package com.intuit.catlist.persist

import androidx.paging.DataSource
import androidx.room.*
import com.intuit.catlist.model.Cat

@Dao
abstract class CatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCats(cats: List<Cat>)

   /* @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertBreeds(breeds: List<Breed>)

    private suspend fun insertBreedForCats(cats: List<Cat>) {
        val breedList = mutableListOf<Breed>()
        cats.forEach { cat ->
            cat.breeds?.forEach {
                it.catId = cat.id
                breedList.add(it)
            }
        }
        insertBreeds(breedList)
    }*/

    @Transaction
    @Query("Select * from cat")
    abstract fun getCatFactory() : DataSource.Factory<Int, Cat>

    @Transaction
    @Query("Select * from cat")
    abstract suspend fun getCats() : List<Cat>

    @Transaction
    @Query("Select * from cat where id=:id")
    abstract suspend fun getCat(id: String) : Cat

    @Query("Delete from cat")
    abstract suspend fun deleteAllCats()

    /*@Query("Delete from breed")
    abstract suspend fun deleteAllBreeds()

    open suspend fun deleteAll() {
        deleteAllCats()
        deleteAllBreeds()
    }

    open suspend fun insertIntoDb(catList: List<Cat>) {
        insertBreedForCats(catList)
        insertCats(catList)
    }*/
}