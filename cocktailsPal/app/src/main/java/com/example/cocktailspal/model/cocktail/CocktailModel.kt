package com.example.cocktailspal.model.cocktail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.example.cocktailspal.model.localDB.AppLocalDb
import com.example.cocktailspal.model.localDB.AppLocalDbRepository
import java.io.ByteArrayOutputStream
import java.net.URL
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

    val allCocktails: LiveData<List<Cocktail?>?>?
        get() {
            if (cocktailsList == null) {
                cocktailsList = localDb?.cocktailDao()?.all
                refreshAllCocktails()
            }
            return cocktailsList
        }

    private var userCocktailsList: LiveData<List<Cocktail?>?>? = null
    fun getAllUserCocktails(userId: String?): LiveData<List<Cocktail?>?>? {
        if (userCocktailsList == null) {
            userCocktailsList = localDb?.cocktailDao()?.getAllCocktailsByUser(userId)
        }
        return userCocktailsList
    }

    fun refreshAllCocktails() {
        EventListLoadingState.value = LoadingState.LOADING

        val localLastUpdate = Cocktail.localLastUpdate

        val callback = object : CocktailModel.Listener<List<Cocktail?>?> {
            override fun onComplete(list: List<Cocktail?>?) {
                executor.execute {
                    Log.d("TAG", " firebase return : ${list?.size}")
                    var time = localLastUpdate
                    list?.forEach { cocktail ->
                        cocktail?.let {
                            cocktail.photo = (urlToByteArr(cocktail.imgUrl)!!)
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

    private var userCocktailCount = -1
    fun getUserCocktailCount(callback: Listener<Int?>): Int? {
        if (userCocktailCount == -1) {
            firebaseModel.getUserCocktailCount(object : Listener<Int?> {
                override fun onComplete(data: Int?) {
                    if (data != null) {
                        userCocktailCount = data
                    }
                    callback.onComplete(data)
                }
            })

        }
        return userCocktailCount
    }

//    fun getUserCocktailCount(): Int? {
//        if (userCocktailCount == -1) {
//            setCocktailsCount()
//            //            callback.onComplete(userCocktailCount);
////           firebaseModel.getUserCocktailCount(data -> {
////               userCocktailCount = data;
////               callback.onComplete(data);
////           });
//        }
//        return userCocktailCount
//    }

    fun isCocktailNameExists(cocktailName: String?): Boolean {
        val cocktail: Cocktail? = localDb?.cocktailDao()?.findByName(cocktailName)
        return cocktail != null
    }

    fun urlToByteArr(link: String?): ByteArray? {
        var imageBytes: ByteArray? = null
        try {
            val url = URL(link)
            val inputStream = url.openStream()
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }
            imageBytes = outputStream.toByteArray()
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            println(e)
        } finally {
            return imageBytes
        }
    }

    private fun setCocktailsCount() {
        userCocktailCount = localDb?.cocktailDao()?.countCocktailByUser(firebaseModel.userId)!!
    }
    fun resetDataOnLogout() {
        userCocktailCount = -1
    }
}
