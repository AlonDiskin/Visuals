package com.diskin.alon.visuals.home.presentation

/**
 * Main screen navigator contract.
 */
interface MainNavigator {

    /**
     * Open settings screen.
     */
    fun openSettings()

    /**
     * Get the photos features navigation graph resource.
     */
    fun getPhotosNavGraph(): Int
}