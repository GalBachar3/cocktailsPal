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

class MainActivity : AppCompatActivity() {
    var createNewAccount: TextView? = null
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var btnLogin: Button? = null
    var progressDialog: ProgressDialog? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNewAccount = findViewById(R.id.createNewAccount)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressDialog = ProgressDialog(this)
        createNewAccount!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }
}
