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
     * Get the pictures browser features navigation graph resource.
     */
    fun getPicturesNavGraph(): Int

    /**
     * Get the videos browser features navigation graph resource.
     */
    fun getVideosNavGraph(): Int
}