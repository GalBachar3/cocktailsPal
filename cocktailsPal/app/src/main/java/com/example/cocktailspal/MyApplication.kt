package com.example.cocktailspal

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // In your Application class or Activity's onCreate method
        FirebaseApp.initializeApp(applicationContext)
        myContext = applicationContext
    }

    companion object {
        private var myContext: Context? = null

        fun getAppContext(): Context {
            return myContext ?: throw IllegalStateException("Application context not initialized yet.")
        }
    }
}
