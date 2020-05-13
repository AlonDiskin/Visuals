package com.diskin.alon.visuals.videos.presentation.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.diskin.alon.visuals.videos.presentation.R
import com.diskin.alon.visuals.videos.presentation.databinding.FragmentVideoDetailBinding
import com.diskin.alon.visuals.videos.presentation.viewmodel.VideoDetailViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Video info ui controller class.
 */
class VideoDetailFragment : Fragment() {

    @Inject
    lateinit var viewModel: VideoDetailViewModel
    private lateinit var layoutBinding: FragmentVideoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject fragment collaborators
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_video_detail,
            container,
            false)

        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe view model state
        viewModel.videoDetail.observe(viewLifecycleOwner, Observer {
            // Bind info update to layout
            it?.let { layoutBinding.videoDetail = it }
        })

        viewModel.videoInfoError.observe(viewLifecycleOwner, Observer {
            // Notify user about error with toast message
            it?.let { error ->
                Toast.makeText(
                    activity,
                    error.message,
                    Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}
