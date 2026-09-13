package com.stock.market.model

/**
 * 产业板块数据模型
 */
data class Sector(
    val name: String,
    val icon: String,
    val changePercent: Double,
    val isUp: Boolean
)
