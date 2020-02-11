package com.diskin.alon.visuals.photos.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * [Picture]s UI adapter.
 */
class PicturesAdapter : ListAdapter<Picture, PicturesAdapter.PictureViewHolder>(DIFF_CALLBACK){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Picture>() {
            override fun areItemsTheSame(oldItem: Picture, newItem: Picture)
                    = oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: Picture, newItem: Picture)
                    = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.picture,parent,false)

        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PictureViewHolder(photoItemView: View) : RecyclerView.ViewHolder(photoItemView) {

        private val photoImageView = photoItemView as ImageView

        fun bind(photo: Picture) {
            // Load picture into image view
            ImageLoader.loadImage(this.photoImageView,photo)
        }
    }
}