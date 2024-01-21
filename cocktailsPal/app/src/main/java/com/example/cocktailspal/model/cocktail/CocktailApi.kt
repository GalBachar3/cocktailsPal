package com.example.cocktailspal.model.cocktail

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface CocktailApi {
    @GET("/api/json/v1/1/random.php")
    fun getRandomCocktail(): Call<CocktailSearchResult?>?

    @GET("/images/media/drinks/{imageName}")
    fun getImage(@Path("imageName") imageName: String?): Call<ResponseBody?>?
}
