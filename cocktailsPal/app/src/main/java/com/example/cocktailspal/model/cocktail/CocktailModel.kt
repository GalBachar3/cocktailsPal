//package com.example.cocktailspal.model.cocktail
//
//import android.util.Log
//import com.google.gson.Gson
//
//class RecipeModel private constructor() {
//    val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
//    var retrofit: Retrofit
//    var movieApi: RecipeApi
//
//    init {
//        val gson: Gson = GsonBuilder()
//            .setLenient()
//            .create()
//        retrofit = Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//        movieApi = retrofit.create(RecipeApi::class.java)
//    }
//
//    val randomRecipe: LiveData<List<Recipe>>
//        get() {
//            val data: MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()
//            val call: Call<RecipeSearchResult> = movieApi.getRandomRecipe()
//            call.enqueue(object : Callback<RecipeSearchResult?>() {
//                fun onResponse(
//                    call: Call<RecipeSearchResult?>?,
//                    response: Response<RecipeSearchResult?>
//                ) {
//                    if (response.isSuccessful()) {
//                        val res: RecipeSearchResult = response.body()
//                        data.setValue(res.getRecipes())
//                    } else {
//                        Log.d("TAG", "----- getRandomRecipe response error")
//                    }
//                }
//
//                fun onFailure(call: Call<RecipeSearchResult?>?, t: Throwable?) {
//                    Log.d("TAG", "----- getRandomRecipe fail")
//                }
//            })
//            return data
//        }
//
//    companion object {
//        val instance = RecipeModel()
//    }
//}
