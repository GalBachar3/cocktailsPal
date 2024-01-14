package com.example.cocktailspal.model.user

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.example.cocktailspal.model.localDB.AppLocalDb
import com.example.cocktailspal.model.localDB.AppLocalDbRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class UserModel private constructor() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val mainHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel: FirebaseModel = FirebaseModel()
    var localDb: AppLocalDbRepository = AppLocalDb.appDb

    fun interface Listener<T> {
        fun onComplete(data: Task<AuthResult?>)
    }

    enum class LoadingState {
        LOADING,
        NOT_LOADING
    }

    val EventUsersListLoadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>(
        LoadingState.NOT_LOADING
    )

    fun registerUser(user: User?, listener: Listener<Task<AuthResult?>?>) {
        firebaseModel.registerUser(
            user
        ) { task: Task<AuthResult?>? -> task?.let { listener.onComplete(it) } }
    }

    fun loginUser(user: User?, listener: Listener<Task<AuthResult?>?>) {
        firebaseModel.loginUser(
            user,
            Listener<Task<AuthResult>> { task: Task<AuthResult?>? ->
                if (task != null) {
                    listener.onComplete(task)
                }
            })
    }

    companion object {
        private val _instance = UserModel()
        fun instance(): UserModel {
            return _instance
        }
    }
}
