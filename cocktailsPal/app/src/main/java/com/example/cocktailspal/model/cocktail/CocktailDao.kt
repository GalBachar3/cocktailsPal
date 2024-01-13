package com.example.cocktailspal.model.cocktail

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cocktailspal.model.cocktail.Recipe

@Dao
interface RecipeDao {
    @get:Query("select * from Recipe")
    val all: LiveData<List<Recipe?>?>?

    @Query("select * from Recipe where id = :recipeId")
    fun getRecipeById(recipeId: String?): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg recipes: Recipe?)

    @Delete
    fun delete(recipe: Recipe?)
}
