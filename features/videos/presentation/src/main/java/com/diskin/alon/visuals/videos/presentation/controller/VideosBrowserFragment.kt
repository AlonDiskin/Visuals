package com.diskin.alon.visuals.videos.presentation.controller

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.diskin.alon.visuals.common.presentation.Event.Status
import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideosBrowserViewModel
import com.google.android.material.snackbar.Snackbar
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
    private var snackbar: Snackbar? = null

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

        // Observe view model state
        viewModel.videos.observe(viewLifecycleOwner, Observer {
            this.adapter.submitList(it)
        })

        viewModel.videosUpdateFail.observe(viewLifecycleOwner, Observer {failMessage ->
            Toast.makeText(activity,failMessage, Toast.LENGTH_LONG)
                .show()
        })

        viewModel.videosTrashedEvent.observe(viewLifecycleOwner, Observer {
            it?.let { event ->
                when(event.status) {
                    Status.SUCCESS -> {
                        // Show snackbar with success message and 'undo'action, that will
                        // undo last videos trashing
                        snackbar = Snackbar.make(
                            videosList,
                            getString(R.string.trashing_success_message),
                            Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.title_undo_trash)) {
                                viewModel.undoLastTrash()
                            }

                        snackbar?.show()
                    }

                    Status.FAILURE -> {
                        Toast.makeText(
                            activity,
                            getString(R.string.trashing_failure_message),
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })

        viewModel.videosTrashUndoEvent.observe(viewLifecycleOwner, Observer {
            it?.let {event ->
                if (event.status == Status.FAILURE) {
                    Toast.makeText(
                        activity,
                        getString(R.string.trashing_undo_failure_message),
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
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
                actionMode?.finish()
            }

        } else {
            // Navigate to video detail screen
            val args = Bundle().apply { putParcelable(getString(R.string.extra_vid_uri),uri) }
            findNavController().navigate(R.id.videoDetail,args)
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

    private fun trashVideos() {
        AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.trash_dialog_message))
            .setTitle(getString(R.string.trash_dialog_title))
            .setPositiveButton(getString(R.string.dialog_pos_label)) { _, _ ->
                viewModel.trashVideos(selectedVideosUri.toList())
                actionMode?.finish()
            }
            .setNegativeButton(getString(R.string.dialog_neg_label),null)
            .create()
            .show()
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.title) {
            getString(R.string.action_share_title) -> shareVideos(selectedVideosUri)
            getString(R.string.action_trash_title) -> trashVideos()
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

        menu?.add(getString(R.string.action_trash_title))?.setIcon(
            R.drawable.ic_trash_24dp
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

        snackbar?.dismiss()
    }
}
