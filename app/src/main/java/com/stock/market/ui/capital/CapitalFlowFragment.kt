package com.stock.market.ui.capital

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.stock.market.databinding.FragmentCapitalFlowBinding
import com.stock.market.network.StockRepository

/**
 * 资金流入板块 - 支持A股/美股切换（实时数据）
 */
class CapitalFlowFragment : Fragment() {

    private var _binding: FragmentCapitalFlowBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CapitalFlowAdapter
    private var currentMarket = MARKET_A_STOCK
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 30000L // 30秒自动刷新

    companion object {
        private const val MARKET_A_STOCK = 0
        private const val MARKET_US_STOCK = 1
    }

    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadData(currentMarket)
            handler.postDelayed(this, refreshInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCapitalFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        loadData(MARKET_A_STOCK)

        binding.swipeRefresh.setOnRefreshListener {
            loadData(currentMarket)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(refreshRunnable, refreshInterval)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun setupRecyclerView() {
        adapter = CapitalFlowAdapter()
        binding.recyclerViewCapitalFlow.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CapitalFlowFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("A股"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("美股"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    MARKET_A_STOCK -> {
                        currentMarket = MARKET_A_STOCK
                        loadData(MARKET_A_STOCK)
                    }
                    MARKET_US_STOCK -> {
                        currentMarket = MARKET_US_STOCK
                        loadData(MARKET_US_STOCK)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadData(market: Int) {
        binding.swipeRefresh.isRefreshing = true
        Thread {
            try {
                val data = when (market) {
                    MARKET_US_STOCK -> StockRepository.getUSStockCapitalFlow()
                    else -> StockRepository.getAStockCapitalFlow()
                }
                activity?.runOnUiThread {
                    adapter.submitList(data)
                    binding.tvTitle.text = when (market) {
                        MARKET_US_STOCK -> "美股主力资金流入"
                        else -> "A股主力资金流入"
                    }
                    // 美股不显示近5日列
                    binding.tvHeaderRecent.visibility = if (market == MARKET_US_STOCK) {
                        android.view.View.GONE
                    } else {
                        android.view.View.VISIBLE
                    }
                    binding.swipeRefresh.isRefreshing = false
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
        _binding = null
    }
}
