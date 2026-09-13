# 流浪狗逼炒股专用

一款实时股票行情 Android 应用，提供全球产业板块数据、A股/美股主力资金流入排名、热股综合排名等功能。

## 开发者

- **GitHub**: [lcccc9897](https://github.com/lcccc9897)
- **联系方式**: lcccc9897

## 功能特性

### 🌐 全球产业数据
- 24+ 热门产业板块实时涨跌幅
- AI算力、半导体、新能源、军工等热门概念
- 自动识别交易/休市状态
- 30秒自动刷新

### 💰 资金流入板块
- **A股主力资金流入排名**（前20）
  - 主力净流入金额（亿元）
  - 当日涨跌幅
  - **近5日累计涨跌幅**
- **美股主力资金流入排名**（前15）
  - 基于成交量估算资金流入
  - 当日涨跌幅
- A股/美股一键切换
- 30秒自动刷新

### 🔥 热股排名
- A股+美股综合热度排名
- 市场标签区分（A股/美股）
- 热度进度条可视化
- 60秒自动刷新

### ⚙️ 设置
- 应用信息展示
- 开发者联系方式
- 免责声明

## 数据来源

| 市场 | 数据源 | 说明 |
|------|--------|------|
| A股 | 东方财富公开接口 | 实时行情、资金流向 |
| 美股 | Yahoo Finance API | 实时行情（可能有15分钟延迟） |
| 板块 | 东方财富板块行情 | 行业板块涨跌幅 |

> **注意**：免费公开 API 可能存在延迟，数据仅供参考，不构成投资建议。

## 技术栈

- **语言**: Kotlin
- **最低 SDK**: Android 7.0 (API 24)
- **目标 SDK**: Android 14 (API 34)
- **构建工具**: Gradle 8.2 + AGP 8.2.0
- **UI**: Material Design Components + ViewBinding
- **网络**: OkHttp 4.12.0 + Gson
- **异步**: Thread + Handler（轻量方案）

## 项目结构

```
StockMarketApp/
├── app/
│   └── src/main/
│       ├── java/com/stock/market/
│       │   ├── MainActivity.kt              # 主Activity（底部导航）
│       │   ├── model/                         # 数据模型
│       │   │   ├── Sector.kt
│       │   │   ├── CapitalFlow.kt
│       │   │   └── HotStock.kt
│       │   ├── network/                       # 网络层
│       │   │   ├── ApiClient.kt              # OkHttp 封装
│       │   │   └── StockRepository.kt        # 数据仓库（真实API+降级）
│       │   ├── ui/
│       │   │   ├── home/                      # 首页-全球产业数据
│       │   │   ├── capital/                   # 资金流入板块
│       │   │   ├── hotstocks/                 # 热股排名
│       │   │   └── settings/                  # 设置页
│       │   └── utils/
│       │       └── MockData.kt                # 模拟数据（API失败降级用）
│       └── res/                                # 资源文件
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## GitHub 编译指南

### 方式一：Android Studio 编译（推荐）

1. **克隆项目**
   ```bash
   git clone https://github.com/lcccc9897/StockMarketApp.git
   cd StockMarketApp
   ```

2. **使用 Android Studio 打开**
   - 打开 Android Studio
   - File → Open → 选择项目根目录
   - 等待 Gradle Sync 完成（首次会自动下载依赖）

3. **编译运行**
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮（▶️）或按 `Shift+F10`

### 方式二：命令行编译

#### Windows
```bash
# 编译 Debug APK
gradlew.bat assembleDebug

# 编译 Release APK
gradlew.bat assembleRelease

# 安装到设备
gradlew.bat installDebug
```

#### macOS / Linux
```bash
# 赋予执行权限
chmod +x gradlew

# 编译 Debug APK
./gradlew assembleDebug

# 编译 Release APK
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

### 编译产物位置

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

### GitHub Actions 自动构建（可选）

在项目根目录创建 `.github/workflows/build.yml`：

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    - name: Build with Gradle
      run: ./gradlew assembleDebug
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 环境要求

- **JDK**: 17 或更高
- **Android Studio**: Hedgehog (2023.1.1) 或更高
- **Android SDK**: Platform 34, Build-Tools 34.0.0
- **Gradle**: 8.2（项目自带 wrapper，无需手动安装）

## 常见问题

### Q: Gradle Sync 失败？
A: 检查网络连接，确保能访问 Google Maven 仓库。国内用户可配置镜像源。

### Q: 数据不显示？
A: 公开 API 可能有限流或不稳定，应用会自动降级为模拟数据。检查网络连接。

### Q: 美股数据有延迟？
A: Yahoo Finance 免费接口通常有 15 分钟延迟，属正常现象。

### Q: 如何替换为自己的 API？
A: 修改 `StockRepository.kt` 中的 API 地址和解析逻辑即可。

## 免责声明

本程序展示的公开查询数据、仅供参考，不构成任何投资建议。

股市有风险，投资需谨慎。开发者不对因使用本应用而产生的任何投资损失承担责任。

## License

MIT License
