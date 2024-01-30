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
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
    }

    fun registerUser(user: User, listener: UserModel.Listener<Task<Void?>?>) {
        mAuth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener {
                mUser = mAuth.currentUser;
                if (mUser != null) {
                    updateUserProfile(user, null, listener);
                } else {
                    listener.onComplete(null);
                }
            }
    }

    fun updateUserProfile(user: User, bitmap: Bitmap?, listener: UserModel.Listener<Task<Void?>?>) {
        val userProfileImageUri =
            MyApplication.getAppContext().let { getImageUri(it, bitmap, user.email) }
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

    fun loginUser(user: User, listener: UserModel.Listener<Task<AuthResult>>) {
        mAuth.signInWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener { task ->
                mUser = mAuth.currentUser
                listener.onComplete(task)
            }
    }

    fun isUserLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }

    fun getAllCocktailsSince(since: Long?, callback: CocktailModel.Listener<List<Cocktail?>?>) {
        if (since == null) {
            // Handle the case where 'since' is null (you might want to log an error or return an empty list)
            callback.onComplete(emptyList())
            return
        }

        db.collection(Cocktail.COLLECTION)
            .whereGreaterThanOrEqualTo(Cocktail.LAST_UPDATED, Timestamp(0, 0))
            .get()
            .addOnCompleteListener { task ->
                val list: MutableList<Cocktail> = LinkedList<Cocktail>()
                if (task.isSuccessful) {
                    val jsonsList: QuerySnapshot? = task.result
                    if (jsonsList != null) {
                        for (json in jsonsList) {
                            val cocktail: Cocktail = Cocktail.fromJson(json.data)
                            list.add(cocktail)
                        }
                    }
                }
                callback.onComplete(list)
            }
    }

    fun getAllCocktails():
            MutableList<Cocktail> {
        val list: MutableList<Cocktail> = LinkedList<Cocktail>()

        val x = db.collection(Cocktail.COLLECTION)
            .whereGreaterThanOrEqualTo(Cocktail.LAST_UPDATED, Timestamp(0, 0))
            .get()
//            .addOnCompleteListener { task ->

//                if (task.isSuccessful) {
        val jsonsList: QuerySnapshot? = x.result
        if (jsonsList != null) {
            for (json in jsonsList) {
                val cocktail: Cocktail = Cocktail.fromJson(json.data)
                list.add(cocktail)
            }
        }
//                }
//            }

        return list;
    }

    fun logout() {
        mAuth.signOut()
        mUser = null
    }

    fun uploadImage(name: String, bitmap: Bitmap, listener: (Any) -> Unit) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener { listener.invoke(true) }.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                listener.invoke(
                    uri.toString()
                )
            }
        }
    }

    fun addCocktail(cocktail: Cocktail, listener: (Any) -> Unit) {
        db.collection(Cocktail.COLLECTION).document(cocktail.id).set(cocktail.toJson())
          .addOnCompleteListener { listener.invoke(true) }
    }

    fun deleteCocktail(cocktail: Cocktail ,listener: (Any) -> Unit) {
        db.collection(Cocktail.COLLECTION).document(cocktail.id).delete()
            .addOnCompleteListener { listener.invoke(true) }
    }

    fun getUserCocktailCount(callback: CocktailModel.Listener<Int?>) {
        db.collection(Cocktail.COLLECTION)
            .whereEqualTo(Cocktail.USER_ID, getUserId()).get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    val list: List<Cocktail> = LinkedList<Cocktail>()
                    var count = -1
                    if (task.isSuccessful()) {
                        val jsonsList: QuerySnapshot = task.getResult()!!
                        count = jsonsList.size()
                    }
                    callback.onComplete(count)
                }
            })
    }

    fun getUserId(): String {
        return getUserProfileDetails()!!.id
    }

    fun getUserProfileDetails(): User? {
        var user: User? = User()
        if (mUser != null) {
            for (profile in mUser!!.providerData) {
                val providerId = profile.providerId
                val uid = profile.uid
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
}