package com.example.cocktailspal

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel
import com.google.android.material.textfield.TextInputLayout


class UserProfileActivity : AppCompatActivity() {
    var fullNameTitle: TextView? = null
    var fullNameInput: TextInputLayout? = null
    var emailInput: TextInputLayout? = null
    var updateBtn: Button? = null
    var user: User? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        fullNameTitle = findViewById<TextView>(R.id.full_name)
        fullNameInput = findViewById(R.id.full_name_profile)
        emailInput = findViewById(R.id.email_profile)
        updateBtn = findViewById<Button>(R.id.update_profile_btn)
        user = UserModel.instance().userProfileDetails
        fullNameTitle?.let { fullNameTitle->
            fullNameTitle.setText(user!!.name)
        }
        fullNameInput?.getEditText()?.setText(user?.name)
        emailInput?.getEditText()?.setText(user!!.email)
        updateBtn!!.setOnClickListener {
            val name: String = fullNameInput?.getEditText()?.text.toString()
            val email: String = emailInput?.getEditText()?.text.toString()
            user!!.name = name
            user!!.email = email
            UserModel.instance().updateUserProfile(user, null) { task ->
                if (task != null) {
                    if (task.isSuccessful()) {
                        fullNameTitle?.text = user!!.name
                    } else {
                    }
                }
            }
        }
    }
}