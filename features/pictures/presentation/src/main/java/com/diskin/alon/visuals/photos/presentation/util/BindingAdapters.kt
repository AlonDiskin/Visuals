package com.diskin.alon.visuals.photos.presentation.util

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.diskin.alon.visuals.photos.presentation.R
import com.diskin.alon.visuals.photos.presentation.model.PictureDetail
import java.text.SimpleDateFormat

@BindingAdapter("pictureSize")
fun setPictureSize(textView: TextView, picDetail: PictureDetail?) {
    picDetail?.let {
        textView.text = textView.context.getString(
            R.string.pic_size_template,
            it.size.toString())
    }
}

@BindingAdapter("pictureDate")
fun setPictureDate(textView: TextView, picDetail: PictureDetail?) {
    picDetail?.let {
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(textView.context.getString(R.string.pic_date_format))
        textView.text = format.format(it.date)
    }
}

@BindingAdapter("pictureResolution")
fun setPictureResolution(textView: TextView, picDetail: PictureDetail?) {
    picDetail?.let {
        textView.text = textView.context.getString(
            R.string.pic_resolution_template,
            it.width.toString(),
            it.height.toString()
        )
    }
}
