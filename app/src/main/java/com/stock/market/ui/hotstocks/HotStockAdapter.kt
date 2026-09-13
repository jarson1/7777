package com.stock.market.ui.hotstocks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stock.market.R
import com.stock.market.databinding.ItemHotStockBinding
import com.stock.market.model.HotStock

/**
 * 热股排名适配器
 */
class HotStockAdapter : ListAdapter<HotStock, HotStockAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHotStockBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHotStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HotStock) {
            // 排名
            binding.tvRank.text = item.rank.toString()
            val rankBg = when (item.rank) {
                1 -> R.drawable.bg_rank_first
                2 -> R.drawable.bg_rank_second
                3 -> R.drawable.bg_rank_third
                else -> R.drawable.bg_rank_normal
            }
            binding.tvRank.setBackgroundResource(rankBg)

            // 股票名称和代码
            binding.tvStockName.text = item.name
            binding.tvStockCode.text = item.code

            // 市场标签
            binding.tvMarketTag.text = item.market
            binding.tvMarketTag.setBackgroundResource(
                if (item.market == "A股") R.drawable.bg_market_a else R.drawable.bg_market_us
            )

            // 最新价
            binding.tvPrice.text = String.format("%.2f", item.price)

            // 涨跌幅
            val changeText = if (item.isUp) {
                "+${String.format("%.2f", item.changePercent)}%"
            } else {
                "${String.format("%.2f", item.changePercent)}%"
            }
            binding.tvChangePercent.text = changeText
            val colorRes = if (item.isUp) R.color.stock_up else R.color.stock_down
            binding.tvChangePercent.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )

            // 热度
            binding.tvHeat.text = "热度 ${item.heat}"
            binding.progressBarHeat.progress = (item.heat / 100).coerceAtMost(100)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HotStock>() {
        override fun areItemsTheSame(oldItem: HotStock, newItem: HotStock): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: HotStock, newItem: HotStock): Boolean {
            return oldItem == newItem
        }
    }
}
