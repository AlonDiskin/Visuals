package com.diskin.alon.visuals.recuclebin.presentation.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource

@BindingAdapter("longClickListener","uri")
fun onLongClick(view: View,listener: ((View,Uri) -> Unit)?,uri: Uri?) {
    if (listener != null && uri != null) {
        view.setOnLongClickListener {
            listener.invoke(view,uri)
            true
        }
    }
}

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, uri: Uri?) {
    uri?.let {
        EspressoIdlingResource.increment()
        Glide
            .with(imageView.context)
            .load(uri)
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
}

@BindingAdapter("loadVideoThumbnail")
fun loadThumbnail(imageView: ImageView, uri: Uri?) {
    uri?.let {
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
