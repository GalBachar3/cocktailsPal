package com.example.cocktailspal.model.cocktail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cocktailspal.utils.StringUtils
import java.net.URL

class CocktailApiObj(
    var idMeal: String,
    var strMeal: String,
    var strCategory: String,
    var strArea: String,
    var strInstructions: String,
    var strCocktailThumb: String,
    var strIngredient1: String,
    var strIngredient2: String,
    var strIngredient3: String,
    var strIngredient4: String,
    var strIngredient5: String,
    var strIngredient6: String,
    var strIngredient7: String,
    var strIngredient8: String,
    var strIngredient9: String,
    var strIngredient10: String,
    var strIngredient11: String,
    var strIngredient12: String,
    var strIngredient13: String,
    var strIngredient14: String,
    var strIngredient15: String,
    var strIngredient16: String,
    var strIngredient17: String,
    var strIngredient18: String,
    var strIngredient19: String,
    var strIngredient20: String,
    var strMeasure1: String,
    var strMeasure2: String,
    var strMeasure3: String,
    var strMeasure4: String,
    var strMeasure5: String,
    var strMeasure6: String,
    var strMeasure7: String,
    var strMeasure8: String,
    var strMeasure9: String,
    var strMeasure10: String,
    var strMeasure11: String,
    var strMeasure12: String,
    var strMeasure13: String,
    var strMeasure14: String,
    var strMeasure15: String,
    var strMeasure16: String,
    var strMeasure17: String,
    var strMeasure18: String,
    var strMeasure19: String,
    var strMeasure20: String
) {
    fun toCocktail(): CocktailApiReturnObj {
        val cocktail = CocktailApiReturnObj()
        cocktail.name = (strMeal)
        cocktail.category = (strCategory)
        cocktail.instructions = (strInstructions)
        cocktail.ingredients = (ingredients)
        val parts = strCocktailThumb.split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val lastPart = parts[parts.size - 1]
        cocktail.imagePath = (lastPart)
        return cocktail
    }

    private val ingredients: String
        private get() {
            var ingredients =
                if (!StringUtils.isBlank(strIngredient1)) "" else "$strIngredient1 - $strMeasure1"
            ingredients += if (!StringUtils.isBlank(strIngredient2)) "" else ", $strIngredient2 - $strMeasure2"
            ingredients += if (!StringUtils.isBlank(strIngredient3)) "" else ", $strIngredient3 - $strMeasure3"
            ingredients += if (!StringUtils.isBlank(strIngredient4)) "" else ", $strIngredient4 - $strMeasure4"
            ingredients += if (!StringUtils.isBlank(strIngredient5)) "" else ", $strIngredient5 - $strMeasure5"
            ingredients += if (!StringUtils.isBlank(strIngredient6)) "" else ", $strIngredient6 - $strMeasure6"
            ingredients += if (!StringUtils.isBlank(strIngredient7)) "" else ", $strIngredient7 - $strMeasure7"
            ingredients += if (!StringUtils.isBlank(strIngredient8)) "" else ", $strIngredient8 - $strMeasure8"
            ingredients += if (!StringUtils.isBlank(strIngredient9)) "" else ", $strIngredient9 - $strMeasure9"
            ingredients += if (!StringUtils.isBlank(strIngredient10)) "" else ", $strIngredient10 - $strMeasure10"
            ingredients += if (!StringUtils.isBlank(strIngredient11)) "" else ", $strIngredient11 - $strMeasure11"
            ingredients += if (!StringUtils.isBlank(strIngredient12)) "" else ", $strIngredient12 - $strMeasure12"
            ingredients += if (!StringUtils.isBlank(strIngredient13)) "" else ", $strIngredient13 - $strMeasure13"
            ingredients += if (!StringUtils.isBlank(strIngredient14)) "" else ", $strIngredient14 - $strMeasure14"
            ingredients += if (!StringUtils.isBlank(strIngredient15)) "" else ", $strIngredient15 - $strMeasure15"
            ingredients += if (!StringUtils.isBlank(strIngredient16)) "" else ", $strIngredient16 - $strMeasure16"
            ingredients += if (!StringUtils.isBlank(strIngredient17)) "" else ", $strIngredient17 - $strMeasure17"
            ingredients += if (!StringUtils.isBlank(strIngredient18)) "" else ", $strIngredient18 - $strMeasure18"
            ingredients += if (!StringUtils.isBlank(strIngredient19)) "" else ", $strIngredient19 - $strMeasure19"
            ingredients += if (!StringUtils.isBlank(strIngredient20)) "" else ", $strIngredient20 - $strMeasure20"
            return ingredients
        }
}
