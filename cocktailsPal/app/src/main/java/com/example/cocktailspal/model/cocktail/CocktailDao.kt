package com.example.cocktailspal.model.cocktail

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CocktailDao {
    @Query("select * from cocktails")
    fun getAll(): LiveData<List<Cocktail?>?>?

    @Query("SELECT * FROM cocktails WHERE name = :name")
    fun getCocktailById(name: String): Cocktail?

    @Query("SELECT COUNT(*) FROM cocktails Where userId = :userId")
    fun countCocktailByUser(userId: String?): Int?

    @Query("SELECT * FROM cocktails Where name = :name")
    fun findByName(name: String?): Cocktail?

    @Query("SELECT * FROM cocktails Where userId = :userId")
    fun getAllCocktailsByUser(userId: String?): LiveData<List<Cocktail?>?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg cocktails: Cocktail)
}
