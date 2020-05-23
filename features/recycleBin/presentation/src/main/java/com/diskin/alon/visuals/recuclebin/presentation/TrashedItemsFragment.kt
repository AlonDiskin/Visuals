package com.diskin.alon.visuals.recuclebin.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        AndroidSupportInjection.inject(this)
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

        // Setup trashed items adapter
        val adapter = TrashedItemsAdapter()
        trashedList.adapter = adapter
        trashedList.itemAnimator = null

        // Observe view model state
        viewModel.trashedItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
