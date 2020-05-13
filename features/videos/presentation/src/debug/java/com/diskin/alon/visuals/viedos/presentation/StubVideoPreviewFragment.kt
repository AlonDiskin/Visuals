package com.diskin.alon.visuals.viedos.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.diskin.alon.visuals.videos.presentation.R

/**
 * A simple [Fragment] subclass.
 */
class StubVideoPreviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stub_video_preview, container, false)
    }

}
