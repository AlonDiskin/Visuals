package com.diskin.alon.visuals

import android.app.Application
import com.diskin.alon.visuals.home.presentation.MainNavigator
import javax.inject.Inject

/**
 * Applies navigation to app destinations.
 */
class AppNavigator @Inject constructor(private val app: Application) : MainNavigator {

    override fun getPicturesNavGraph(): Int {
        return R.navigation.pictures_nav_graph
    }

    override fun getVideosNavGraph(): Int {
        return R.navigation.videos_nav_graph
    }

    override fun getRecycleBinNavGraph(): Int {
        return R.navigation.recycle_bin_nav_graph
    }
}