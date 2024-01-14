package com.example.cocktailspal

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel

class MainActivity : AppCompatActivity() {
    var createNewAccount: TextView? = null
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var btnLogin: Button? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var progressDialog: ProgressDialog? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNewAccount = findViewById<TextView>(R.id.createNewAccount)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        inputEmail = findViewById<EditText>(R.id.inputEmail)
        inputPassword = findViewById<EditText>(R.id.inputPassword)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        progressDialog = ProgressDialog(this)
        createNewAccount!!.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    RegisterActivity::class.java
                )
            )
        }
        btnLogin!!.setOnClickListener { view: View? -> PerformLogin() }
    }

    private fun PerformLogin() {
        val email: String = inputEmail?.text.toString()
        val password: String = inputPassword?.text.toString()
        if (!email.matches(emailPattern.toRegex())) {
            inputEmail?.error = "Enter correct email"
            inputEmail?.requestFocus()
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword?.error = "Enter proper password"
            inputPassword?.requestFocus()
        } else {
            progressDialog?.setMessage("Please wait while login...")
            progressDialog?.setTitle("Login")
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.show()
            loginUser(User(email, password))
        }
    }

    private fun loginUser(user: User) {
        UserModel.instance().loginUser(user) { task ->
            if (task.isSuccessful()) {
                progressDialog?.dismiss()
                sendUserToNextActivity()
                Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog?.dismiss()
                Toast.makeText(this@MainActivity, "" + task.getException(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendUserToNextActivity() {
        val intent: Intent = Intent(this@MainActivity, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}