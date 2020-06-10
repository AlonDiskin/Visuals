package com.diskin.alon.visuals.recuclebin.presentation.controller

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.visuals.recuclebin.presentation.BR
import com.diskin.alon.visuals.recuclebin.presentation.R
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItemType
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding
import com.diskin.alon.visuals.recuclebin.presentation.model.TrashItem

/**
 * [TrashItem]s ui adapter.
 */
class TrashItemsAdapter(
    private val selectedVideoUri: List<Uri>,
    private val itemLongClickListener: (View, Uri) -> Unit,
    private val itemClickListener: (View, Uri) -> Unit,
    var isMultiSelect: Boolean = false
) : ListAdapter<TrashItem, TrashItemsAdapter.TrashItemViewHolder>(
    DIFF_CALLBACK
){
    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrashItem>() {

            override fun areItemsTheSame(oldItem: TrashItem, newItem: TrashItem): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: TrashItem, newItem: TrashItem) =
                oldItem == newItem

        }
    }

    override fun getItemViewType(position: Int): Int {
        return this.getItem(position).type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashItemViewHolder {
        val binding = when(TrashItemType.values()[viewType]) {
            TrashItemType.PICTURE -> {
                TrashedPictureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            }
            TrashItemType.VIDEO -> {
                TrashedVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }
        }

        return TrashItemViewHolder(
            binding,
            itemLongClickListener,
            itemClickListener,
            selectedVideoUri
        )
    }

    override fun onBindViewHolder(holder: TrashItemViewHolder, position: Int) {
        holder.bind(getItem(position),isMultiSelect)
    }

    class TrashItemViewHolder(
        private val binding: ViewDataBinding,
        longClickListener: (View,Uri) -> Unit,
        clickListener: (View, Uri) -> Unit,
        private val selectedVideoUri: List<Uri>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val selectableForeground: View =
            binding.root.findViewById(R.id.selectable_foreground)
        private val selectCheckBox: CheckBox =
            binding.root.findViewById(R.id.select_item_checkBox)

        init {
            binding.setVariable(BR.longClickListener,longClickListener)
            binding.setVariable(BR.clickListener,clickListener)
            binding.executePendingBindings()
        }

        fun bind(item: TrashItem, isMultiSelect: Boolean) {
            binding.setVariable(BR.trashedItem, item)
            binding.executePendingBindings()

            if (isMultiSelect) {
                this.selectableForeground.visibility =  View.VISIBLE
                this.selectCheckBox.isChecked = selectedVideoUri.contains(item.uri)
            } else {
                this.selectableForeground.visibility =  View.INVISIBLE
                this.selectCheckBox.isChecked = false
            }
        }
    }
}