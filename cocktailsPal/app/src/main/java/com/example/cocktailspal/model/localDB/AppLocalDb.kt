package com.example.cocktailspal.model.localDB

import androidx.room.Room
import com.example.cocktailspal.MyApplication

object AppLocalDb {
    val appDb: AppLocalDbRepository
        get() = Room.databaseBuilder(
            MyApplication.Companion.myContext!!,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
}
