package com.diskin.alon.visuals.recyclebin.featuretest.util

import android.net.Uri
import androidx.databinding.ViewDataBinding
import com.diskin.alon.visuals.recuclebin.presentation.controller.TrashItemsAdapter.TrashItemViewHolder
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withTrashItemUri(uri: Uri): Matcher<TrashItemViewHolder> {
    return object : Matcher<TrashItemViewHolder> {
        override fun describeTo(description: Description?) {
            description?.appendText(
                "TrashItemViewHolder with uri: $uri"
            )
        }

        override fun matches(item: Any): Boolean {
            val viewHolder: TrashItemViewHolder = item as TrashItemViewHolder
            val field = TrashItemViewHolder::class.java.getDeclaredField("binding")
            field.isAccessible = true

            return when (val binding = field.get(viewHolder) as ViewDataBinding) {
                is TrashedPictureBinding -> {
                    binding.trashedItem?.uri == uri

                }
                is TrashedVideoBinding -> {
                    binding.trashedItem?.uri == uri
                }
                else -> {
                    false
                }
            }
        }

        override fun describeMismatch(item: Any?, mismatchDescription: Description?) {

        }

        override fun _dont_implement_Matcher___instead_extend_BaseMatcher_() {

        }
    }
}