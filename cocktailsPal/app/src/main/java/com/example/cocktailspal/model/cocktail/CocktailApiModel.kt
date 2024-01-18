package com.example.cocktailspal.model.cocktail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CocktailApiModel private constructor() {
    val BASE_URL = "https://www.themealdb.com/"
    var retrofit: Retrofit
    var cocktailApi: CocktailApi

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        cocktailApi = retrofit.create(CocktailApi::class.java)
    }

    val randomRecipe: LiveData<List<CocktailApiObj>>
        get() {
            val data: MutableLiveData<List<CocktailApiObj>> = MutableLiveData<List<CocktailApiObj>>()
            val call: Call<CocktailSearchResult?>? = cocktailApi.randomCocktail
            if (call != null) {
                call.enqueue(object : Callback<CocktailSearchResult?> {
                    override fun onResponse(
                        call: Call<CocktailSearchResult?>,
                        response: Response<CocktailSearchResult?>
                    ) {
                        if (response.isSuccessful()) {
                            val res: CocktailSearchResult? = response.body()
                            if (res != null) {
                                data.setValue(res.cocktails)
                            }
                        } else {
                            Log.d("TAG", "----- getRandomRecipe response error")
                        }
                    }

                    override fun onFailure(call: Call<CocktailSearchResult?>, t: Throwable) {
                        Log.d("TAG", "----- getRandomRecipe fail")
                    }
                })
            }
            return data
        }

    companion object {
        val _instance = CocktailApiModel()
    }
}
