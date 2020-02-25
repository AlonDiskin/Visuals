package com.diskin.alon.visuals.videos.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * [Video]s UI adapter.
 */
class VideosAdapter : ListAdapter<Video, VideosAdapter.VideoViewHolder>(DIFF_CALLBACK){

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

        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VideoViewHolder(videoView: View) : RecyclerView.ViewHolder(videoView) {

        private val videoImageView: ImageView = videoView.findViewById(R.id.videoThumb)
        private val videoDuration: TextView = videoView.findViewById(R.id.videoDuration)

        fun bind(video: Video) {
            // Bind video info to view holder
            videoDuration.text = video.duration.getFormattedDuration()
            ThumbnailLoader.load(videoImageView,video.uri)
        }
    }
}