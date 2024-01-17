package com.example.cocktailspal.model.localDB

import androidx.room.Room
import com.example.cocktailspal.MyApplication

object AppLocalDb {
    val appDb: AppLocalDbRepository?
        get() = MyApplication.Companion.myContext?.applicationContext?.let {
            Room.databaseBuilder(
                it,
                AppLocalDbRepository::class.java,
                "dbFileName.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
}