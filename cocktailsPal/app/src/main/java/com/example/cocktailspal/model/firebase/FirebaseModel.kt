package com.example.cocktailspal.model.firebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.cocktailspal.MyApplication
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
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

    fun registerUser(user: User, listener: UserModel.Listener<Task<Void?>?>) {
        mAuth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener { task ->
                mUser=mAuth.currentUser;
                updateUserProfile(user,null, listener);
            }
    }

    fun updateUserProfile(user: User, bitmap: Bitmap?, listener: UserModel.Listener<Task<Void?>?>) {
        val userProfileImageUri = MyApplication.myContext?.let { getImageUri(it, bitmap, user.email) }
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(user.name)
            .setPhotoUri(userProfileImageUri)
            .build()
        mUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listener.onComplete(task)
                    Log.d("TAG", "User profile updated.")
                }
            }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap?, fileName: String?): Uri? {
        if (inImage == null) return null
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, fileName, null)
        return Uri.parse(path)
    }

    val userProfileDetails: User?
        get() {
            var user: User? = User()
            if (mUser != null) {
                for (profile in mUser!!.providerData) {
                    // Id of the provider (ex: google.com)
                    val providerId = profile.providerId

                    // UID specific to the provider
                    val uid = profile.uid

                    // Name, email address, and profile photo Url
                    val name = profile.displayName
                    val email = profile.email
                    val photoUrl = profile.photoUrl
                    val avatarUrl = if (photoUrl == null) null else profile.photoUrl.toString()
                    user = User(uid, email, name, avatarUrl)
                }
                return user
            }
            return null
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