package com.example.cocktailspal.model.cocktail

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cocktailspal.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
class Recipe {
    @PrimaryKey
    var id = ""
    var name: String? = ""
    var category: String? = ""
    var area: String? = ""
    var instructions: String? = ""
    var imgUrl: String? = ""

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
        id: String,
        name: String?,
        category: String?,
        area: String?,
        instructions: String?,
        imgUrl: String? //            ,  List<String> ingredients
    ) {
        this.id = id
        this.name = name
        this.category = category
        this.area = area
        this.instructions = instructions
        this.imgUrl = imgUrl
        //        this.ingredients = ingredients;
    }

    fun toJson(): Map<String, Any?> {
        val json: MutableMap<String, Any?> = HashMap()
        json[ID] = id
        json[NAME] = name
        json[CATEGORY] = this.category
        json[AREA] = area
        json[INSTRUCTIONS] = instructions
        json[IMG_URL] = imgUrl
        //        json.put(INGREDIENTS, getIngredients());
        json[LAST_UPDATED] = FieldValue.serverTimestamp()
        return json
    }

    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val CATEGORY = "category"
        const val AREA = "area"
        const val INSTRUCTIONS = "instructions"
        const val IMG_URL = "imgUrl"
        const val INGREDIENTS = "ingredients"
        const val COLLECTION = "recipes"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "recipes_local_last_update"
        fun fromJson(json: Map<String?, Any?>): Recipe {
            val id = json[ID] as String?
            val name = json[NAME] as String?
            val category = json[CATEGORY] as String?
            val area = json[AREA] as String?
            val instructions = json[INSTRUCTIONS] as String?
            val imgUrl = json[IMG_URL] as String?
            //        List<String> ingredients = (List<String>) json.get(INGREDIENTS);
            val rcp = Recipe(id!!, name, category, area, instructions, imgUrl)
            try {
                val time = json[LAST_UPDATED] as Timestamp?
                rcp.lastUpdated = time!!.seconds
            } catch (e: Exception) {
            }
            return rcp
        }

        var localLastUpdate: Long?
            get() {
                val sharedPref: SharedPreferences? =
                    MyApplication.myContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                return sharedPref?.getLong(LOCAL_LAST_UPDATED, 0)
            }
            set(time) {
                val sharedPref: SharedPreferences? =
                    MyApplication.myContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPref!!.edit()
                if (time != null) {
                    editor.putLong(LOCAL_LAST_UPDATED, time)
                }
                editor.commit()
            }
    }
}
