package com.diskin.alon.visuals.catalog.presentation.controller

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diskin.alon.visuals.catalog.presentation.util.ImageLoader
import com.diskin.alon.visuals.photos.presentation.R
import kotlinx.android.synthetic.main.fragment_picture.*

class PictureFragment : Fragment() {

    companion object {
        const val KEY_PIC_URI = "pic uri"
    }

    /**
     * Create a new [PictureFragment] instance.
     *
     * @param picUri the [Uri] of the device picture to show in fragment ui.
     */
    fun newInstance(picUri: Uri): PictureFragment {
        val fragment =
            PictureFragment()
        val args = Bundle()

        args.putParcelable(KEY_PIC_URI, picUri)
        fragment.arguments = args

        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load pic into image view
        val picUri: Uri = arguments?.getParcelable(KEY_PIC_URI)!!
        ImageLoader.loadImage(
            pictureView,
            picUri)
        {
            Toast.makeText(
                activity,
                getString(R.string.picture_image_loading_error),
                Toast.LENGTH_LONG)
                .show()
        }
    }
}
