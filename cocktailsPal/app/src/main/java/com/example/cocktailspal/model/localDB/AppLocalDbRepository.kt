package com.example.cocktailspal.model.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cocktailspal.model.cocktail.Recipe
import com.example.cocktailspal.model.cocktail.RecipeDao
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserDao

@Database(entities = [Recipe::class, User::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao?
    abstract fun userDao(): UserDao?
}
