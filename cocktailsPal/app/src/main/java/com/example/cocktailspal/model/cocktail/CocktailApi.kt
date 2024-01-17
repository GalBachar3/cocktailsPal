package com.example.cocktailspal.model.cocktail

import retrofit2.Call
import retrofit2.http.GET

interface CocktailApi {
    @get:GET("/random.php")
    val randomCocktail: Call<CocktailSearchResult?>?
}
