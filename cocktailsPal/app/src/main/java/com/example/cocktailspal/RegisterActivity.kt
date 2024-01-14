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

class RegisterActivity : AppCompatActivity() {
    var alreadyHaveAccount: TextView? = null
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var inputConfirmPassword: EditText? = null
    var btnRegister: Button? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
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
        btnRegister!!.setOnClickListener { view: View? -> PerformAuth() }
    }

    private fun PerformAuth() {
        val email: String = inputEmail?.text.toString()
        val password: String = inputPassword?.text.toString()
        val confirmPassword: String = inputConfirmPassword?.text.toString()
        if (!email.matches(emailPattern.toRegex())) {
            inputEmail?.error = "Enter correct email"
            inputEmail?.requestFocus()
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword?.error = "Enter proper password"
            inputPassword?.requestFocus()
        } else if (password != confirmPassword) {
            inputConfirmPassword?.error = "Password not match both fields"
            inputConfirmPassword?.requestFocus()
        } else {
            progressDialog?.setMessage("Please wait while registration...")
            progressDialog?.setTitle("Registration")
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.show()
            registerUser(User(email, password))
        }
    }

    private fun registerUser(user: User) {
        UserModel.instance().registerUser(user) { task ->
            if (task.isSuccessful()) {
                progressDialog?.dismiss()
                sendUserToNextActivity()
                Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT)
                    .show()
            } else {
                progressDialog?.dismiss()
                Toast.makeText(this@RegisterActivity, "" + task.getException(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendUserToNextActivity() {
        val intent: Intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}