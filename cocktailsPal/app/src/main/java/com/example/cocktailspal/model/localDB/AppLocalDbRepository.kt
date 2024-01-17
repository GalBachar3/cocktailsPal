package com.example.cocktailspal.model.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailDao
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserDao

@Database(entities = [Cocktail::class, User::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun cocktailDao(): CocktailDao?
    abstract fun userDao(): UserDao?
}
