package com.diskin.alon.visuals.photos.presentation


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_pictures.*
import javax.inject.Inject

/**
 * Ui controller for photos browser ui features.
 */
class PicturesFragment : Fragment() {

    @Inject
    lateinit var viewModel: PicturesViewModel

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
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup photos adapter
        val photosAdapter = PicturesAdapter()
        pictures_list.adapter = photosAdapter

        // Observe view model state
        viewModel.photos.observe(viewLifecycleOwner, Observer {
            // Submit list update
            photosAdapter.submitList(it)
        })

        viewModel.photosUpdateError.observe(this, Observer { failMessage ->
            Toast.makeText(activity,failMessage,Toast.LENGTH_LONG)
                .show()
        })
    }
}
