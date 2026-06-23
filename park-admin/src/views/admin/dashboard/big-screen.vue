<template>
  <div class="big-screen">
    <div class="screen-bg"></div>
    <div class="screen-grid"></div>
    <div class="screen-vignette"></div>

    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="header-deco header-deco-left"></div>
      <div class="header-deco header-deco-right"></div>
      <div class="header-content">
        <div class="title-wrapper">
          <div class="title-line-top"></div>
          <div class="title-text">杭州市小微园区数据大屏</div>
          <div class="title-line-bottom"></div>
        </div>
      </div>
      <div class="year-selector">
        <span class="year-text">2026</span>
        <i class="year-icon"></i>
      </div>
    </div>

    <!-- 主体三栏 -->
    <div class="main-content">
      <!-- 左侧数据 -->
      <div class="side-panel left-panel">
        <div
          v-for="(item, index) in leftStats"
          :key="'l' + index"
          class="stat-card"
        >
          <div class="card-header">
            <span class="card-label">{{ item.label }}</span>
            <span class="card-change" :class="item.change >= 0 ? 'up' : 'down'">
              较去年
              <span class="change-num">{{ item.change >= 0 ? '+' : '' }}{{ item.change }}%</span>
            </span>
          </div>
          <div class="card-value">
            <span class="value-num">{{ item.value }}</span>
            <span class="value-unit">{{ item.unit }}</span>
          </div>
        </div>
      </div>

      <!-- 中央地图 -->
      <div class="center-panel">
        <div class="map-container">
          <div class="map-ellipse map-ellipse-1"></div>
          <div class="map-ellipse map-ellipse-2"></div>
          <div class="map-ellipse map-ellipse-3"></div>
          <div class="map-ellipse map-ellipse-4"></div>

          <div ref="mapChart" class="map-chart"></div>

          <!-- 自定义金色光柱标注 -->
          <div class="map-markers">
            <div
              v-for="d in districtData"
              :key="d.name"
              class="map-marker"
              :style="{ left: d._px + 'px', top: d._py + 'px' }"
              @click="showDistrictDetail(d)"
            >
              <div class="marker-dot"></div>
              <div class="marker-beam"></div>
              <div class="marker-label">{{ d.name }}</div>
            </div>
          </div>

          <!-- 弹窗 -->
          <div
            v-if="selectedDistrict"
            class="district-popup"
            :style="popupPosition"
          >
            <div class="popup-header">
              <span class="popup-title">{{ selectedDistrict.name }}</span>
              <span class="popup-close" @click="closeDistrictDetail">×</span>
            </div>
            <div class="popup-body">
              <div class="popup-row"><span class="popup-dot"></span>园区总数：{{ selectedDistrict.parkCount }}处</div>
              <div class="popup-row"><span class="popup-dot"></span>主要产业：{{ selectedDistrict.mainIndustry }}</div>
              <div class="popup-row">
                <span class="popup-dot"></span>生产制造类：{{ selectedDistrict.manufacturingCount }}家
                <span class="popup-dot" style="margin-left:20px"></span>生产服务类：{{ selectedDistrict.serviceCount }}家
              </div>
              <div class="popup-row">
                <span class="popup-dot"></span>亩均产值：{{ selectedDistrict.revenuePerMu }}元/亩
                <span class="popup-dot" style="margin-left:20px"></span>亩均税收：{{ selectedDistrict.taxPerMu }}元/亩
              </div>
              <div class="popup-row"><span class="popup-dot"></span>参评企业：{{ selectedDistrict.participateEnterpriseCount }}家</div>
              <div class="popup-row"><span class="popup-dot"></span>国家专精特新小巨人：{{ selectedDistrict.nationalSpecializedCount }}家</div>
              <div class="popup-row"><span class="popup-dot"></span>省专精特新中小企业：{{ selectedDistrict.provincialSpecializedCount }}家</div>
              <div class="popup-row"><span class="popup-dot"></span>创新型中小企业：{{ selectedDistrict.innovativeSmeCount }}家</div>
            </div>
            <div class="popup-footer"></div>
          </div>

          <div class="compass">
            <span class="compass-arrow"></span>
            <span class="compass-letter">N</span>
          </div>
        </div>
      </div>

      <!-- 右侧数据 -->
      <div class="side-panel right-panel">
        <div
          v-for="(item, index) in rightStats"
          :key="'r' + index"
          class="stat-card"
        >
          <div class="card-header">
            <span class="card-label">{{ item.label }}</span>
            <span class="card-change" :class="item.change >= 0 ? 'up' : 'down'">
              较去年
              <span class="change-num">{{ item.change >= 0 ? '+' : '' }}{{ item.change }}%</span>
            </span>
          </div>
          <div class="card-value">
            <span class="value-num">{{ item.value }}</span>
            <span class="value-unit">{{ item.unit }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部图表 -->
    <div class="bottom-content">
      <div class="chart-panel mu-jun-panel">
        <div class="chart-title">
          <span class="title-flame"></span>
          <span class="title-name">亩均分析</span>
        </div>
        <div class="chart-tabs">
          <span :class="['tab', { active: muJunTab === 0 }]" @click="muJunTab = 0">税收</span>
          <span :class="['tab', { active: muJunTab === 1 }]" @click="muJunTab = 1">产出</span>
        </div>
        <div ref="muJunChart" class="chart-body"></div>
      </div>

      <div class="chart-panel performance-panel">
        <div class="chart-title">
          <span class="title-flame"></span>
          <span class="title-name">各区县园区绩效分档统计</span>
        </div>
        <div class="chart-legend">
          <span class="legend-item"><span class="legend-box" style="background:#5a6478"></span>服务类</span>
          <span class="legend-item"><span class="legend-box" style="background:#8a96a8"></span>制造类</span>
          <span class="legend-item"><span class="legend-box" style="background:#22d3ee"></span>A档</span>
          <span class="legend-item"><span class="legend-box" style="background:#10b981"></span>B档</span>
          <span class="legend-item"><span class="legend-box" style="background:#fbbf24"></span>C档</span>
          <span class="legend-item"><span class="legend-box" style="background:#a855f7"></span>D档</span>
          <span class="legend-item"><span class="legend-line" style="background:#ef4444"></span>亩均税收</span>
          <span class="legend-item"><span class="legend-line" style="background:#3b82f6"></span>亩均产出</span>
        </div>
        <div ref="performanceChart" class="chart-body"></div>
      </div>

      <div class="chart-panel ranking-panel">
        <div class="chart-title">
          <span class="title-trophy"></span>
          <span class="title-name">光荣榜单</span>
        </div>
        <div ref="rankingChart" class="chart-body"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import hangzhouMap from '@/../public/hangzhou-map.json'
import {
  getBigScreenStats,
  getDistrictData,
  getMuJunAnalysis,
  getPerformanceStats,
  getStarRanking
} from '@/api/dashboard-big'

export default {
  name: 'BigScreen',
  data() {
    return {
      year: 2026,
      stats: {},
      districtData: [],
      selectedDistrict: null,
      muJunTab: 0,
      mapChart: null,
      muJunChart: null,
      performanceChart: null,
      rankingChart: null,
      mapPixelPositions: {}
    }
  },
  computed: {
    leftStats() {
      return [
        { label: '园区总数', value: this.stats.parkTotal || 0, unit: '处', change: this.stats.parkTotalChange || 0 },
        { label: '员工总数', value: this.stats.employeeTotal || 0, unit: '人', change: this.stats.employeeTotalChange || 0 },
        { label: '生产制造类园区数', value: this.stats.manufacturingCount || 0, unit: '家', change: this.stats.manufacturingChange || 0 },
        { label: '生产服务类园区数', value: this.stats.serviceCount || 0, unit: '家', change: this.stats.serviceChange || 0 },
        { label: '已建建筑面积', value: this.formatNumber(this.stats.buildArea) || 0, unit: '平米', change: this.stats.buildAreaChange || 0 },
        { label: '实际用地数', value: this.formatNumber(this.stats.landArea) || 0, unit: '亩', change: this.stats.landAreaChange || 0 },
        { label: '入驻企业总数', value: this.stats.enterpriseTotal || 0, unit: '家', change: this.stats.enterpriseTotalChange || 0 }
      ]
    },
    rightStats() {
      return [
        { label: '全市亩均产值', value: this.stats.revenuePerMu || 0, unit: '万元', change: this.stats.revenuePerMuChange || 0 },
        { label: '全市亩均税收', value: this.formatNumber(this.stats.taxPerMu) || 0, unit: '元', change: this.stats.taxPerMuChange || 0 },
        { label: '国家专精特新小巨人', value: this.stats.nationalSpecializedCount || 0, unit: '家', change: this.stats.nationalSpecializedChange || 0 },
        { label: '省专精特新中小企业', value: this.stats.provincialSpecializedCount || 0, unit: '家', change: this.stats.provincialSpecializedChange || 0 },
        { label: '创新型中小企业', value: this.stats.innovativeSmeCount || 0, unit: '家', change: this.stats.innovativeSmeChange || 0 },
        { label: '参评企业总数', value: this.stats.participateEnterpriseCount || 0, unit: '家', change: this.stats.participateEnterpriseChange || 0 }
      ]
    },
    popupPosition() {
      if (!this.selectedDistrict) return {}
      const d = this.selectedDistrict
      const offsetX = 200
      const offsetY = -50
      return {
        left: (d._px + offsetX) + 'px',
        top: (d._py + offsetY) + 'px'
      }
    }
  },
  async mounted() {
    window.addEventListener('resize', this.handleResize)
    await this.loadData()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.mapChart && this.mapChart.dispose()
    this.muJunChart && this.muJunChart.dispose()
    this.performanceChart && this.performanceChart.dispose()
    this.rankingChart && this.rankingChart.dispose()
  },
  methods: {
    formatNumber(num) {
      if (!num) return 0
      return Number(num).toLocaleString('en-US', { maximumFractionDigits: 2 })
    },
    async loadData() {
      const [statsRes, districtRes, muJunRes, performanceRes, rankingRes] = await Promise.all([
        getBigScreenStats(this.year),
        getDistrictData(this.year),
        getMuJunAnalysis(this.year),
        getPerformanceStats(this.year),
        getStarRanking(this.year)
      ])
      if (statsRes.code === 200) this.stats = statsRes.data
      if (districtRes.code === 200) this.districtData = districtRes.data
      await this.$nextTick()
      this.initMapChart()
      if (muJunRes.code === 200) this.initMuJunChart(muJunRes.data)
      if (performanceRes.code === 200) this.initPerformanceChart(performanceRes.data)
      if (rankingRes.code === 200) this.initRankingChart(rankingRes.data)
    },
    initMapChart() {
      const chartDom = this.$refs.mapChart
      this.mapChart = echarts.init(chartDom)
      echarts.registerMap('hangzhou', hangzhouMap)

      // 使用蓝绿色渐变 + 立体山脉纹理（多层叠加）
      const option = {
        backgroundColor: 'transparent',
        tooltip: { show: false },
        geo: {
          map: 'hangzhou',
          roam: false,
          zoom: 1.1,
          aspectScale: 0.85,
          layoutCenter: ['50%', '50%'],
          layoutSize: '95%',
          label: { show: false },
          itemStyle: {
            areaColor: {
              type: 'radial',
              x: 0.5, y: 0.5, r: 0.7,
              colorStops: [
                { offset: 0, color: '#1e6db8' },
                { offset: 0.5, color: '#0e4a8f' },
                { offset: 1, color: '#082d5e' }
              ]
            },
            borderColor: '#7dd3fc',
            borderWidth: 1.5,
            shadowColor: 'rgba(0, 212, 255, 0.8)',
            shadowBlur: 20
          },
          emphasis: { disabled: true }
        }
      }
      this.mapChart.setOption(option)
      // 计算每个区县在地图上的实际像素坐标
      this.$nextTick(() => {
        this.calculateMarkerPositions()
      })
    },
    calculateMarkerPositions() {
      if (!this.mapChart || !this.districtData.length) return
      const chartDom = this.$refs.mapChart
      const rect = chartDom.getBoundingClientRect()
      const containerRect = chartDom.parentElement.getBoundingClientRect()
      const offsetX = rect.left - containerRect.left
      const offsetY = rect.top - containerRect.top

      this.districtData.forEach(d => {
        if (d.position && Array.isArray(d.position)) {
          const px = this.mapChart.convertToPixel({ geoIndex: 0 }, d.position)
          if (px) {
            d._px = px[0] + offsetX
            d._py = px[1] + offsetY
          }
        }
      })
    },
    initMuJunChart(data) {
      const chartDom = this.$refs.muJunChart
      this.muJunChart = echarts.init(chartDom)
      const seriesData = this.muJunTab === 0
        ? [
            { name: '生产性服务类', color: '#22d3ee', data: data.map(i => i.service), areaColor: ['rgba(34, 211, 238, 0.6)', 'rgba(34, 211, 238, 0.05)'] },
            { name: '平均值', color: '#fbbf24', data: data.map(i => i.average), areaColor: ['rgba(251, 191, 36, 0.5)', 'rgba(251, 191, 36, 0.05)'] },
            { name: '生产制造类', color: '#ef4444', data: data.map(i => i.manufacturing), areaColor: ['rgba(239, 68, 68, 0.5)', 'rgba(239, 68, 68, 0.05)'] }
          ]
        : [
            { name: '生产性服务类', color: '#22d3ee', data: data.map(i => i.serviceOut), areaColor: ['rgba(34, 211, 238, 0.6)', 'rgba(34, 211, 238, 0.05)'] },
            { name: '平均值', color: '#fbbf24', data: data.map(i => i.averageOut), areaColor: ['rgba(251, 191, 36, 0.5)', 'rgba(251, 191, 36, 0.05)'] },
            { name: '生产制造类', color: '#ef4444', data: data.map(i => i.manufacturingOut), areaColor: ['rgba(239, 68, 68, 0.5)', 'rgba(239, 68, 68, 0.05)'] }
          ]
      const option = {
        backgroundColor: 'transparent',
        grid: { top: 30, right: 20, bottom: 28, left: 45 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 19, 37, 0.95)',
          borderColor: '#00d4ff',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 }
        },
        legend: {
          data: seriesData.map(s => s.name),
          textStyle: { color: '#8ecae6', fontSize: 11 },
          top: 0,
          right: 10,
          itemWidth: 14,
          itemHeight: 8,
          itemGap: 14
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.map(i => i.year),
          axisLine: { lineStyle: { color: '#0e7490' } },
          axisLabel: { color: '#7dd3fc', fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: this.muJunTab === 0 ? '万元/亩' : '元/亩',
          nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
          axisLine: { show: false },
          axisLabel: { color: '#7dd3fc', fontSize: 11 },
          splitLine: { lineStyle: { color: 'rgba(125, 211, 252, 0.1)' } }
        },
        series: seriesData.map(s => ({
          name: s.name,
          type: 'line',
          data: s.data,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { color: s.color, width: 2.5, shadowBlur: 12, shadowColor: s.color },
          itemStyle: { color: s.color, borderColor: '#fff', borderWidth: 1 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: s.areaColor[0] },
              { offset: 1, color: s.areaColor[1] }
            ])
          }
        }))
      }
      this.muJunChart.setOption(option)
    },
    initPerformanceChart(data) {
      const chartDom = this.$refs.performanceChart
      this.performanceChart = echarts.init(chartDom)
      const option = {
        backgroundColor: 'transparent',
        grid: { top: 30, right: 50, bottom: 45, left: 45 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 19, 37, 0.95)',
          borderColor: '#00d4ff',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 },
          axisPointer: { type: 'shadow' }
        },
        xAxis: {
          type: 'category',
          data: data.map(i => i.name),
          axisLine: { lineStyle: { color: '#0e7490' } },
          axisLabel: { color: '#7dd3fc', fontSize: 10, interval: 0 },
          axisTick: { show: false }
        },
        yAxis: [
          {
            type: 'value',
            name: '园区数',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 10 },
            splitLine: { lineStyle: { color: 'rgba(125, 211, 252, 0.08)' } }
          },
          {
            type: 'value',
            name: '元/亩',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 10 },
            splitLine: { show: false }
          }
        ],
        series: [
          { name: '服务类', type: 'bar', data: data.map(i => i.service), itemStyle: { color: '#5a6478' }, barWidth: 5 },
          { name: '制造类', type: 'bar', data: data.map(i => i.manufacturing), itemStyle: { color: '#8a96a8' }, barWidth: 5 },
          { name: 'A档', type: 'bar', data: data.map(i => i.gradeA), itemStyle: { color: '#22d3ee' }, barWidth: 5 },
          { name: 'B档', type: 'bar', data: data.map(i => i.gradeB), itemStyle: { color: '#10b981' }, barWidth: 5 },
          { name: 'C档', type: 'bar', data: data.map(i => i.gradeC), itemStyle: { color: '#fbbf24' }, barWidth: 5 },
          { name: 'D档', type: 'bar', data: data.map(i => i.gradeD), itemStyle: { color: '#a855f7' }, barWidth: 5 },
          {
            name: '亩均税收',
            type: 'line',
            yAxisIndex: 1,
            data: data.map(i => i.taxPerMu),
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: { color: '#ef4444', width: 2 },
            itemStyle: { color: '#ef4444', borderColor: '#fff', borderWidth: 1 }
          },
          {
            name: '亩均产出',
            type: 'line',
            yAxisIndex: 1,
            data: data.map(i => i.revenuePerMu),
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: { color: '#3b82f6', width: 2 },
            itemStyle: { color: '#3b82f6', borderColor: '#fff', borderWidth: 1 }
          }
        ]
      }
      this.performanceChart.setOption(option)
    },
    initRankingChart(data) {
      const chartDom = this.$refs.rankingChart
      this.rankingChart = echarts.init(chartDom)
      const colors = ['#fbbf24', '#9ca3af', '#f59e0b', '#d97706']
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(5, 19, 37, 0.95)',
          borderColor: '#00d4ff',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 },
          formatter: '{b}<br/>{c}家 ({d}%)'
        },
        series: [
          {
            type: 'pie',
            radius: ['38%', '62%'],
            center: ['50%', '55%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 6,
              borderColor: '#0a2540',
              borderWidth: 3
            },
            label: {
              show: true,
              color: '#7dd3fc',
              fontSize: 11,
              formatter: '{b}\n{c}家'
            },
            labelLine: {
              show: true,
              length: 12,
              length2: 18,
              lineStyle: { color: '#7dd3fc' }
            },
            emphasis: {
              label: { fontSize: 13, fontWeight: 'bold', color: '#fff' },
              itemStyle: { shadowBlur: 20, shadowColor: 'rgba(251, 191, 36, 0.5)' }
            },
            data: data.map((d, i) => ({
              value: d.value,
              name: d.name,
              itemStyle: { color: colors[i] }
            }))
          }
        ]
      }
      this.rankingChart.setOption(option)
    },
    showDistrictDetail(d) {
      this.selectedDistrict = d
    },
    closeDistrictDetail() {
      this.selectedDistrict = null
    },
    handleResize() {
      this.mapChart && this.mapChart.resize()
      this.muJunChart && this.muJunChart.resize()
      this.performanceChart && this.performanceChart.resize()
      this.rankingChart && this.rankingChart.resize()
      this.calculateMarkerPositions()
    }
  },
  watch: {
    muJunTab() {
      getMuJunAnalysis(this.year).then(r => {
        if (r.code === 200) this.initMuJunChart(r.data)
      })
    }
  }
}
</script>

<style scoped>
/* ============== 全局基础 ============== */
.big-screen {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  background: #030a16;
  color: #fff;
}

/* ============== 背景层 ============== */
.screen-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 50% 30%, rgba(20, 70, 140, 0.25) 0%, transparent 50%),
    radial-gradient(ellipse at 15% 80%, rgba(0, 212, 255, 0.06) 0%, transparent 40%),
    radial-gradient(ellipse at 85% 80%, rgba(0, 212, 255, 0.05) 0%, transparent 40%),
    linear-gradient(180deg, #030a16 0%, #051325 50%, #030a16 100%);
  z-index: 1;
}

.screen-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 212, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.04) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 2;
}

.screen-vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, transparent 50%, rgba(0, 0, 0, 0.5) 100%);
  z-index: 3;
  pointer-events: none;
}

/* ============== 顶部标题栏 ============== */
.header {
  position: relative;
  z-index: 10;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 30px;
  background: linear-gradient(180deg, rgba(5, 25, 50, 0.4) 0%, transparent 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.25);
}

.header-deco {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 280px;
  height: 60px;
  pointer-events: none;
}

.header-deco-left {
  left: 0;
  background:
    linear-gradient(90deg, transparent 0%, rgba(34, 211, 238, 0.15) 100%),
    linear-gradient(135deg, transparent 65%, rgba(34, 211, 238, 0.1) 100%);
  clip-path: polygon(0 30%, 100% 0, 100% 100%, 0 70%);
}

.header-deco-right {
  right: 0;
  background:
    linear-gradient(-90deg, transparent 0%, rgba(34, 211, 238, 0.15) 100%),
    linear-gradient(-135deg, transparent 65%, rgba(34, 211, 238, 0.1) 100%);
  clip-path: polygon(100% 30%, 0 0, 0 100%, 100% 70%);
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.title-line-top,
.title-line-bottom {
  width: 220px;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #22d3ee 50%, transparent 100%);
  margin: 4px 0;
  position: relative;
}

.title-line-top::before,
.title-line-bottom::before,
.title-line-top::after,
.title-line-bottom::after {
  content: '';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background: #22d3ee;
  border-radius: 50%;
  box-shadow: 0 0 8px #22d3ee, 0 0 15px rgba(34, 211, 238, 0.5);
}

.title-line-top::before { left: 30%; }
.title-line-top::after { right: 30%; }
.title-line-bottom::before { left: 35%; }
.title-line-bottom::after { right: 35%; }

.title-text {
  font-size: 30px;
  font-weight: bold;
  color: #22d3ee;
  text-shadow:
    0 0 10px rgba(34, 211, 238, 0.8),
    0 0 20px rgba(34, 211, 238, 0.5),
    0 0 30px rgba(34, 211, 238, 0.3);
  letter-spacing: 6px;
  padding: 4px 20px;
}

.year-selector {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 18px;
  background: linear-gradient(180deg, rgba(34, 211, 238, 0.15), rgba(5, 19, 37, 0.5));
  border: 1px solid #22d3ee;
  border-radius: 4px;
  cursor: pointer;
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.3), inset 0 0 10px rgba(34, 211, 238, 0.1);
  z-index: 5;
}

.year-text {
  font-size: 20px;
  font-weight: bold;
  color: #22d3ee;
  text-shadow: 0 0 8px rgba(34, 211, 238, 0.6);
}

.year-icon {
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 7px solid #22d3ee;
  filter: drop-shadow(0 0 3px #22d3ee);
}

/* ============== 主体三栏 ============== */
.main-content {
  position: relative;
  z-index: 10;
  display: flex;
  height: calc(100vh - 80px - 290px);
  padding: 10px 20px;
  gap: 15px;
}

/* ============== 侧边数据卡片 ============== */
.side-panel {
  width: 220px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 5px 0;
}

.stat-card {
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.5) 0%, rgba(5, 19, 37, 0.3) 100%);
  border: 1px solid rgba(34, 211, 238, 0.15);
  border-radius: 3px;
  padding: 10px 14px;
  position: relative;
  transition: all 0.3s;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 20%;
  right: 20%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(34, 211, 238, 0.6), transparent);
}

.stat-card:hover {
  border-color: rgba(34, 211, 238, 0.4);
  box-shadow: 0 0 15px rgba(34, 211, 238, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #6a8caf;
  margin-bottom: 6px;
}

.card-label {
  color: #6a8caf;
}

.card-change {
  font-size: 11px;
  color: #6a8caf;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-change.up { color: #6a8caf; }
.card-change.up .change-num { color: #10b981; font-weight: bold; }
.card-change.down { color: #6a8caf; }
.card-change.down .change-num { color: #ef4444; font-weight: bold; }

.card-value {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.value-num {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
  text-shadow: 0 0 15px rgba(34, 211, 238, 0.5);
  letter-spacing: 1px;
}

.value-unit {
  font-size: 12px;
  color: #6a8caf;
}

/* ============== 中央地图 ============== */
.center-panel {
  flex: 1;
  position: relative;
  min-width: 0;
}

.map-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.map-ellipse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(34, 211, 238, 0.1);
  border-radius: 50%;
  pointer-events: none;
}

.map-ellipse-1 { width: 65%; height: 95%; }
.map-ellipse-2 { width: 80%; height: 115%; }
.map-ellipse-3 { width: 95%; height: 135%; }
.map-ellipse-4 { width: 100%; height: 155%; border-style: dashed; border-color: rgba(34, 211, 238, 0.05); }

.map-chart {
  position: absolute;
  inset: 0;
  z-index: 5;
}

.map-markers {
  position: absolute;
  inset: 0;
  z-index: 10;
  pointer-events: none;
}

.map-marker {
  position: absolute;
  transform: translate(-50%, -100%);
  pointer-events: auto;
  cursor: pointer;
  width: 0;
  height: 0;
}

.marker-dot {
  position: absolute;
  top: -6px;
  left: -6px;
  width: 12px;
  height: 12px;
  background: #fbbf24;
  border-radius: 50%;
  box-shadow:
    0 0 8px #fbbf24,
    0 0 16px rgba(251, 191, 36, 0.6),
    0 0 24px rgba(251, 191, 36, 0.3);
  z-index: 2;
}

.marker-beam {
  position: absolute;
  top: 0;
  left: -2px;
  width: 4px;
  height: 60px;
  background: linear-gradient(180deg,
    rgba(251, 191, 36, 0.8) 0%,
    rgba(251, 191, 36, 0.6) 40%,
    rgba(251, 191, 36, 0.2) 80%,
    transparent 100%);
  box-shadow: 0 0 6px rgba(251, 191, 36, 0.6);
  z-index: 1;
}

.marker-label {
  position: absolute;
  top: -75px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(5, 19, 37, 0.95);
  border: 1px solid rgba(251, 191, 36, 0.7);
  color: #fbbf24;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 12px;
  white-space: nowrap;
  box-shadow: 0 0 10px rgba(251, 191, 36, 0.3);
  font-weight: 500;
}

.map-marker:hover .marker-dot {
  transform: scale(1.3);
  transition: transform 0.2s;
}

.map-marker:hover .marker-label {
  background: rgba(251, 191, 36, 0.95);
  color: #051325;
}

/* ============== 区县弹窗 ============== */
.district-popup {
  position: absolute;
  z-index: 20;
  width: 460px;
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.98) 0%, rgba(5, 19, 37, 0.99) 100%);
  border: 2px solid #f59e0b;
  border-radius: 4px;
  box-shadow:
    0 0 30px rgba(245, 158, 11, 0.4),
    inset 0 0 30px rgba(245, 158, 11, 0.05);
  animation: popupFadeIn 0.3s ease-out;
}

@keyframes popupFadeIn {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 18px;
  background: linear-gradient(90deg, rgba(245, 158, 11, 0.3) 0%, rgba(245, 158, 11, 0.1) 100%);
  border-bottom: 1px solid rgba(245, 158, 11, 0.5);
}

.popup-title {
  font-size: 16px;
  font-weight: bold;
  color: #f59e0b;
  text-shadow: 0 0 8px rgba(245, 158, 11, 0.5);
}

.popup-close {
  font-size: 22px;
  color: #7dd3fc;
  cursor: pointer;
  line-height: 1;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.popup-close:hover { color: #f59e0b; }

.popup-body {
  padding: 16px 20px;
}

.popup-row {
  font-size: 13px;
  color: #7dd3fc;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  line-height: 1.6;
}

.popup-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  background: #22d3ee;
  border-radius: 50%;
  margin-right: 8px;
  box-shadow: 0 0 6px #22d3ee;
  flex-shrink: 0;
}

.popup-footer {
  height: 8px;
  background: linear-gradient(90deg, rgba(245, 158, 11, 0.4), rgba(245, 158, 11, 0.05));
}

/* ============== 指南针 ============== */
.compass {
  position: absolute;
  bottom: 20px;
  right: 25px;
  width: 60px;
  height: 60px;
  z-index: 15;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.compass-arrow {
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 14px solid #22d3ee;
  filter: drop-shadow(0 0 5px #22d3ee);
  margin-bottom: 2px;
}

.compass-letter {
  font-size: 16px;
  font-weight: bold;
  color: #22d3ee;
  text-shadow: 0 0 8px #22d3ee;
}

/* ============== 底部图表 ============== */
.bottom-content {
  position: relative;
  z-index: 10;
  display: flex;
  height: 290px;
  padding: 0 20px 15px;
  gap: 15px;
}

.chart-panel {
  flex: 1;
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.4) 0%, rgba(5, 19, 37, 0.6) 100%);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 4px;
  padding: 10px 16px;
  position: relative;
  overflow: hidden;
}

.chart-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 10%, rgba(34, 211, 238, 0.6) 50%, transparent 90%);
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: bold;
  color: #22d3ee;
  margin-bottom: 6px;
  text-shadow: 0 0 6px rgba(34, 211, 238, 0.3);
}

.title-flame {
  display: inline-block;
  width: 14px;
  height: 16px;
  background: linear-gradient(180deg, #fde047 0%, #fbbf24 50%, #f59e0b 100%);
  clip-path: polygon(50% 0%, 80% 30%, 100% 60%, 80% 100%, 20% 100%, 0% 60%, 20% 30%);
  filter: drop-shadow(0 0 6px #fbbf24);
}

.title-trophy {
  display: inline-block;
  width: 14px;
  height: 16px;
  background: linear-gradient(180deg, #fde047 0%, #fbbf24 50%, #d97706 100%);
  clip-path: polygon(20% 0%, 80% 0%, 90% 30%, 70% 50%, 80% 80%, 70% 100%, 30% 100%, 20% 80%, 30% 50%, 10% 30%);
  filter: drop-shadow(0 0 6px #fbbf24);
}

.title-name {
  letter-spacing: 1px;
}

.chart-tabs {
  display: flex;
  gap: 14px;
  margin-bottom: 4px;
  margin-left: 22px;
}

.tab {
  font-size: 12px;
  color: #6a8caf;
  cursor: pointer;
  padding: 2px 10px;
  border-radius: 3px;
  transition: all 0.3s;
}

.tab:hover { color: #22d3ee; }

.tab.active {
  color: #22d3ee;
  background: rgba(34, 211, 238, 0.15);
  font-weight: bold;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 4px;
  margin-left: 22px;
  font-size: 11px;
  color: #6a8caf;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-box {
  width: 12px;
  height: 10px;
  border-radius: 1px;
}

.legend-line {
  width: 14px;
  height: 2px;
  border-radius: 1px;
}

.chart-body {
  height: calc(100% - 36px);
}

.mu-jun-panel .chart-body,
.performance-panel .chart-body {
  height: calc(100% - 56px);
}
</style>