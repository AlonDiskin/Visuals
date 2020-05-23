package com.diskin.alon.visuals.photos.presentation.controller

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.photos.presentation.model.Picture
import com.diskin.alon.visuals.photos.presentation.util.ImageLoader

/**
 * [Picture]s UI adapter.
 */
class PicturesAdapter(
    private val longClickListener: (Uri, View) -> Boolean,
    private val clickListener: (Uri,View) -> Unit,
    private val selectedVideoUri: List<Uri>
    ) : ListAdapter<Picture, PicturesAdapter.PictureViewHolder>(
    DIFF_CALLBACK
){

    var isMultiSelect: Boolean = false

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Picture>() {

            override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean
                    = oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: Picture, newItem: Picture)
                    = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.picture,parent,false)

        return PictureViewHolder(
            view,
            longClickListener,
            clickListener,
            selectedVideoUri
        )
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(getItem(position), isMultiSelect)
    }

    class PictureViewHolder(
        pictureItemView: View,
        private val longClickListener: (Uri,View) -> Boolean,
        private val clickListener: (Uri,View) -> Unit,
        private val selectedVideoUri: List<Uri>
    ) : RecyclerView.ViewHolder(pictureItemView) {

        private lateinit var picture: Picture
        private val pictureImageView: ImageView = pictureItemView.findViewById(R.id.pictureImage)
        private val selectableForeground: View = pictureItemView.findViewById(R.id.selectable_foreground)
        private val selectCheckBox: CheckBox =  pictureItemView.findViewById(R.id.select_item_checkBox)

        init {
            pictureItemView.setOnLongClickListener { longClickListener.invoke(picture.uri,it) }
            pictureItemView.setOnClickListener { clickListener.invoke(picture.uri,it) }
        }

        fun bind(picture: Picture, isMultiSelect: Boolean) {
            // Bind picture value
            this.picture = picture

            // Load picture into image view
            ImageLoader.loadImage(
                pictureImageView,
                picture.uri
            )

            // Resolve multi select ui if needed
            if (isMultiSelect) {
                this.selectableForeground.visibility =  View.VISIBLE
                this.selectCheckBox.isChecked = selectedVideoUri.contains(picture.uri)
            } else {
                this.selectableForeground.visibility =  View.INVISIBLE
                this.selectCheckBox.isChecked = false
            }
        }
    }
}