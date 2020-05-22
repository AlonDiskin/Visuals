package com.diskin.alon.visuals.home.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST = 10
    }

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
            initNavigation()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        initNavigation()
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
            R.id.action_settings -> {
                mNavigator.openSettings()
            }
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
        val navGraphIds = listOf(
            mNavigator.getPicturesNavGraph(),
            mNavigator.getVideosNavGraph(),
            mNavigator.getRecycleBinNavGraph()
        )

        // Setup the bottom navigation view with a list of navigation graphs
        navController = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
    }

    private fun initNavigation() {
        // Check runtime permission for storage access since app feature all require this permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, setup bottom navigation
            setupBottomNavigation()

        } else {
            // Permission is not yet granted
            // Ask the user for the needed permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, setupbottom navigation
                setupBottomNavigation()

            } else {
                // permission denied, Disable the
                // navigation to app features setup that depends on this permission.
                Toast.makeText(this,getString(R.string.permission_deny_message), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
