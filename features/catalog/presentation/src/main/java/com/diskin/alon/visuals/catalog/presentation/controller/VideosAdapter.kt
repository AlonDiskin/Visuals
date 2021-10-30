package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.model.Video
import com.diskin.alon.visuals.catalog.presentation.util.ThumbnailLoader

/**
 * [Video]s UI adapter.
 */
class VideosAdapter(
    private val longClickListener: (Uri,View) -> Boolean,
    private val clickListener: (Uri,View) -> Unit,
    private val selectedVideoUri: List<Uri>,
    var isMultiSelect: Boolean = false
) : ListAdapter<Video, VideosAdapter.VideoViewHolder>(
    DIFF_CALLBACK
){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video)
                    = oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: Video, newItem: Video)
                    = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video,parent,false)

        return VideoViewHolder(
            view,
            longClickListener,
            clickListener,
            selectedVideoUri
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position),isMultiSelect)
    }

    class VideoViewHolder(
        videoView: View,
        private val longClickListener: (Uri,View) -> Boolean,
        private val clickListener: (Uri,View) -> Unit,
        private val selectedVideoUri: List<Uri>
    ) : RecyclerView.ViewHolder(videoView) {

        private lateinit var video: Video
        private val videoImageView: ImageView = videoView.findViewById(R.id.videoThumb)
        private val videoDuration: TextView = videoView.findViewById(R.id.videoDuration)
        private val selectableForeground: View = videoView.findViewById(R.id.selectable_foreground)
        private val selectCheckBox: CheckBox =  videoView.findViewById(R.id.select_item_checkBox)

        init {
            videoView.setOnLongClickListener { longClickListener.invoke(video.uri,it) }
            videoView.setOnClickListener { clickListener.invoke(video.uri,it) }
        }

        fun bind(video: Video, isMultiSelect: Boolean) {
            // Bind video info to view holder
            this.video = video
            this.videoDuration.text = video.duration.getFormattedDuration()
            ThumbnailLoader.load(
                videoImageView,
                video.uri
            )

            if (isMultiSelect) {
                this.selectableForeground.visibility =  View.VISIBLE
                this.selectCheckBox.isChecked = selectedVideoUri.contains(video.uri)
            } else {
                this.selectableForeground.visibility =  View.INVISIBLE
                this.selectCheckBox.isChecked = false
            }
        }
    }
}