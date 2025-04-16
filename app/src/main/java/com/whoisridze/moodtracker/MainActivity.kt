package com.whoisridze.moodtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController(R.id.navHostFragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, dest, _ ->
            bottomNav.isVisible = dest.id != R.id.logMoodFragment
        }
    }
}
