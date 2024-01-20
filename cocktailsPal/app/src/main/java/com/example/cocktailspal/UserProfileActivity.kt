package com.example.cocktailspal

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktailspal.model.user.UserModel
import com.example.cocktailspal.model.user.User
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {
    var fullNameTitle: TextView? = null
    var fullNameInput: TextInputLayout? = null
    var profileImg: ImageView? = null
    var updateBtn: Button? = null
    var cameraBtn: ImageButton? = null
    var galleryBtn: ImageButton? = null
    var user: User? = null
    var cameraLauncher: ActivityResultLauncher<Void?>? = null
    var galleryLauncher: ActivityResultLauncher<String>? = null
    var isAvatarSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        fullNameTitle = findViewById<TextView>(R.id.full_name)
        fullNameInput = findViewById<TextInputLayout>(R.id.full_name_profile)
        updateBtn = findViewById<Button>(R.id.update_profile_btn)
        profileImg = findViewById<ImageView>(R.id.profile_image)
        cameraBtn = findViewById<ImageButton>(R.id.cameraButton)
        galleryBtn = findViewById<ImageButton>(R.id.galleryButton)
        user = UserModel.instance().userProfileDetails
        if(user!=null){
            fullNameTitle?.setText(user?.name)
            fullNameInput?.getEditText()?.setText(user?.name)
            if (user?.avatarUrl != null && user?.avatarUrl?.length!! > 5) {
                Picasso.get().load(user!!.avatarUrl).placeholder(R.drawable.avatar).into(profileImg)
            } else {
                profileImg?.setImageResource(R.drawable.avatar)
            }
        }
        updateBtn?.setOnClickListener(View.OnClickListener { view: View? ->
            val name = fullNameInput?.getEditText()!!.text.toString()
            if (isFormValid(name)) {
                var bitmap: Bitmap? = null
                user?.name =name
                if (isAvatarSelected) {
                    profileImg?.setDrawingCacheEnabled(true)
                    profileImg?.buildDrawingCache()
                    bitmap = (profileImg?.getDrawable() as BitmapDrawable).bitmap
                }
                UserModel.instance().updateUserProfile(user, bitmap) { task ->
                    if (task!!.isSuccessful) {
                        fullNameTitle?.setText(user?.name)
                        Toast.makeText(
                            this@UserProfileActivity,
                            "update user profile Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@UserProfileActivity,
                            "update user profile failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
        cameraLauncher = registerForActivityResult<Void?, Bitmap>(
            ActivityResultContracts.TakePicturePreview(),
            object : ActivityResultCallback<Bitmap?> {
                override fun onActivityResult(result: Bitmap?) {
                    if (result != null) {
                        profileImg?.setImageBitmap(result)
                        isAvatarSelected = true
                    }
                }
            })
        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent(),
            object : ActivityResultCallback<Uri?> {
                override fun onActivityResult(result: Uri?) {
                    if (result != null) {
                        profileImg?.setImageURI(result)
                        isAvatarSelected = true
                    }
                }
            })
        cameraBtn?.setOnClickListener(View.OnClickListener { view1: View? ->
            cameraLauncher!!.launch(
                null
            )
        })
        galleryBtn?.setOnClickListener(View.OnClickListener { view1: View? ->
            galleryLauncher!!.launch(
                "image/*"
            )
        })
    }

    fun isFormValid(name: String): Boolean {
        fullNameInput!!.error = null
        if (name.isEmpty()) {
            fullNameInput!!.error = "Enter full name"
            fullNameInput!!.requestFocus()
        }
        return !name.isEmpty()
    }
}