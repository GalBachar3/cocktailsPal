package com.example.cocktailspal.model.cocktail

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CocktailDao {
    @get:Query("select * from Cocktail")
    val all: LiveData<List<Cocktail?>?>?

    @Query("select * from Cocktail where name = :cocktailId")
    fun getCocktailById(cocktailId: String?): Cocktail?

    @Query("SELECT COUNT(*) FROM Cocktail Where userId = :userId")
    fun countCocktailByUser(userId: String?): Int?

    @Query("SELECT * FROM Cocktail Where name = :name")
    fun findByName(name: String?): Cocktail?

    @Query("SELECT * FROM Cocktail Where userId = :userId")
    fun getAllCocktailsByUser(userId: String?): LiveData<List<Cocktail?>?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg cocktails: Cocktail)
}
