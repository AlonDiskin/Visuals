package com.diskin.alon.visuals.recuclebin.presentation.controller

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.diskin.alon.visuals.common.presentation.Event
import com.diskin.alon.visuals.recuclebin.presentation.R
import com.diskin.alon.visuals.recuclebin.presentation.viewmodel.TrashBrowserViewModel
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashedFilter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_trash_browser.*
import javax.inject.Inject

/**
 * Trashed media items ui controller class.
 */
class TrashBrowserFragment : Fragment(), ActionMode.Callback {

    companion object {
        private const val KEY_ACTION_MODE = "action_mode"
        private const val KEY_SELECTED_URI = "selected_uri"
    }

    @Inject
    lateinit var viewModel: TrashBrowserViewModel
    private var actionMode: ActionMode? = null
    private var multiSelect: Boolean = false
    private val selectedItemsUri: MutableList<Uri> = mutableListOf()
    private lateinit var adapter: TrashItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject fragment
        AndroidSupportInjection.inject(this)

        // Indicate fragment contribute to app bar menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trash_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup trashed items recycle view
        adapter =
            TrashItemsAdapter(
                selectedItemsUri,
                this::onTrashedItemLongClick,
                this::onTrashedItemClick
            )
        trashList.adapter = adapter

        // Observe view model state
        viewModel.trashItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.restoreEvent.observe(viewLifecycleOwner, Observer {
            // Notify user about items restoration event
            it?.let { event ->
                val message = when(event.status) {
                    Event.Status.FAILURE -> "Items restore fail!"
                    else -> "Items restored"
                }

                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            if (it.getBoolean(KEY_ACTION_MODE)) {
                @Suppress("UNCHECKED_CAST")
                selectedItemsUri.addAll(
                    (it.getParcelableArray(KEY_SELECTED_URI) as Array<Uri>).toMutableList()
                )

                activity?.startActionMode(this)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_ACTION_MODE,this.multiSelect )
        outState.putParcelableArray(KEY_SELECTED_URI,selectedItemsUri.toTypedArray())

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate fragments app bar menu
        inflater.inflate(R.menu.menu_trash_browser,menu)

        // Set menu state
        when(viewModel.filter) {
            TrashedFilter.ALL -> menu.findItem(R.id.action_filter_all).isChecked = true
            TrashedFilter.PICTURE -> menu.findItem(R.id.action_filter_image).isChecked = true
            TrashedFilter.VIDEO -> menu.findItem(R.id.action_filter_video).isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_filter_all -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.filter = TrashedFilter.ALL
                }
            }

            R.id.action_filter_video -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.filter = TrashedFilter.VIDEO
                }
            }

            R.id.action_filter_image -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.filter = TrashedFilter.PICTURE
                }
            }

            R.id.action_restore_all -> restoreAllTrashedItems()
        }

        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.title) {
            getString(R.string.action_restore) -> viewModel.restore(selectedItemsUri.toList())
        }

        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        actionMode = mode
        multiSelect = true
        adapter.isMultiSelect = true
        adapter.notifyDataSetChanged()

        menu?.add(getString(R.string.action_restore))?.setIcon(
            R.drawable.ic_restore_24
        )

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        multiSelect = false
        adapter.isMultiSelect = false

        adapter.notifyDataSetChanged()
        selectedItemsUri.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
    }

    private fun onTrashedItemLongClick(view: View,uri: Uri) {
        if (!multiSelect) {
            activity?.startActionMode(this)
            view.findViewById<CheckBox>(R.id.select_item_checkBox).isChecked = true
            selectedItemsUri.add(uri)
        }
    }

    private fun onTrashedItemClick(view: View,uri: Uri) {
        if(multiSelect) {
            // Add video to selected videos for sharing
            val checkBox = view.findViewById<CheckBox>(R.id.select_item_checkBox)
            if (selectedItemsUri.contains(uri)) {
                selectedItemsUri.remove(uri)
                checkBox.isChecked = false

            } else {
                selectedItemsUri.add(uri)
                checkBox.isChecked = true
            }

            if (selectedItemsUri.isEmpty()) {
                actionMode?.finish()
            }
        }
    }

    private fun restoreAllTrashedItems() {
        AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.restore_all_dialog_message))
            .setTitle(getString(R.string.restore_all_dialog_title))
            .setPositiveButton(getString(R.string.dialog_pos_label)) { _, _ ->
                viewModel.restoreAll() }
            .setNegativeButton(getString(R.string.dialog_neg_label),null)
            .create()
            .show()
    }
}
