package com.example.cocktailspal.model.cocktail

import com.google.gson.annotations.SerializedName

class CocktailSearchResult {
    @SerializedName("drinks")
    var cocktails: List<Cocktail>? = null
}
