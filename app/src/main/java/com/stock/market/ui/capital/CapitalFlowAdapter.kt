package com.stock.market.ui.capital

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stock.market.R
import com.stock.market.databinding.ItemCapitalFlowBinding
import com.stock.market.model.CapitalFlow

/**
 * 资金流入列表适配器
 */
class CapitalFlowAdapter : ListAdapter<CapitalFlow, CapitalFlowAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCapitalFlowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCapitalFlowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CapitalFlow) {
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

            // 主力净流入
            val inflowText = if (item.netInflow >= 0) {
                "+${String.format("%.2f", item.netInflow)}亿"
            } else {
                "${String.format("%.2f", item.netInflow)}亿"
            }
            binding.tvNetInflow.text = inflowText
            binding.tvNetInflow.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (item.netInflow >= 0) R.color.stock_up else R.color.stock_down
                )
            )

            // 近5日累计涨跌幅（仅A股显示）
            if (item.recentChangePercent != null && item.recentIsUp != null) {
                binding.layoutRecentChange.visibility = android.view.View.VISIBLE
                val recentText = if (item.recentIsUp) {
                    "+${String.format("%.2f", item.recentChangePercent)}%"
                } else {
                    "${String.format("%.2f", item.recentChangePercent)}%"
                }
                binding.tvRecentChange.text = recentText
                binding.tvRecentChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        if (item.recentIsUp) R.color.stock_up else R.color.stock_down
                    )
                )
            } else {
                binding.layoutRecentChange.visibility = android.view.View.GONE
            }

            // 当日涨跌幅
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
            binding.tvChangePercent.setBackgroundResource(
                if (item.isUp) R.drawable.bg_change_up else R.drawable.bg_change_down
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CapitalFlow>() {
        override fun areItemsTheSame(oldItem: CapitalFlow, newItem: CapitalFlow): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: CapitalFlow, newItem: CapitalFlow): Boolean {
            return oldItem == newItem
        }
    }
}
