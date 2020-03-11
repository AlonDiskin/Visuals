package com.diskin.alon.visuals.photos.presentation

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_picture_detail.*

class PictureDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_detail)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load picture into layout
        val uriKey = getString(R.string.extra_pic_uri)
        val picUri = intent.extras?.getParcelable<Uri>(uriKey)!!

        ImageLoader.loadImage(pictureView, picUri)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
