package com.diskin.alon.visuals.videos.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_videos.*
import javax.inject.Inject

/**
 * UI controller class for video browser features.
 */
class VideosBrowserFragment : Fragment(), ActionMode.Callback {

    companion object {
        private const val KEY_ACTION_MODE = "action_mode"
        private const val KEY_SELECTED_URI = "selected_uri"
    }

    @Inject
    lateinit var viewModel: VideosBrowserViewModel
    private var multiSelect: Boolean = false
    private val selectedVideosUri: MutableList<Uri> = mutableListOf()
    private var actionMode: ActionMode? = null
    private lateinit var adapter: VideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.adapter =
            VideosAdapter(
                this::onVideoLongClick,
                this::onVideoClick,
                selectedVideosUri
            )
        videosList.adapter = this.adapter

        viewModel.videos.observe(viewLifecycleOwner, Observer {
            this.adapter.submitList(it)
        })

        viewModel.videosUpdateFail.observe(this, Observer {failMessage ->
            Toast.makeText(activity,failMessage, Toast.LENGTH_LONG)
                .show()
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            if (it.getBoolean(KEY_ACTION_MODE)) {
                @Suppress("UNCHECKED_CAST")
                this.selectedVideosUri.addAll(
                    (it.getParcelableArray(KEY_SELECTED_URI) as Array<Uri>).toMutableList()
                )

                this.actionMode = activity?.startActionMode(this)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_ACTION_MODE,this.multiSelect )
        outState.putParcelableArray(KEY_SELECTED_URI,this.selectedVideosUri.toTypedArray())

        super.onSaveInstanceState(outState)
    }

    private fun onVideoClick(uri: Uri,view: View) {
        if(multiSelect) {
            // Add video to selected videos for sharing
            val checkBox = view.findViewById<CheckBox>(R.id.select_item_checkBox)
            if (selectedVideosUri.contains(uri)) {
                selectedVideosUri.remove(uri)
                checkBox.isChecked = false
            } else {
                selectedVideosUri.add(uri)
                checkBox.isChecked = true
            }

            if (selectedVideosUri.isEmpty()) {
                //this.onDestroyActionMode(this.actionMode)
                actionMode?.finish()
            }

        } else {
            // Open video player screen
            val intent = Intent(activity, VideoPlayerActivity::class.java).apply {
                putExtra(getString(R.string.extra_vid_uri), uri)
            }

            startActivity(intent)
        }
    }

    private fun onVideoLongClick(uri: Uri,view: View): Boolean {
        if (!multiSelect) {
            actionMode =  activity?.startActionMode(this)
            view.findViewById<CheckBox>(R.id.select_item_checkBox).isChecked = true
            selectedVideosUri.add(uri)
        }

        return true
    }

    private fun shareVideos(videoUri: List<Uri>) {
        activity?.let {activity ->
            if (videoUri.isNotEmpty()) {
                val intentBuilder = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType(getString(R.string.video_uri_mime_type))
                    .setChooserTitle(getString(R.string.share_sheet_title))

                videoUri.forEach { uri ->
                    intentBuilder
                        .addStream(uri)
                }

                intentBuilder.startChooser()
            }
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.title) {
            getString(R.string.action_share_title) -> shareVideos(selectedVideosUri)
        }

        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        multiSelect = true
        this.adapter.isMultiSelect = true

        this.adapter.notifyDataSetChanged()

        menu?.add(getString(R.string.action_share_title))?.setIcon(
            R.drawable.ic_share_24dp
        )

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        this.multiSelect = false
        this.adapter.isMultiSelect = false

        this.adapter.notifyDataSetChanged()
        selectedVideosUri.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (multiSelect) {
            actionMode?.finish()
        }
    }
}
