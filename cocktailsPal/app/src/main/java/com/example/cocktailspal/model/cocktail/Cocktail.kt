package com.example.cocktailspal.model.cocktail

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cocktailspal.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

@Entity
class Cocktail: Serializable {
    private val serialVersionUID = 1L
    @PrimaryKey
    var name: String? = ""
    var category: String? = ""
    var area: String? = ""
    var instructions: String? = ""
    var imgUrl: String? = ""
    var userId: String? = ""
    var userName: String? = ""

    //    public List<String> getIngredients() {
    //        return ingredients;
    //    }
    //
    //    public void setIngredients(List<String> ingredients) {
    //        this.ingredients = ingredients;
    //    }
    //    private List<String> ingredients = new ArrayList<>();
    var lastUpdated: Long? = null

    constructor()
    constructor(
        name: String?,
        category: String?,
        area: String?,
        instructions: String?,
        userId: String?,
        userName: String?
    ) {
        this.name = name
        this.category = category
        this.area = area
        this.instructions = instructions
        this.userId = userId
        this.userName = userName
        //        this.ingredients = ingredients;
    }

    constructor(
        name: String?,
        category: String?,
        area: String?,
        instructions: String?,
        userId: String?,
        imgUrl: String?,
        userName: String?
    ) {
        this.name = name
        this.category = category
        this.area = area
        this.instructions = instructions
        this.imgUrl = imgUrl
        this.userId = userId
        this.userName = userName
    }

    constructor(
        name: String?,
        category: String?,
        area: String?,
        instructions: String?,
        ) {
        this.name = name
        this.category = category
        this.area = area
        this.instructions = instructions
    }

    fun toJson(): Map<String, Any?> {
        val json: MutableMap<String, Any?> = HashMap()
        json[NAME] = name
        json[CATEGORY] = this.category
        json[AREA] = area
        json[INSTRUCTIONS] = instructions
        json[IMG_URL] = imgUrl
        //        json.put(INGREDIENTS, getIngredients());
        json[LAST_UPDATED] = FieldValue.serverTimestamp()
        json[USER_ID] = userId
        json[USERNAME] = userName
        return json
    }

    companion object {
        const val NAME = "name"
        const val CATEGORY = "category"
        const val AREA = "area"
        const val INSTRUCTIONS = "instructions"
        const val IMG_URL = "imgUrl"
        const val INGREDIENTS = "ingredients"
        const val COLLECTION = "cocktails"
        const val USER_ID = "userId"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "recipes_local_last_update"
        const val USERNAME = "username"
        fun fromJson(json: Map<String?, Any?>): Cocktail {
            val name = json[NAME] as String?
            val category = json[CATEGORY] as String?
            val area = json[AREA] as String?
            val instructions = json[INSTRUCTIONS] as String?
            val imgUrl = json[IMG_URL] as String?
            val userId = json[USER_ID] as String?
            val userName = json[USERNAME] as String?
            //        List<String> ingredients = (List<String>) json.get(INGREDIENTS);
            val cocktail = Cocktail( name, category, area, instructions, userId,imgUrl, userName)
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
                    MyApplication.myContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                return sharedPref?.getLong(LOCAL_LAST_UPDATED, 0)
            }
            set(time) {
                MyApplication.myContext?.let { context ->
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
