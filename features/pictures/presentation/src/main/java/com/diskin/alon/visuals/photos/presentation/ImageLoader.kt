package com.diskin.alon.visuals.photos.presentation

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource

object ImageLoader {

    fun loadImage(imageView: ImageView, photo: Picture) {
        EspressoIdlingResource.increment()
        Glide
            .with(imageView.context)
            .load(photo.uri)
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
                    imageView.tag = photo.uri.toString()
                    EspressoIdlingResource.decrement()
                    return false
                }
            })
            .into(imageView)
    }
}