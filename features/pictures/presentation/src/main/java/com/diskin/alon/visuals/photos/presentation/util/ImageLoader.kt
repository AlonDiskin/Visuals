package com.diskin.alon.visuals.photos.presentation.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource

object ImageLoader {

    fun loadImage(imageView: ImageView, uri: Uri) {
        EspressoIdlingResource.increment()
        Glide
            .with(imageView.context)
            .load(uri)
            //.centerCrop()
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    EspressoIdlingResource.decrement()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
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

    fun loadImage(imageView: ImageView, uri: Uri, onError: () -> Unit) {
        EspressoIdlingResource.increment()
        Glide
            .with(imageView.context)
            .load(uri)
            //.centerCrop()
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    EspressoIdlingResource.decrement()
                    onError.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
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