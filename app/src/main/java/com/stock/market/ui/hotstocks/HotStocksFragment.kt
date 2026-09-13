package com.stock.market.ui.hotstocks

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stock.market.databinding.FragmentHotStocksBinding
import com.stock.market.network.StockRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 热股排名页面（实时数据）
 */
class HotStocksFragment : Fragment() {

    private var _binding: FragmentHotStocksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HotStockAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 60000L // 60秒自动刷新

    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadData()
            handler.postDelayed(this, refreshInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()

        binding.swipeRefresh.setOnRefreshListener {
            loadData()
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
        adapter = HotStockAdapter()
        binding.recyclerViewHotStocks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HotStocksFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        binding.swipeRefresh.isRefreshing = true
        Thread {
            try {
                val hotStocks = StockRepository.getHotStocks()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val updateTime = timeFormat.format(Date())
                activity?.runOnUiThread {
                    adapter.submitList(hotStocks)
                    binding.tvUpdateTime.text = "更新时间：$updateTime"
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
