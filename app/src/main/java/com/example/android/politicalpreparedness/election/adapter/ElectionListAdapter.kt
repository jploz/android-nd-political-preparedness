package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    class ElectionViewHolder(private var binding: ItemElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election) {
            binding.election = item
            binding.executePendingBindings()
        }

        companion object {
            @LayoutRes
            val LAYOUT = R.layout.item_election

            fun from(parent: ViewGroup): ElectionViewHolder {
                val dataBinding: ItemElectionBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    LAYOUT,
                    parent,
                    false
                )
                return ElectionViewHolder(dataBinding)
            }
        }
    }

    class ElectionListener(val clickListener: (item: Election) -> Unit) {
        fun onClick(item: Election) = clickListener(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ElectionViewHolder = ElectionViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: ElectionViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.id.toLong()
    }

    companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {

        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem == newItem
        }
    }
}
