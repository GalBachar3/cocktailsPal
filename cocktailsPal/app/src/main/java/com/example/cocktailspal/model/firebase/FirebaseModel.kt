package com.example.cocktailspal.model.firebase

import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class FirebaseModel {
    var db: FirebaseFirestore
    var storage: FirebaseStorage
    var mAuth: FirebaseAuth
    var mUser: FirebaseUser
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    init {
        db = FirebaseFirestore.getInstance()
        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.setFirestoreSettings(settings)
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.getCurrentUser()!!
    }

    fun registerUser(
        user: User,
        listener: UserModel.Listener<Task<AuthResult>?>
    ) {
        mAuth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(p0: Task<AuthResult?>) {
                    listener.onComplete(p0)
                }
            })
    }

    fun loginUser(
        user: User,
        listener: UserModel.Listener<Task<AuthResult>?>
    ) {
        mAuth.signInWithEmailAndPassword(user.email.toString(), user.password!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(p0: Task<AuthResult?>) {
                    listener.onComplete(p0)
                }
            })
    }
}
