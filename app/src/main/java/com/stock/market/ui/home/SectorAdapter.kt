package com.stock.market.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stock.market.R
import com.stock.market.databinding.ItemSectorBinding
import com.stock.market.model.Sector

/**
 * 产业板块适配器
 */
class SectorAdapter : ListAdapter<Sector, SectorAdapter.SectorViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectorViewHolder {
        val binding = ItemSectorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SectorViewHolder(private val binding: ItemSectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sector: Sector) {
            binding.tvIcon.text = sector.icon
            binding.tvSectorName.text = sector.name

            val changeText = if (sector.isUp) {
                "▲+${String.format("%.2f", sector.changePercent)}%"
            } else {
                "▼${String.format("%.2f", sector.changePercent)}%"
            }
            binding.tvChangePercent.text = changeText

            val colorRes = if (sector.isUp) R.color.stock_up else R.color.stock_down
            binding.tvChangePercent.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Sector>() {
        override fun areItemsTheSame(oldItem: Sector, newItem: Sector): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Sector, newItem: Sector): Boolean {
            return oldItem == newItem
        }
    }
}
