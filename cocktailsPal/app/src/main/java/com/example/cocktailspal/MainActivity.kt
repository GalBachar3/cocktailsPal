package com.example.cocktailspal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktailspal.model.user.UserModel

class MainActivity : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (UserModel.instance().isUserLoggedIn()) {
            sendUserToNextActivity(HomeActivity::class.java)
        } else {
            sendUserToNextActivity(LoginActivity::class.java)
        }
    }

    private fun sendUserToNextActivity(clazz: Class<*>) {
        val intent: Intent = Intent(this@MainActivity, clazz)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}