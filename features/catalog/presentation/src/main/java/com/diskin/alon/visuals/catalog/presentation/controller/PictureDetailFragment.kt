package com.diskin.alon.visuals.catalog.presentation.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.photos.presentation.databinding.FragmentPictureDetailBinding
import com.diskin.alon.visuals.catalog.presentation.viewmodel.PictureDetailViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PictureDetailFragment : Fragment() {

    @Inject
    lateinit var viewModel: PictureDetailViewModel
    private lateinit var layoutBinding: FragmentPictureDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject view model
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_picture_detail,
            container,
            false)

        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Observe view model state
        viewModel.pictureDetail.observe(viewLifecycleOwner, Observer {
            it?.let { layoutBinding.detail = it }
        })

        viewModel.pictureError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(
                    activity,
                    getString(R.string.picture_detail_loading_error),
                    Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}
