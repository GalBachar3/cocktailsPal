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

    @Query("select * from Cocktail where id = :cocktailId")
    fun getCocktailById(cocktailId: String?): Cocktail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cocktails: Cocktail?)

    @Delete
    fun delete(cocktail: Cocktail?)
}
