package com.diskin.alon.visuals.util

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
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

fun isRecyclerViewItemsCount(size: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("with items count:${size}")
        }

        override fun matchesSafely(item: RecyclerView): Boolean {
            return item.adapter!!.itemCount == size
        }

    }
}