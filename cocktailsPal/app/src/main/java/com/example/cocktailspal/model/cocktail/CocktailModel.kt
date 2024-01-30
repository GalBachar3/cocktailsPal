package com.example.cocktailspal.model.cocktail

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.cocktailspal.MyApplication
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.example.cocktailspal.model.localDB.AppDatabase
import com.example.cocktailspal.model.user.UserModel
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class CocktailModel private constructor() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val firebaseModel: FirebaseModel = FirebaseModel()
    var localDb = Room.databaseBuilder(
        MyApplication.getAppContext(),
        AppDatabase::class.java,
        "database-name"
    ).fallbackToDestructiveMigration().build()

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

    private var cocktailsList: LiveData<List<Cocktail>> = MutableLiveData()

    val allCocktails: LiveData<List<Cocktail>>
        get() {
            refreshAllCocktails()
            cocktailsList = localDb.cocktailDao().getAll()

            return cocktailsList
        }

    private var userCocktailsList: LiveData<List<Cocktail>> = MutableLiveData()
    fun getAllUserCocktails(userId: String?): LiveData<List<Cocktail>> {
        refreshAllCocktails()
        userCocktailsList = localDb.cocktailDao().getAllCocktailsByUser(userId)

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
                    for (cocktail in list!!) {
                        if (cocktail != null) {
                            if (cocktail.imgUrl != null) {
                                //cocktail.photo = urlToByteArr(cocktail.imgUrl)!!
                            }
                        }
                        if (cocktail != null) {
                            localDb.cocktailDao().insertAll(cocktail)
                        }
                        if (cocktail != null) {
                            if (time!! < cocktail.lastUpdated!!) {
                                time = cocktail.lastUpdated
                            }
                        }
                        localDb.cocktailDao().insertAll(cocktail!!)
                    }

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

    fun deleteCocktail(cocktail: Cocktail?, listener: () -> Boolean) {
        firebaseModel.deleteCocktail(cocktail!!) { Void ->
            Thread {
                localDb.cocktailDao().delete(cocktail)
            }.start()

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

    fun isCocktailNameExists(cocktailName: String?): Boolean {
        val cocktail: Cocktail? = localDb.cocktailDao().findByName(cocktailName)
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

    fun getUserCocktailCount(): Int? {
        return localDb.cocktailDao().countCocktailByUser(UserModel.instance().getUserId())!!
    }
}
