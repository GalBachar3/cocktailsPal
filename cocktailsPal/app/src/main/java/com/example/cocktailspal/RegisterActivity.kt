package com.example.cocktailspal

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    var alreadyHaveAccount: TextView? = null
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var inputConfirmPassword: EditText? = null
    var btnRegister: Button? = null
    var progressDialog: ProgressDialog? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        alreadyHaveAccount = findViewById<TextView>(R.id.alreadyHaveAccount)
        inputEmail = findViewById<EditText>(R.id.inputEmail)
        inputPassword = findViewById<EditText>(R.id.inputPassword)
        inputConfirmPassword = findViewById<EditText>(R.id.inputConfirmPassword)
        btnRegister = findViewById<Button>(R.id.btnRegister)
        progressDialog = ProgressDialog(this)
        alreadyHaveAccount!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@RegisterActivity,
                    MainActivity::class.java
                )
            )
        }
    }
}