package com.stock.market.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stock.market.model.CapitalFlow
import com.stock.market.model.HotStock
import com.stock.market.model.Sector
import com.stock.market.utils.MockData

/**
 * 股票数据仓库
 * 统一管理真实 API 数据获取，失败时降级为模拟数据
 */
object StockRepository {

    private val gson = Gson()

    // ==================== 全球产业板块数据 ====================

    /**
     * 获取全球产业板块数据
     * 优先使用东方财富板块行情，失败降级为模拟数据
     */
    fun getGlobalSectors(): List<Sector> {
        return try {
            val url = "https://push2.eastmoney.com/api/qt/clist/get?" +
                    "pn=1&pz=30&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:2" +
                    "&fields=f12,f14,f3,f104,f105,f128,f140,f141"
            val response = ApiClient.get(url) ?: return MockData.getGlobalSectors()

            val json = gson.fromJson(response, JsonObject::class.java)
            val data = json.getAsJsonObject("data") ?: return MockData.getGlobalSectors()
            val diff = data.getAsJsonArray("diff") ?: return MockData.getGlobalSectors()

            val sectors = mutableListOf<Sector>()
            val iconMap = mapOf(
                "半导体" to "🔬", "AI算力" to "🧠", "CPO" to "💡",
                "存储" to "💾", "数据中心" to "🗄️", "云计算" to "☁️",
                "商业航天" to "🚀", "卫星" to "🛰️", "机器人" to "🤖",
                "自动驾驶" to "🚗", "核电" to "⚛️", "电网" to "⚡",
                "军工" to "🛡️", "新能源" to "🔋", "光伏" to "☀️",
                "锂电池" to "🔌", "石油" to "🛢️", "天然气" to "🔥",
                "黄金" to "🏅", "银行金融" to "🏦", "生物医药" to "💊",
                "消费" to "🛒", "稀土" to "🧲", "铜/有色" to "🟠"
            )

            for (i in 0 until diff.size()) {
                val item = diff[i].asJsonObject
                val name = item.get("f14")?.asString ?: continue
                val change = item.get("f3")?.asDouble ?: 0.0
                val icon = iconMap[name] ?: "📊"
                sectors.add(Sector(name, icon, change, change >= 0))
            }

            if (sectors.isEmpty()) MockData.getGlobalSectors() else sectors
        } catch (e: Exception) {
            e.printStackTrace()
            MockData.getGlobalSectors()
        }
    }

    // ==================== A股资金流入数据 ====================

    /**
     * 获取A股主力资金流入排名（真实数据）
     */
    fun getAStockCapitalFlow(): List<CapitalFlow> {
        return try {
            val url = "https://push2.eastmoney.com/api/qt/clist/get?" +
                    "pn=1&pz=20&po=1&np=1&fltt=2&invt=2&fid=f62&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23" +
                    "&fields=f12,f14,f2,f3,f62,f184,f66,f69,f72,f75,f78,f81,f84,f87"
            val response = ApiClient.get(url) ?: return MockData.getAStockCapitalFlow()

            val json = gson.fromJson(response, JsonObject::class.java)
            val data = json.getAsJsonObject("data") ?: return MockData.getAStockCapitalFlow()
            val diff = data.getAsJsonArray("diff") ?: return MockData.getAStockCapitalFlow()

            val flows = mutableListOf<CapitalFlow>()
            for (i in 0 until diff.size()) {
                val item = diff[i].asJsonObject
                val code = item.get("f12")?.asString ?: continue
                val name = item.get("f14")?.asString ?: continue
                val netInflow = (item.get("f62")?.asDouble ?: 0.0) / 100000000 // 转换为亿元
                val changePercent = item.get("f3")?.asDouble ?: 0.0
                // f184 是5日涨跌幅
                val recentChange = item.get("f184")?.asDouble

                flows.add(
                    CapitalFlow(
                        rank = i + 1,
                        name = name,
                        code = code,
                        netInflow = Math.round(netInflow * 100.0) / 100.0,
                        changePercent = changePercent,
                        isUp = changePercent >= 0,
                        recentChangePercent = recentChange,
                        recentIsUp = recentChange?.let { it >= 0 }
                    )
                )
            }

            if (flows.isEmpty()) MockData.getAStockCapitalFlow() else flows
        } catch (e: Exception) {
            e.printStackTrace()
            MockData.getAStockCapitalFlow()
        }
    }

    // ==================== 美股资金流入数据 ====================

    /**
     * 获取美股主力资金流入排名
     * 使用 Yahoo Finance API 获取热门美股行情
     */
    fun getUSStockCapitalFlow(): List<CapitalFlow> {
        return try {
            // Yahoo Finance 热门股票列表
            val symbols = listOf(
                "NVDA", "AAPL", "MSFT", "TSLA", "AMZN", "GOOGL", "META",
                "AMD", "NFLX", "JPM", "V", "JNJ", "WMT", "PG", "DIS"
            )
            val symbolsStr = symbols.joinToString(",")
            val url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=$symbolsStr"
            val response = ApiClient.get(url) ?: return MockData.getUSStockCapitalFlow()

            val json = gson.fromJson(response, JsonObject::class.java)
            val quoteResponse = json.getAsJsonObject("quoteResponse")
                ?: return MockData.getUSStockCapitalFlow()
            val result = quoteResponse.getAsJsonArray("result")
                ?: return MockData.getUSStockCapitalFlow()

            val flows = mutableListOf<CapitalFlow>()
            for (i in 0 until result.size()) {
                val item = result[i].asJsonObject
                val symbol = item.get("symbol")?.asString ?: continue
                val name = item.get("shortName")?.asString ?: symbol
                val changePercent = item.get("regularMarketChangePercent")?.asDouble ?: 0.0
                // 美股没有直接的主力资金流，用成交量估算
                val volume = item.get("regularMarketVolume")?.asDouble ?: 0.0
                val price = item.get("regularMarketPrice")?.asDouble ?: 0.0
                val netInflow = (volume * price) / 1000000000 // 估算为十亿美元

                flows.add(
                    CapitalFlow(
                        rank = i + 1,
                        name = name,
                        code = symbol,
                        netInflow = Math.round(netInflow * 100.0) / 100.0,
                        changePercent = Math.round(changePercent * 100.0) / 100.0,
                        isUp = changePercent >= 0,
                        recentChangePercent = null,
                        recentIsUp = null
                    )
                )
            }

            // 按估算资金流入排序
            flows.sortByDescending { it.netInflow }
            flows.forEachIndexed { index, flow ->
                flow.rank = index + 1
            }

            if (flows.isEmpty()) MockData.getUSStockCapitalFlow() else flows.take(15)
        } catch (e: Exception) {
            e.printStackTrace()
            MockData.getUSStockCapitalFlow()
        }
    }

    // ==================== 热股排名数据 ====================

    /**
     * 获取热股排名（综合A股和美股）
     */
    fun getHotStocks(): List<HotStock> {
        return try {
            val aStocks = getAStockCapitalFlow().take(10)
            val usStocks = getUSStockCapitalFlow().take(10)

            val hotStocks = mutableListOf<HotStock>()

            aStocks.forEachIndexed { index, flow ->
                hotStocks.add(
                    HotStock(
                        rank = 0, // 后面统一排序
                        name = flow.name,
                        code = flow.code,
                        price = 0.0, // A股价格需要额外获取，这里简化
                        changePercent = flow.changePercent,
                        isUp = flow.isUp,
                        market = "A股",
                        heat = 9000 - index * 200
                    )
                )
            }

            usStocks.forEachIndexed { index, flow ->
                hotStocks.add(
                    HotStock(
                        rank = 0,
                        name = flow.name,
                        code = flow.code,
                        price = 0.0,
                        changePercent = flow.changePercent,
                        isUp = flow.isUp,
                        market = "美股",
                        heat = 8800 - index * 200
                    )
                )
            }

            // 按热度排序
            hotStocks.sortByDescending { it.heat }
            hotStocks.forEachIndexed { index, stock ->
                stock.rank = index + 1
            }

            if (hotStocks.isEmpty()) MockData.getHotStocks() else hotStocks
        } catch (e: Exception) {
            e.printStackTrace()
            MockData.getHotStocks()
        }
    }
}
