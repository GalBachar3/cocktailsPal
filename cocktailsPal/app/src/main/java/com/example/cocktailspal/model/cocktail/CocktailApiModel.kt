package com.example.cocktailspal.model.cocktail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

class CocktailApiModel private constructor() {
    val BASE_URL = "https://www.thecocktaildb.com/"
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

    val randomCocktail: MutableLiveData<CocktailApiReturnObj>
        get() {
            val data: MutableLiveData<CocktailApiReturnObj> = MutableLiveData<CocktailApiReturnObj>()
            val call: Call<CocktailSearchResult?>? = cocktailApi.getRandomCocktail()
            call?.enqueue(object : Callback<CocktailSearchResult?> {
                override fun onResponse(
                    call: Call<CocktailSearchResult?>,
                    response: Response<CocktailSearchResult?>
                ) {
                    if (response.isSuccessful) {
                        val res: CocktailSearchResult? = response.body()
                        val cocktail: CocktailApiReturnObj? = res?.cocktails?.firstOrNull()?.toCocktail()

                        if (cocktail != null) {
                            data.value = cocktail!!
                        } else {
                            Log.d("TAG", "----- getRandomCocktail response error - cant generate cocktail")
                        }

                    } else {
                        Log.d("TAG", "----- getRandomCocktail response error")
                    }
                }

                override fun onFailure(call: Call<CocktailSearchResult?>, t: Throwable) {
                    Log.d("TAG", "----- getRandomCocktail fail")
                }
            })
            return data
        }

    fun getImg(path: String?): MutableLiveData<InputStream> {
        val data = MutableLiveData<InputStream>()
        val call: Call<ResponseBody?>? = cocktailApi.getImage(path)
        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    data.setValue(response.body()!!.byteStream())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("TAG", "----- getRandomRecipe fail")
            }
        })
        return data
    }

    companion object {
        val _instance = CocktailApiModel()
        fun instance(): CocktailApiModel {
            return _instance
        }
    }
}
