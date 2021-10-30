package com.diskin.alon.visuals.catalog.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PicturesBrowserViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_pictures_browser.*
import javax.inject.Inject

/**
 * Ui controller for photos browser ui features.
 */
class PicturesBrowserFragment : Fragment(), ActionMode.Callback {

    companion object {
        private const val KEY_ACTION_MODE = "action_mode"
        private const val KEY_SELECTED_URI = "selected_uri"
    }

    @Inject
    lateinit var viewModel: PicturesBrowserViewModel
    private var multiSelect: Boolean = false
    private val selectedPicturesUri: MutableList<Uri> = mutableListOf()
    private var actionMode: ActionMode? = null
    private val picturesAdapter =
        PicturesAdapter(
            this::onPictureLongClick,
            this::onPictureClick,
            selectedPicturesUri
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject fragment
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictures_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup photos adapter
        pictures_list.adapter = picturesAdapter

        // Observe view model state
        viewModel.photos.observe(viewLifecycleOwner, Observer {
            // Submit list update
            picturesAdapter.submitList(it)
        })

        viewModel.photosUpdateError.observe(this, Observer { failMessage ->
            Toast.makeText(activity,failMessage,Toast.LENGTH_LONG)
                .show()
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            if (it.getBoolean(KEY_ACTION_MODE)) {
                @Suppress("UNCHECKED_CAST")
                this.selectedPicturesUri.addAll(
                    (it.getParcelableArray(KEY_SELECTED_URI) as Array<Uri>).toMutableList()
                )

                this.actionMode = activity?.startActionMode(this)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_ACTION_MODE,this.multiSelect )
        outState.putParcelableArray(KEY_SELECTED_URI,this.selectedPicturesUri.toTypedArray())

        super.onSaveInstanceState(outState)
    }

    private fun onPictureLongClick(uri: Uri,view: View): Boolean {
        if (!multiSelect) {
            actionMode =  activity?.startActionMode(this)
            view.findViewById<CheckBox>(R.id.select_item_checkBox).isChecked = true
            selectedPicturesUri.add(uri)
        }

        return true
    }

    private fun onPictureClick(uri: Uri,view: View) {
        if(multiSelect) {
            val checkBox = view.findViewById<CheckBox>(R.id.select_item_checkBox)
            if (selectedPicturesUri.contains(uri)) {
                selectedPicturesUri.remove(uri)
                checkBox.isChecked = false
            } else {
                selectedPicturesUri.add(uri)
                checkBox.isChecked = true
            }

            if (selectedPicturesUri.isEmpty()) {
                actionMode?.finish()
            }

        } else {
            val intent = Intent(activity, PictureViewerActivity::class.java).apply {
                putExtra(getString(R.string.extra_pic_uri), uri)
            }

            startActivity(intent)
        }
    }

    private fun sharePictures(picturesUri: List<Uri>) {
        activity?.let {activity ->
            if (picturesUri.isNotEmpty()) {
                // Create a share intent via builder
                val intentBuilder = ShareCompat.IntentBuilder
                    .from(activity)
                    .setType(getString(R.string.mime_type_image))
                    .setChooserTitle(getString(R.string.share_pictures_sheet_title))

                // Add all selected picture uris
                picturesUri.forEach { uri ->
                    intentBuilder
                        .addStream(uri)
                }

                // Show android share sheet ui
                intentBuilder.startChooser()
            }
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.title) {
            getString(R.string.action_share_pictures) -> sharePictures(selectedPicturesUri)
        }

        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        multiSelect = true
        picturesAdapter.isMultiSelect = true

        picturesAdapter.notifyDataSetChanged()
        menu?.add(getString(R.string.action_share_pictures))?.setIcon(
            R.drawable.ic_share_24dp
        )
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        multiSelect = false
        picturesAdapter.isMultiSelect = false

        picturesAdapter.notifyDataSetChanged()
        selectedPicturesUri.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (multiSelect) {
            actionMode?.finish()
        }

    }
}
