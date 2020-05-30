package com.diskin.alon.visuals.recuclebin.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedPictureBinding
import com.diskin.alon.visuals.recuclebin.presentation.databinding.TrashedVideoBinding
import java.util.concurrent.Executor

/**
 * [TrashedItem]s ui adapter.
 */
class TrashedItemsAdapter(executor: Executor? = null) : ListAdapter<TrashedItem, TrashedItemsAdapter.TrashedItemViewHolder>(
    AsyncDifferConfig.Builder(DIFF_CALLBACK)
        .setBackgroundThreadExecutor(executor)
        .build()
){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrashedItem>() {

            override fun areItemsTheSame(oldItem: TrashedItem, newItem: TrashedItem): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: TrashedItem, newItem: TrashedItem) =
                oldItem == newItem

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