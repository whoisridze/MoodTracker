package com.whoisridze.moodtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController(R.id.navHostFragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setupWithNavController(navController)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        bottomNav.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                when(item.itemId) {
                    R.id.dashboardFragment -> navController.navigate(R.id.dashboardFragment, null, navOptions)
                    R.id.statsFragment -> navController.navigate(R.id.statsFragment, null, navOptions)
                    R.id.socialFragment -> navController.navigate(R.id.socialFragment, null, navOptions)
                    R.id.settingsFragment -> navController.navigate(R.id.settingsFragment, null, navOptions)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, dest, _ ->
            bottomNav.isVisible = dest.id != R.id.logMoodFragment
        }
    }
}