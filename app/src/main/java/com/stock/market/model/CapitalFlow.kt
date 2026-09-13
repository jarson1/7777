package com.stock.market.model

/**
 * 资金流入数据模型
 */
data class CapitalFlow(
    var rank: Int,
    val name: String,
    val code: String,
    val netInflow: Double,       // 主力净流入（亿元）
    val changePercent: Double,   // 当日涨跌幅
    val isUp: Boolean,
    val recentChangePercent: Double? = null,  // 近5日累计涨跌幅（仅A股）
    val recentIsUp: Boolean? = null           // 近5日涨跌方向（仅A股）
)
