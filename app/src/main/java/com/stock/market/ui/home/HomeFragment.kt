package com.stock.market.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.stock.market.databinding.FragmentHomeBinding
import com.stock.market.network.StockRepository
import java.util.Calendar

/**
 * 首页 - 全球产业数据（实时数据）
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sectorAdapter: SectorAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 30000L // 30秒自动刷新

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        sectorAdapter = SectorAdapter()
        binding.recyclerViewSectors.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = sectorAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        binding.swipeRefresh.isRefreshing = true
        Thread {
            try {
                val sectors = StockRepository.getGlobalSectors()
                activity?.runOnUiThread {
                    sectorAdapter.submitList(sectors)
                    updateMarketStatus()
                    binding.swipeRefresh.isRefreshing = false
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }.start()
    }

    private fun updateMarketStatus() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val time = hour * 60 + minute

        // A股交易时间：周一至周五 9:30-11:30, 13:00-15:00
        val isWeekday = dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY
        val isMorningSession = time in 570..690 // 9:30-11:30
        val isAfternoonSession = time in 780..900 // 13:00-15:00

        binding.tvMarketStatus.text = if (isWeekday && (isMorningSession || isAfternoonSession)) {
            "交易中"
        } else {
            "休市"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
        _binding = null
    }
}
