package com.example.cocktailspal.model.cocktail

import android.content.Context
import android.content.SharedPreferences
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.cocktailspal.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable
import kotlin.random.Random

@Entity("cocktails")
data class Cocktail(
    var name: String = "",
    var category: String? = "",
    var instructions: String? = "",
    var imgUrl: String? = "",
    var userId: String? = "",
    var username: String? = "",
    var lastUpdated: Long? = null,
    @PrimaryKey
    var id: String = "",
    var ingredients: String? = "" )
    : Serializable {
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var photo: ByteArray? = null
    @Ignore
    constructor(
        name: String,
        category: String?,
        instructions: String?,
        userId: String?,
        userName: String?,
        ingredients: String?) : this() {
        this.name = name
        this.category = category
        this.instructions = instructions
        this.userId = userId
        this.username = userName
        this.ingredients = ingredients
    }

    @Ignore
    constructor(
        name: String,
        category: String?,
        instructions: String?,
        userId: String?,
        imgUrl: String?,
        userName: String?,
        ingredients: String?
    ) : this() {
        this.name = name
        this.category = category
        this.instructions = instructions
        this.imgUrl = imgUrl
        this.userId = userId
        this.username = userName
        this.ingredients = ingredients
    }

    @Ignore
    constructor(
        name: String,
        category: String?,
        instructions: String?,
        userId: String?,
        ingredients: String?
    ) : this() {
        this.name = name
        this.category = category
        this.instructions = instructions
        this.userId = userId
        this.ingredients = ingredients
    }

    @Ignore
    constructor(
        name: String,
        category: String?,
        instructions: String?,
        ingredients: String?
        ) : this() {
        this.name = name
        this.category = category
        this.instructions = instructions
        this.ingredients = ingredients
    }

    fun toJson(): Map<String, Any?> {
        val json: MutableMap<String, Any?> = HashMap()
        json[ID] = id
        json[NAME] = name
        json[CATEGORY] = this.category
        json[INSTRUCTIONS] = instructions
        json[IMG_URL] = imgUrl
        json[INGREDIENTS] =  ingredients
        json[LAST_UPDATED] = FieldValue.serverTimestamp()
        json[USER_ID] = userId
        json[USERNAME] = username
        return json
    }

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val CATEGORY = "category"
        const val INSTRUCTIONS = "instructions"
        const val IMG_URL = "imgUrl"
        const val INGREDIENTS = "ingredients"
        const val COLLECTION = "cocktails"
        const val USER_ID = "userId"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "recipes_local_last_update"
        const val USERNAME = "username"
        fun fromJson(json: Map<String?, Any?>): Cocktail {
            val id = json[ID] as String
            val name = json[NAME] as String
            val category = json[CATEGORY] as String?
            val instructions = json[INSTRUCTIONS] as String?
            val imgUrl = json[IMG_URL] as String?
            val userId = json[USER_ID] as String?
            val userName = json[USERNAME] as String?
            val ingredients = json[INGREDIENTS] as String?
            val cocktail = Cocktail( name, category, instructions, userId,imgUrl, userName, ingredients)
            cocktail.id = id
            try {
                val time = json[LAST_UPDATED] as Timestamp?
                cocktail.lastUpdated = time!!.seconds
            } catch (e: Exception) {
            }
            return cocktail
        }

        var localLastUpdate: Long?
            get() {
                val sharedPref: SharedPreferences? =
                    MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                return sharedPref?.getLong(LOCAL_LAST_UPDATED, 0)
            }
            set(time) {
                MyApplication.getAppContext().let { context ->
                    val sharedPref: SharedPreferences =
                        context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPref.edit()

                    time?.let {
                        editor.putLong(LOCAL_LAST_UPDATED, it)
                        editor.apply() // Use apply() for asynchronous write
                    }
                }
            }
    }
}
