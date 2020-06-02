package com.diskin.alon.visuals.recuclebin.presentation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_trashed_items.*
import javax.inject.Inject

/**
 * Trashed media items ui controller class.
 */
class TrashedItemsFragment : Fragment() {

    @Inject
    lateinit var viewModel: TrashedItemsViewModel

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
        return inflater.inflate(R.layout.fragment_trashed_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TrashedItemsAdapter()
        trashedList.adapter = adapter

        // Observe view model trash items list state
        viewModel.trashedItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate fragments app bar menu
        inflater.inflate(R.menu.menu_trashed_items,menu)

        // Set menu state
        when(viewModel.filter) {
            TrashedFilter.ALL -> menu.findItem(R.id.action_filter_all).isChecked = true
            TrashedFilter.PICTURE -> menu.findItem(R.id.action_filter_image).isChecked = true
            TrashedFilter.VIDEO -> menu.findItem(R.id.action_filter_video).isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!item.isChecked) {
            // Update view model filter state according to selection
            item.isChecked = true
            when(item.itemId) {
                R.id.action_filter_all -> viewModel.filter = TrashedFilter.ALL

                R.id.action_filter_video -> viewModel.filter = TrashedFilter.VIDEO

                R.id.action_filter_image -> {
                    viewModel.filter = TrashedFilter.PICTURE
                }
            }
        }

        return true
    }
}
