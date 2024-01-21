package com.example.cocktailspal.model.cocktail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.example.cocktailspal.model.localDB.AppLocalDb
import com.example.cocktailspal.model.localDB.AppLocalDbRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class CocktailModel private constructor() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val firebaseModel: FirebaseModel = FirebaseModel()
    var localDb: AppLocalDbRepository? = AppLocalDb.appDb

    enum class LoadingState {
        LOADING,
        NOT_LOADING
    }

    val EventListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData<LoadingState>(
            LoadingState.NOT_LOADING
        )

    interface Listener<T> {
        fun onComplete(data: T)
    }

    private var cocktailsList: LiveData<List<Cocktail?>?>? = null

    val allRecipes: LiveData<List<Cocktail?>?>?
        get() {
            if (cocktailsList == null) {
                cocktailsList = localDb?.cocktailDao()?.all
                refreshAllCocktails()
            }
            return cocktailsList
        }

    fun refreshAllCocktails() {
        EventListLoadingState.value = LoadingState.LOADING

        // get local last update
        val localLastUpdate = Cocktail.localLastUpdate

        // get all updated records from firebase since local last update
        // Calling getAllRecipesSince and providing a callback


        val callback = object : CocktailModel.Listener<List<Cocktail?>?> {
            override fun onComplete(list: List<Cocktail?>?) {
                executor.execute {
                    Log.d("TAG", " firebase return : ${list?.size}")
                    var time = localLastUpdate
                    list?.forEach { cocktail ->
                        // insert new records into ROOM
                        cocktail?.let {
                            localDb?.cocktailDao()?.insertAll(it)
                            if (time!! < it.lastUpdated!!) {
                                time = it.lastUpdated
                            }
                        }
                    }
                    // update local last update
                    Cocktail.localLastUpdate = time
                    EventListLoadingState.postValue(LoadingState.NOT_LOADING)
                }
            }
            }
        firebaseModel.getAllCocktailsSince(localLastUpdate, callback);
        }

    fun addCocktail(cocktail: Cocktail?, listener: () -> Boolean) {
        firebaseModel.addCocktail(cocktail!!) { Void ->
            refreshAllCocktails()
            listener.invoke()
        }
    }

    fun uploadImage(name: String?, bitmap: Bitmap?, listener: (Any) -> Unit) {
        firebaseModel.uploadImage(name!!, bitmap!!, listener!!)
    }

    companion object {
        val _instance = CocktailModel()
        fun instance(): CocktailModel {
            return _instance
        }
    }
}
