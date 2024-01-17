package com.example.cocktailspal.model.cocktail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.example.cocktailspal.model.localDB.AppLocalDb
import com.example.cocktailspal.model.localDB.AppLocalDbRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CocktailModel private constructor() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val firebaseModel: FirebaseModel = FirebaseModel()
    var localDb: AppLocalDbRepository? = AppLocalDb.appDb
    val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    var retrofit: Retrofit
    var cocktailApi: CocktailApi
    val randomRecipe: LiveData<List<Cocktail>>
        get() {
            val data: MutableLiveData<List<Cocktail>> = MutableLiveData<List<Cocktail>>()
            val call: Call<CocktailSearchResult?>? = cocktailApi.randomCocktail
            call?.enqueue(object:Callback<CocktailSearchResult?> {
                override fun onFailure(call: Call<CocktailSearchResult?>?, t: Throwable?) {
                    Log.d("TAG", "----- getRandomRecipe fail")
                }

                override fun onResponse(
                    call: Call<CocktailSearchResult?>,
                    response: retrofit2.Response<CocktailSearchResult?>
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
            })
            return data
        }

    enum class LoadingState {
        LOADING,
        NOT_LOADING
    }

    val EventStudentsListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData<LoadingState>(
            LoadingState.NOT_LOADING
        )

    interface Listener<T> {
        fun onComplete(data: T)
    }

    private var cocktailsList: LiveData<List<Cocktail?>?>? = null

    init {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        cocktailApi = retrofit.create(CocktailApi::class.java)
    }

    val allRecipes: LiveData<List<Cocktail?>?>?
        get() {
            if (cocktailsList == null) {
                cocktailsList = localDb?.cocktailDao()?.all
                refreshAllRecipes()
            }
            return cocktailsList
        }

    fun refreshAllRecipes() {
        EventStudentsListLoadingState.value = LoadingState.LOADING

        // get local last update
        val localLastUpdate = Cocktail.localLastUpdate

        // get all updated records from firebase since local last update
        // Calling getAllRecipesSince and providing a callback


        val callback = object : CocktailModel.Listener<List<Cocktail?>?> {
            override fun onComplete(list: List<Cocktail?>?) {
                executor.execute {
                    Log.d("TAG", " firebase return : ${list?.size}")
                    var time = localLastUpdate
                    list?.forEach { rcp ->
                        // insert new records into ROOM
                        rcp?.let {
                            localDb?.cocktailDao()?.insertAll(it)
                            if (time!! < it.lastUpdated!!) {
                                time = it.lastUpdated
                            }
                        }
                    }
                    try {
                        Thread.sleep(3000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    // update local last update
                    Cocktail.localLastUpdate = time
                    EventStudentsListLoadingState.postValue(LoadingState.NOT_LOADING)
                }
            }
            }
        firebaseModel.getAllCocktailsSince(localLastUpdate, callback);
        }

    companion object {
        val _instance = CocktailModel()
        fun instance(): CocktailModel {
            return _instance
        }
    }
}
