package com.diskin.alon.visuals.videos.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_videos.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class VideosFragment : Fragment() {

    @Inject
    lateinit var viewModel: VideosViewModel

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
        val adapter = VideosAdapter()
        videosList.adapter = adapter

        viewModel.videos.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.videosUpdateFail.observe(this, Observer {failMessage ->
            Toast.makeText(activity,failMessage, Toast.LENGTH_LONG)
                .show()
        })
    }
}
