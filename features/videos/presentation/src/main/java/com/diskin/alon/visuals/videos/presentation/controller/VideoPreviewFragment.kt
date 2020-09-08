package com.diskin.alon.visuals.videos.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diskin.alon.visuals.common.presentation.EspressoIdlingResource
import com.diskin.alon.visuals.videos.presentation.R
import kotlinx.android.synthetic.main.fragment_video_preview.*

/**
 * Video preview ui controller.
 */
class VideoPreviewFragment : Fragment() {

    companion object {
        const val KEY_VID_URI = "vid uri"
    }

    private var playerPrepared: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup videoView listeners
        videoView.setOnPreparedListener {
            // Seek to playback position
            videoView.seekTo(0)

            // Mute playback
            it.setVolume(0f,0f)

            // Start video
            videoView.start()

            if (!playerPrepared) {
                playerPrepared = true
                EspressoIdlingResource.decrement()
            }
        }
        videoView.setOnCompletionListener { videoView.seekTo(0) }

        // Set play video playback button listener
        val uri = arguments?.getParcelable<Uri>(KEY_VID_URI)!!
        playVideoButton.setOnClickListener {
            // Open video via available apps on user device
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri,getString(R.string.video_mime_type))
            }

            if (intent.resolveActivity(activity?.packageManager!!) != null) {
                startActivity(intent)
            } else {
                // If no player app exist, show error message to user
                Toast.makeText(
                    activity,
                    getString(R.string.no_player_error),
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val uri = arguments?.getParcelable<Uri>(KEY_VID_URI)!!

        EspressoIdlingResource.increment()
        videoView.setVideoURI(uri)
    }

    private fun releasePlayer() {
        videoView.stopPlayback()
    }
}
