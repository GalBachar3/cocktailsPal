package com.example.cocktailspal.model.cocktail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CocktailDao {
    @Query("select * from Cocktail")
    fun getAll(): LiveData<List<Cocktail>>

    @Query("SELECT * FROM Cocktail WHERE name = :name")
    fun getCocktailById(name: String): Cocktail?

    @Query("SELECT COUNT(*) FROM Cocktail Where userId = :userId")
    fun countCocktailByUser(userId: String?): Int?

    @Query("SELECT * FROM Cocktail Where name = :name")
    fun findByName(name: String?): Cocktail?

    @Query("SELECT * FROM Cocktail Where userId = :userId")
    fun getAllCocktailsByUser(userId: String?): LiveData<List<Cocktail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cocktails: Cocktail)

    @Delete
    fun delete(cocktail: Cocktail)
}
