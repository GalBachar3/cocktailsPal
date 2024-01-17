package com.example.cocktailspal.model.firebase

import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailModel
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.LinkedList

class FirebaseModel {
    var db: FirebaseFirestore
    var storage: FirebaseStorage
    var mAuth: FirebaseAuth
    var mUser: FirebaseUser?

    init {
        db = FirebaseFirestore.getInstance()
        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
    }

    fun registerUser(user: User, listener: UserModel.Listener<Task<AuthResult?>?>) {
        mAuth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener { task -> listener.onComplete(task) }
    }

    fun loginUser(user: User, listener: UserModel.Listener<Task<AuthResult>>) {
        mAuth.signInWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener { task -> listener.onComplete(task) }
    }

    fun isUserLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }
    fun getAllCocktailsSince(since: Long?, callback: CocktailModel.Listener<List<Cocktail?>?>) {
        db.collection(Cocktail.COLLECTION)
            .whereGreaterThanOrEqualTo(Cocktail.LAST_UPDATED, Timestamp(since!!, 0))
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    val list: MutableList<Cocktail> = LinkedList<Cocktail>()
                    if (task.isSuccessful()) {
                        val jsonsList: QuerySnapshot? = task.getResult()
                        if (jsonsList != null) {
                            for (json in jsonsList) {
                                val cocktail: Cocktail = Cocktail.fromJson(json.getData())
                                list.add(cocktail)
                            }
                        }
                    }
                    callback.onComplete(list)
                }
            })
    }
}
