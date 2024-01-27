package com.example.cocktailspal.model.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailDao
import com.example.cocktailspal.model.user.User

@Database(entities = [Cocktail::class, User::class], version = 61)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun cocktailDao(): CocktailDao?
}
