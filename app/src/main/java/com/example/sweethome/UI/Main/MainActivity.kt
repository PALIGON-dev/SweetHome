package com.example.sweethome.UI.Main

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sweethome.R
import com.example.sweethome.UI.Auth.AuthActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        val bottomView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val drawerView = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)
        val navController = navHostFragment.navController
        bottomView.setupWithNavController(navController)
        navView.setupWithNavController(navController)

        //Включаем Drawer только на фрагменте с деталями проекта
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showDrawer = destination.id == R.id.ProjectFragment || destination.id == R.id.projectBudgetFragment
            drawerView.setDrawerLockMode(//Если фрагмент нужный, разблокируем Drawer
                if (showDrawer) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            )
            val showBottomNav = destination.id == R.id.addTaskFragment || destination.id == R.id.addProjectFragment
                    || destination.id == R.id.ProjectFragment || destination.id == R.id.projectBudgetFragment
            if (showBottomNav) bottomView.visibility = View.GONE //Если фрагмент нужный, скроем BottomNavigation
            else bottomView.visibility = View.VISIBLE
        }
    }

    class App : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
}
