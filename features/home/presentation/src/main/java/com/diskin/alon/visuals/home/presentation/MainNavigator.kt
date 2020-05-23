package com.diskin.alon.visuals.home.presentation

import androidx.annotation.NavigationRes

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
    @NavigationRes
    fun getPicturesNavGraph(): Int

    /**
     * Get the videos browser features navigation graph resource.
     */
    @NavigationRes
    fun getVideosNavGraph(): Int

    /**
     * Get the recycle bin features navigation graph resource.
     */
    @NavigationRes
    fun getRecycleBinNavGraph(): Int
}