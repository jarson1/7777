package com.stock.market.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stock.market.databinding.FragmentSettingsBinding

/**
 * 设置页面
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 开发者信息
        binding.tvDeveloperContact.text = "lcccc9897"

        // 版本信息
        binding.tvVersion.text = "v1.0.0"

        // 点击联系开发者
        binding.layoutDeveloper.setOnClickListener {
            // 可根据需要跳转到 GitHub 或其他联系方式
            // 示例：打开 GitHub 主页
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lcccc9897"))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 免责声明
        binding.tvDisclaimer.text =
            "本程序展示的公开查询数据、仅供参考，不构成任何投资建议。\n" +
            "股市有风险，投资需谨慎。"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
