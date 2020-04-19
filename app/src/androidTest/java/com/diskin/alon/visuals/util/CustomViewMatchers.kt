package com.diskin.alon.visuals.util

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun isVideoViewPlayingWithUri(uri: Uri): Matcher<View> {
    return object : BoundedMatcher<View, VideoView>(VideoView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("with started video view")
        }

        override fun matchesSafely(item: VideoView): Boolean {
            val filed = VideoView::class.java.getDeclaredField("mUri")
            filed.isAccessible = true

            return item.isPlaying && (uri == filed.get(item))
        }
    }
}