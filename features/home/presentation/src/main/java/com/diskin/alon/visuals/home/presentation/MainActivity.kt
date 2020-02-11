package com.diskin.alon.visuals.home.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Application home screen controller.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mNavigator: MainNavigator
    private var navController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject activity
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup toolbar
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            // Setup bottom navigation
            setupBottomNavigation()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.value?.navigateUp() ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate options menu
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Resolve menu selection
        when(item?.itemId) {
            // Navigate to settings screen
            R.id.action_settings -> mNavigator.openSettings()
        }

        return true
    }

    /**
     * Configures the navigation controller component for ui bottom navigation
     * view. This setup will allow the user to navigate to the destinations
     * presented by view menu items.
     */
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)!!
        val navGraphIds = listOf(mNavigator.getPhotosNavGraph())

        // Setup the bottom navigation view with a list of navigation graphs
        navController = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
    }
}
