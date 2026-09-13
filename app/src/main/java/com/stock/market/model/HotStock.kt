package com.stock.market.model

/**
 * 热股排名数据模型
 */
data class HotStock(
    var rank: Int,
    val name: String,
    val code: String,
    val price: Double,
    val changePercent: Double,
    val isUp: Boolean,
    val market: String,  // A股 / 美股
    val heat: Int        // 热度值
)
