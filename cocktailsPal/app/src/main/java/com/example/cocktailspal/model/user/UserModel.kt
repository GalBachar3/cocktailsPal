package com.example.cocktailspal.model.user

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.cocktailspal.model.firebase.FirebaseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class UserModel private constructor() {
    private val firebaseModel: FirebaseModel? = FirebaseModel()

    fun interface Listener<T> {
        fun onComplete(data: T)
    }

    enum class LoadingState {
        LOADING,
        NOT_LOADING
    }

    val EventUsersListLoadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>(
        LoadingState.NOT_LOADING
    )

    fun registerUser(user: User?, listener: Listener<Task<Void?>?>) {
        if (user != null) {
            firebaseModel?.registerUser(
                user
            ) { task: Task<Void?>? -> run { listener.onComplete(task) } }
        }
    }

    fun loginUser(user: User?, listener: Listener<Task<AuthResult>>) {
        if (user != null) {
            firebaseModel?.loginUser(
                user
            ) { task: Task<AuthResult> ->
                listener.onComplete(task)
            }
        }
    }

    companion object {
        private val _instance = UserModel()
        fun instance(): UserModel {
            return _instance
        }
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseModel!!.isUserLoggedIn()
    }

    val userProfileDetails: User?
        get() = firebaseModel?.userProfileDetails

    fun updateUserProfile(user: User?, bitmap: Bitmap?, listener: Listener<Task<Void?>?>) {
        if (user != null) {
            firebaseModel?.updateUserProfile(user, null) { task ->
                listener.onComplete(
                    task
                )
            }
        }
    }
}
