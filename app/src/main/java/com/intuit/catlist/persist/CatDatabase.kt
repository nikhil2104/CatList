package com.intuit.catlist.persist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intuit.catlist.model.Breed
import com.intuit.catlist.model.Cat

@Database(
    entities = [Cat::class],
    version = 1,
    exportSchema = false
)
abstract class CatDatabase: RoomDatabase() {
    companion object {
        private var database: CatDatabase?= null
        fun getInstance(context: Context): CatDatabase {
            if (database == null) {
                return Room.databaseBuilder(context.applicationContext, CatDatabase::class.java, "cat.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }

    abstract fun getCatDao(): CatDao
}