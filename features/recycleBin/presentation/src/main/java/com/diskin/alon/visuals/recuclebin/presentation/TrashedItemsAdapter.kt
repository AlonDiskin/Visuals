package com.diskin.alon.visuals.recuclebin.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding

/**
 * [TrashedItem]s ui adapter.
 */
class TrashedItemsAdapter : ListAdapter<TrashedItem, TrashedItemsAdapter.TrashedItemViewHolder>(
    DIFF_CALLBACK
){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrashedItem>() {

            override fun areItemsTheSame(oldItem: TrashedItem, newItem: TrashedItem)
                    = oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: TrashedItem, newItem: TrashedItem)
                    = oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return this.getItem(position).type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashedItemViewHolder {
        val binding = when(TrashedItemType.values()[viewType]) {
            TrashedItemType.PICTURE -> {
                TrashedPictureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            }
            TrashedItemType.VIDEO -> {
                TrashedVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }
        }

        return TrashedItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashedItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrashedItemViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item:TrashedItem) {
            binding.setVariable(BR.trashedItem, item)
            binding.executePendingBindings()
        }
    }
}