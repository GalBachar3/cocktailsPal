package com.example.cocktailspal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.cocktailspal.model.user.UserModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.navhost) as NavHostFragment?
        navController = navHostFragment?.navController
        NavigationUI.setupActionBarWithNavController(this, navController!!)
        val navView = findViewById<BottomNavigationView>(R.id.bottomNav)
        NavigationUI.setupWithNavController(navView, navController!!)
    }

    var fragmentMenuId = 0
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        if (fragmentMenuId != 0) {
            menu.removeItem(fragmentMenuId)
        }
        fragmentMenuId = 0
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController?.popBackStack()
            }
            R.id.logout -> {
                UserModel.instance().logout()
                sendUserToNextActivity(LoginActivity::class.java)
            }
            R.id.addEditCocktailFragment -> {
                // Make sure navController is not null and the navigation graph is set up correctly
                navController?.navigate(R.id.addEditCocktailFragment)
            }
            else -> {
                fragmentMenuId = item.itemId
                return NavigationUI.onNavDestinationSelected(item, navController!!)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun sendUserToNextActivity(clazz: Class<*>) {
        val intent = Intent(this@HomeActivity, clazz)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}