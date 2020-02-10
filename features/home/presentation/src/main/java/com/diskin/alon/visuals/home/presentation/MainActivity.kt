package com.diskin.alon.visuals.home.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Application home screen controller.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mNavigator: MainNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject activity
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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
}
