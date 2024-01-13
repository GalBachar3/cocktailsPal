package com.example.cocktailspal.model.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cocktailspal.model.user.User

@Dao
interface UserDao {
    @get:Query("select * from User")
    val all: LiveData<List<User?>?>?

    @Query("select * from User where id = :userId")
    fun getRecipeById(userId: String?): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User?)

    @Delete
    fun delete(user: User?)
}
