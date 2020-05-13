package com.diskin.alon.visuals.videos.presentation.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource

object ThumbnailLoader {

    fun load(imageView: ImageView, uri: Uri) {
        EspressoIdlingResource.increment()
        Glide
            .with(imageView.context)
            .asBitmap()
            .load(uri)
            .addListener( object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    EspressoIdlingResource.decrement()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.tag = uri.toString()
                    EspressoIdlingResource.decrement()
                    return false
                }
            })
            .into(imageView)
    }
}