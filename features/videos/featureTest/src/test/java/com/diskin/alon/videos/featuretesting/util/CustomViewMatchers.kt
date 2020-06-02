package com.diskin.alon.videos.featuretesting.util

import android.net.Uri
import com.diskin.alon.visuals.videos.presentation.controller.VideosAdapter.VideoViewHolder
import com.diskin.alon.visuals.videos.presentation.model.Video
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withVideoUri(uri: Uri): Matcher<VideoViewHolder> {
    return object : Matcher<VideoViewHolder> {
        override fun describeTo(description: Description?) {
            description?.appendText(
                "VideoViewHolder with uri: $uri"
            )
        }

        override fun matches(item: Any?): Boolean {
            val viewHolder: VideoViewHolder = item as VideoViewHolder
            val field = VideoViewHolder::class.java.getDeclaredField("video")
            field.isAccessible = true
            val video = field.get(viewHolder) as Video

            return video.uri == uri
        }

        override fun describeMismatch(item: Any?, mismatchDescription: Description?) {

        }

        override fun _dont_implement_Matcher___instead_extend_BaseMatcher_() {

        }
    }
}