<template>
  <div class="big-screen">
    <div class="screen-bg"></div>
    <div class="screen-grid"></div>
    <div class="screen-glow"></div>
    <div class="screen-particles"></div>
    <div class="screen-scan-line"></div>

    <div class="header">
      <div class="header-left">
        <div class="header-time">{{ currentTime }}</div>
      </div>
      <div class="header-center">
        <div class="header-decoration-left"></div>
        <div class="header-title-box">
          <div class="header-title">杭州市小微园区数据大屏</div>
        </div>
        <div class="header-decoration-right"></div>
      </div>
      <div class="header-right">
        <div class="year-selector">
          <span class="year-text">2026</span>
          <i class="el-icon-arrow-down year-icon"></i>
        </div>
      </div>
    </div>

    <div class="main-content">
      <template v-if="activeTab === 'overview'">
        <div class="side-panel left-panel">
          <div
            v-for="(item, index) in leftStats"
            :key="'l' + index"
            class="stat-card"
            @mouseenter="handleCardHover(index, 'left')"
            @mouseleave="handleCardLeave(index, 'left')"
            :class="{ active: hoveredCard === 'left-' + index }"
          >
            <div class="card-corner card-corner-top-left"></div>
            <div class="card-corner card-corner-top-right"></div>
            <div class="card-corner card-corner-bottom-left"></div>
            <div class="card-corner card-corner-bottom-right"></div>
            <div class="card-top">
              <span class="card-label">{{ item.label }}</span>
            </div>
            <div class="card-center">
              <span class="value-num">{{ item.value }}</span>
              <span class="value-unit">{{ item.unit }}</span>
            </div>
            <div class="card-bottom">
              <span class="card-change-label">较去年</span>
              <span class="card-change-value" :class="item.change >= 0 ? 'up' : 'down'">
                {{ item.change >= 0 ? '+' : '' }}{{ item.change }}%
              </span>
            </div>
            <div class="card-glow"></div>
            <div class="card-pulse"></div>
          </div>
        </div>

        <div class="center-panel">
          <div class="map-container">
            <div class="map-ring map-ring-1"></div>
            <div class="map-ring map-ring-2"></div>
            <div class="map-ring map-ring-3"></div>
            <div class="map-ring map-ring-4"></div>
            <div class="map-center-glow"></div>
            <div class="map-radar"></div>

            <div class="map-3d-layer">
              <div ref="mapChart" class="map-chart"></div>
            </div>

            <div class="vertical-beams">
              <template v-for="d in districtData">
                <div
                  :key="'beam-' + d.name"
                  class="v-beam"
                  :class="{ active: hoveredDistrict === d.name }"
                  :style="{
                    left: d._screenX + 'px',
                    top: d._screenY + 'px',
                    height: d._beamScreenLen + 'px',
                    transform: 'translate(-50%, -100%) translateX(' + (d._labelOffsetX || 0) + 'px)'
                  }"
                ></div>
                <div
                  :key="'label-' + d.name"
                  class="v-label"
                  :class="{ active: hoveredDistrict === d.name }"
                  :style="{
                    left: d._screenX + 'px',
                    top: (d._screenY - d._beamScreenLen) + 'px',
                    transform: 'translate(-50%, -100%) translateX(' + (d._labelOffsetX || 0) + 'px)'
                  }"
                  @mouseenter="handleMarkerHover(d)"
                  @mouseleave="handleMarkerLeave"
                  @click="handleMarkerClick(d)"
                >{{ d.name }}</div>
              </template>
            </div>

            <div
              v-if="hoveredDistrictData"
              class="district-tooltip"
              :style="tooltipPosition"
            >
              <div class="tooltip-arrow"></div>
              <div class="tooltip-header">
                <span class="tooltip-title">{{ hoveredDistrictData.name }}</span>
                <div class="tooltip-close" @click="handleMarkerLeave"></div>
              </div>
              <div class="tooltip-body">
                <div class="tooltip-row">
                  <span class="row-icon icon-park"></span>
                  <span class="row-label">园区总数</span>
                  <span class="row-value">{{ hoveredDistrictData.parkCount }}处</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-industry"></span>
                  <span class="row-label">主要产业</span>
                  <span class="row-value">{{ hoveredDistrictData.mainIndustry }}</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-factory"></span>
                  <span class="row-label">生产制造类</span>
                  <span class="row-value">{{ hoveredDistrictData.manufacturingCount }}家</span>
                  <span class="row-icon icon-service" style="margin-left:20px"></span>
                  <span class="row-label">生产服务类</span>
                  <span class="row-value">{{ hoveredDistrictData.serviceCount }}家</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-revenue"></span>
                  <span class="row-label">亩均产值</span>
                  <span class="row-value">{{ hoveredDistrictData.revenuePerMu }}万元/亩</span>
                  <span class="row-icon icon-tax" style="margin-left:20px"></span>
                  <span class="row-label">亩均税收</span>
                  <span class="row-value">{{ hoveredDistrictData.taxPerMu }}万元/亩</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-enterprise"></span>
                  <span class="row-label">参评企业</span>
                  <span class="row-value">{{ hoveredDistrictData.participateEnterpriseCount }}家</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-star"></span>
                  <span class="row-label">国家专精特新小巨人</span>
                  <span class="row-value">{{ hoveredDistrictData.nationalSpecializedCount }}家</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-award"></span>
                  <span class="row-label">省专精特新中小企业</span>
                  <span class="row-value">{{ hoveredDistrictData.provincialSpecializedCount }}家</span>
                </div>
                <div class="tooltip-row">
                  <span class="row-icon icon-innovation"></span>
                  <span class="row-label">创新型中小企业</span>
                  <span class="row-value">{{ hoveredDistrictData.innovativeSmeCount }}家</span>
                </div>
              </div>
              <div class="tooltip-footer"></div>
            </div>

            <div class="compass">
              <div class="compass-arrow"></div>
              <span class="compass-letter">N</span>
            </div>
          </div>
        </div>

        <div class="side-panel right-panel">
          <div
            v-for="(item, index) in rightStats"
            :key="'r' + index"
            class="stat-card"
            @mouseenter="handleCardHover(index, 'right')"
            @mouseleave="handleCardLeave(index, 'right')"
            :class="{ active: hoveredCard === 'right-' + index }"
          >
            <div class="card-corner card-corner-top-left"></div>
            <div class="card-corner card-corner-top-right"></div>
            <div class="card-corner card-corner-bottom-left"></div>
            <div class="card-corner card-corner-bottom-right"></div>
            <div class="card-top">
              <span class="card-label">{{ item.label }}</span>
            </div>
            <div class="card-center">
              <span class="value-num">{{ item.value }}</span>
              <span class="value-unit">{{ item.unit }}</span>
            </div>
            <div class="card-bottom">
              <span class="card-change-label">较去年</span>
              <span class="card-change-value" :class="item.change >= 0 ? 'up' : 'down'">
                {{ item.change >= 0 ? '+' : '' }}{{ item.change }}%
              </span>
            </div>
            <div class="card-glow"></div>
            <div class="card-pulse"></div>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="analysis-grid">
          <div class="chart-panel analysis-panel">
            <div class="panel-corner panel-corner-top-left"></div>
            <div class="panel-corner panel-corner-top-right"></div>
            <div class="panel-corner panel-corner-bottom-left"></div>
            <div class="panel-corner panel-corner-bottom-right"></div>
            <div class="panel-header">
              <div class="panel-icon panel-icon-analysis"></div>
              <span class="panel-title">园区数量类型分析</span>
            </div>
            <div class="panel-legend">
              <span class="legend-item"><span class="legend-dot" style="background:#22d3ee"></span>服务</span>
              <span class="legend-item"><span class="legend-dot" style="background:#10b981"></span>制造</span>
              <span class="legend-item"><span class="legend-dot" style="background:#fbbf24"></span>总数</span>
            </div>
            <div ref="analysisChart" class="panel-chart"></div>
          </div>

          <div class="chart-panel performance-panel">
            <div class="panel-corner panel-corner-top-left"></div>
            <div class="panel-corner panel-corner-top-right"></div>
            <div class="panel-corner panel-corner-bottom-left"></div>
            <div class="panel-corner panel-corner-bottom-right"></div>
            <div class="panel-header">
              <div class="panel-icon panel-icon-performance"></div>
              <span class="panel-title">绩效分析</span>
            </div>
            <div class="performance-content">
              <div class="performance-left">
                <div class="perf-sub-title">科技创新</div>
                <div ref="techInnovGauge" class="tech-gauge"></div>
                <div class="gauge-legend">
                  <div class="gauge-legend-item">
                    <span class="legend-bar" style="background: #22d3ee"></span>
                    <span>科技型企业</span>
                  </div>
                  <div class="gauge-legend-item">
                    <span class="legend-bar" style="background: #10b981"></span>
                    <span>高企</span>
                  </div>
                  <div class="gauge-legend-item">
                    <span class="legend-bar" style="background: #fbbf24"></span>
                    <span>浙江制造精品</span>
                  </div>
                  <div class="gauge-legend-item">
                    <span class="legend-bar" style="background: #a855f7"></span>
                    <span>省级优秀工业新品</span>
                  </div>
                </div>
              </div>
              <div class="performance-right">
                <div class="perf-sub-title">企业培育</div>
                <div class="perf-right-legend">
                  <span class="legend-item"><span class="legend-dot" style="background:#10b981"></span>新增</span>
                  <span class="legend-item"><span class="legend-dot" style="background:#3b82f6"></span>总数</span>
                </div>
                <div ref="enterpriseCultivateChart" class="enterprise-chart"></div>
              </div>
            </div>
          </div>

          <div class="chart-panel efficiency-panel">
            <div class="panel-corner panel-corner-top-left"></div>
            <div class="panel-corner panel-corner-top-right"></div>
            <div class="panel-corner panel-corner-bottom-left"></div>
            <div class="panel-corner panel-corner-bottom-right"></div>
            <div class="panel-header">
              <div class="panel-icon panel-icon-efficiency"></div>
              <span class="panel-title">亩均分析（万元/亩）</span>
            </div>
            <div class="panel-legend">
              <span class="legend-item"><span class="legend-dot" style="background:#fbbf24"></span>生产性服务类</span>
              <span class="legend-item"><span class="legend-dot" style="background:#22d3ee"></span>平均值</span>
              <span class="legend-item"><span class="legend-dot" style="background:#a855f7"></span>生产制造类</span>
            </div>
            <div class="efficiency-tabs">
              <span :class="['efficiency-tab', { active: efficiencyTab === 0 }]" @click="efficiencyTab = 0">产值分析</span>
              <span :class="['efficiency-tab', { active: efficiencyTab === 1 }]" @click="efficiencyTab = 1">税收分析</span>
            </div>
            <div ref="efficiencyChart" class="panel-chart efficiency-chart"></div>
          </div>

          <div class="chart-panel district-performance-panel">
            <div class="panel-corner panel-corner-top-left"></div>
            <div class="panel-corner panel-corner-top-right"></div>
            <div class="panel-corner panel-corner-bottom-left"></div>
            <div class="panel-corner panel-corner-bottom-right"></div>
            <div class="panel-header">
              <div class="panel-icon panel-icon-district"></div>
              <span class="panel-title">各区县园区绩效分档统计</span>
            </div>
            <div class="panel-legend">
              <span class="legend-item"><span class="legend-box" style="background:#5a6478"></span>服务类</span>
              <span class="legend-item"><span class="legend-box" style="background:#8a96a8"></span>制造类</span>
              <span class="legend-item"><span class="legend-box" style="background:#22d3ee"></span>A档</span>
              <span class="legend-item"><span class="legend-box" style="background:#10b981"></span>B档</span>
              <span class="legend-item"><span class="legend-box" style="background:#fbbf24"></span>C档</span>
              <span class="legend-item"><span class="legend-box" style="background:#a855f7"></span>D档</span>
              <span class="legend-item"><span class="legend-line" style="background:#ef4444"></span>亩均税收</span>
              <span class="legend-item"><span class="legend-line" style="background:#3b82f6"></span>亩均产出</span>
            </div>
            <div ref="districtPerfChart" class="panel-chart district-perf-chart"></div>
            <div ref="districtPerfMapBg" class="district-perf-map-bg"></div>
            <div
              v-if="hoveredDistrictPerf"
              class="district-perf-tooltip"
              :style="districtPerfTooltipPos"
            >
              <div class="perf-tooltip-title">{{ hoveredDistrictPerf.name }}园区总数：{{ hoveredDistrictPerf.total }}</div>
              <div class="perf-tooltip-grid">
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">服务类</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.service }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">制造类</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.manufacturing }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">A档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeA }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">A档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeA }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">B档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeB }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">B档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeB }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">C档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeC }}</span>
                </div>
                <div class="perf-tooltip-item">
                  <span class="perf-item-label">C档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeC }}</span>
                </div>
                <div class="perf-tooltip-item d-grade">
                  <span class="perf-item-label">D档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeD }}</span>
                </div>
                <div class="perf-tooltip-item d-grade">
                  <span class="perf-item-label">D档</span>
                  <span class="perf-item-value">{{ hoveredDistrictPerf.gradeD }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <div class="footer">
      <div class="footer-tab" :class="{ active: activeTab === 'overview' }" @click="switchTab('overview')">
        <span class="tab-text">全市园区概况</span>
      </div>
      <div class="footer-divider"></div>
      <div class="footer-tab" :class="{ active: activeTab === 'analysis' }" @click="switchTab('analysis')">
        <span class="tab-text">园区评价分析</span>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import hangzhouMap from '@/../public/hangzhou-map.json'
import '@/styles/big-screen.css'
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
      hoveredDistrict: null,
      hoveredDistrictData: null,
      hoveredCard: null,
      efficiencyTab: 0,
      activeTab: 'overview',
      currentTime: '',
      mapChart: null,
      analysisChart: null,
      efficiencyChart: null,
      districtPerfChart: null,
      techInnovGauge: null,
      enterpriseCultivateChart: null,
      timer: null,
      performanceData: [],
      hoveredDistrictPerf: null,
      districtPerfTooltipPos: { left: '0px', top: '0px' }
    }
  },
  computed: {
    leftStats() {
      return [
        { label: '园区总数', value: this.stats.parkTotal || 728, unit: '处', change: this.stats.parkTotalChange || 7.34 },
        { label: '四星级园区数', value: this.stats.fourStarCount || 4805, unit: '家', change: this.stats.fourStarChange || 10.41 },
        { label: '五星级园区数', value: this.stats.fiveStarCount || 2124, unit: '家', change: this.stats.fiveStarChange || 8.56 },
        { label: '生产制造类园区数', value: this.stats.manufacturingCount || 415, unit: '家', change: this.stats.manufacturingChange || -8.20 },
        { label: '生产服务类园区数', value: this.stats.serviceCount || 313, unit: '家', change: this.stats.serviceChange || 6.50 },
        { label: '实际用地数', value: this.formatNumber(this.stats.landArea) || 2859.48, unit: '亩', change: this.stats.landAreaChange || 4.12 },
        { label: '已建建筑面积', value: this.formatNumber(this.stats.buildArea) || 118956.32, unit: '平米', change: this.stats.buildAreaChange || 13.98 }
      ]
    },
    rightStats() {
      return [
        { label: '员工总数', value: this.stats.employeeTotal || 13208, unit: '人', change: this.stats.employeeTotalChange || 11.02 },
        { label: '参评企业总数', value: this.stats.participateEnterpriseCount || 686, unit: '家', change: this.stats.participateEnterpriseChange || 8.41 },
        { label: '全市亩均产值', value: this.stats.revenuePerMu || 1.56, unit: '万元', change: this.stats.revenuePerMuChange || 6.21 },
        { label: '全市亩均税收', value: this.stats.taxPerMu || 108.20, unit: '元', change: this.stats.taxPerMuChange || -5.00 },
        { label: '国家专精特新小巨人', value: this.stats.nationalSpecializedCount || 16, unit: '家', change: this.stats.nationalSpecializedChange || 11.81 },
        { label: '省专精特新中小企业', value: this.stats.provincialSpecializedCount || 48, unit: '家', change: this.stats.provincialSpecializedChange || 15.20 },
        { label: '创新型中小企业', value: this.stats.innovativeSmeCount || 86, unit: '家', change: this.stats.innovativeSmeChange || 20.18 }
      ]
    },
    tooltipPosition() {
      if (!this.hoveredDistrictData) return {}
      const d = this.hoveredDistrictData
      const mapContainer = this.$refs.mapChart?.parentElement
      if (!mapContainer) return {}
      const rect = mapContainer.getBoundingClientRect()
      const offsetX = 150
      const offsetY = -120
      let left = d._px + offsetX
      let top = d._py + offsetY
      if (left + 420 > rect.width) left = d._px - 500
      if (top < 20) top = 20
      return { left: left + 'px', top: top + 'px' }
    }
  },
  async mounted() {
    window.addEventListener('resize', this.handleResize)
    this.startTimeUpdate()
    await this.loadData()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.timer && clearInterval(this.timer)
    this.mapChart && this.mapChart.dispose()
    this.analysisChart && this.analysisChart.dispose()
    this.efficiencyChart && this.efficiencyChart.dispose()
    this.districtPerfChart && this.districtPerfChart.dispose()
    this.techInnovGauge && this.techInnovGauge.dispose()
    this.enterpriseCultivateChart && this.enterpriseCultivateChart.dispose()
  },
  methods: {
    switchTab(tab) {
      if (this.activeTab === tab) return
      this.activeTab = tab
      this.$nextTick(() => {
        if (tab === 'analysis') {
          setTimeout(() => {
            this.initAnalysisChart()
            this.initTechInnovGauge()
            this.initEnterpriseCultivateChart()
            if (this.muJunData) this.initEfficiencyChart(this.muJunData)
            if (this.performanceData.length) this.initDistrictPerfChart()
          }, 100)
        } else {
          setTimeout(() => {
            this.initMapChart()
            this.calculateMarkerPositions()
          }, 100)
        }
      })
    },
    startTimeUpdate() {
      this.updateTime()
      this.timer = setInterval(() => this.updateTime(), 1000)
    },
    updateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    formatNumber(num) {
      if (!num) return 0
      return Number(num).toLocaleString('en-US', { maximumFractionDigits: 2 })
    },
    async loadData() {
      const [statsRes, districtRes, muJunRes, performanceRes] = await Promise.all([
        getBigScreenStats(this.year),
        getDistrictData(this.year),
        getMuJunAnalysis(this.year),
        getPerformanceStats(this.year)
      ])
      if (statsRes.code === 200) this.stats = statsRes.data
      if (muJunRes.code === 200) this.muJunData = muJunRes.data
      if (performanceRes.code === 200) this.performanceData = performanceRes.data
      await this.$nextTick()
      setTimeout(() => {
        this.$nextTick(() => {
          this.initMapChart()
        })
      }, 800)
    },
    initMapChart() {
      const chartDom = this.$refs.mapChart
      if (!chartDom) return
      if (this.mapChart) {
        this.mapChart.dispose()
      }
      this.mapChart = echarts.init(chartDom)
      echarts.registerMap('hangzhou', hangzhouMap)

      const geoCoords = {}
      hangzhouMap.features.forEach(feature => {
        geoCoords[feature.properties.name] = feature.properties.center
      })

      this.districtsConfig = [
        { name: '上城区', position: geoCoords['上城区'] || [120.17, 30.25], beamHeight: 70, labelOffset: [0, 0] },
        { name: '拱墅区', position: geoCoords['拱墅区'] || [120.15, 30.31], beamHeight: 90, labelOffset: [4, 0] },
        { name: '西湖区', position: geoCoords['西湖区'] ? [geoCoords['西湖区'][0] - 0.05, geoCoords['西湖区'][1] - 0.04] : [120.10, 30.23], beamHeight: 55, labelOffset: [7, 0] },
        { name: '滨江区', position: geoCoords['滨江区'] || [120.21, 30.21], beamHeight: 80, labelOffset: [17, 0] },
        { name: '萧山区', position: geoCoords['萧山区'] || [120.27, 30.16], beamHeight: 100, labelOffset: [8, 0] },
        { name: '余杭区', position: geoCoords['余杭区'] || [119.98, 30.27], beamHeight: 65, labelOffset: [-10, 0] },
        { name: '临平区', position: geoCoords['临平区'] || [120.30, 30.30], beamHeight: 110, labelOffset: [8, 0] },
        { name: '钱塘区', position: geoCoords['钱塘区'] || [120.49, 30.31], beamHeight: 75, labelOffset: [10, 0] },
        { name: '富阳区', position: geoCoords['富阳区'] || [119.95, 30.05], beamHeight: 60, labelOffset: [0, 0] },
        { name: '临安区', position: geoCoords['临安区'] || [119.72, 30.23], beamHeight: 50, labelOffset: [4, 0] },
        { name: '桐庐县', position: geoCoords['桐庐县'] || [119.64, 29.88], beamHeight: 65, labelOffset: [0, 0] },
        { name: '淳安县', position: geoCoords['淳安县'] || [119.05, 29.61], beamHeight: 55, labelOffset: [-3, 0] },
        { name: '建德市', position: geoCoords['建德市'] || [119.28, 29.49], beamHeight: 70, labelOffset: [0, 0] }
      ].filter(d => true)

      const option = {
        backgroundColor: 'transparent',
        tooltip: { show: false },
        geo: {
          map: 'hangzhou',
          roam: false,
          zoom: 1.2,
          aspectScale: 0.85,
          layoutCenter: ['50%', '50%'],
          layoutSize: '90%',
          silent: true,
          label: { show: false },
          itemStyle: {
            areaColor: {
              type: 'radial',
              x: 0.5, y: 0.5, r: 0.7,
              colorStops: [
                { offset: 0, color: '#1a4a8e' },
                { offset: 0.5, color: '#0d3066' },
                { offset: 1, color: '#061a38' }
              ]
            },
            borderColor: '#22d3ee',
            borderWidth: 1.5,
            shadowColor: 'rgba(34, 211, 238, 0.6)',
            shadowBlur: 15
          },
          emphasis: {
            focus: 'none',
            itemStyle: {
              areaColor: {
                type: 'radial',
                x: 0.5, y: 0.5, r: 0.7,
                colorStops: [
                  { offset: 0, color: '#2d6bc7' },
                  { offset: 0.5, color: '#1a4a8e' },
                  { offset: 1, color: '#0d3066' }
                ]
              },
              borderColor: '#fbbf24',
              borderWidth: 2,
              shadowColor: 'rgba(251, 191, 36, 0.8)',
              shadowBlur: 20
            },
            label: { show: false }
          }
        },
        series: [
          {
            // 黄色光点
            type: 'effectScatter',
            coordinateSystem: 'geo',
            geoIndex: 0,
            data: this.districtsConfig.map(d => ({
              name: d.name,
              value: d.position
            })),
            symbolSize: 10,
            showEffectOn: 'render',
            rippleEffect: {
              period: 3,
              scale: 4,
              brushType: 'stroke'
            },
            itemStyle: {
              color: '#fbbf24',
              shadowBlur: 10,
              shadowColor: 'rgba(251, 191, 36, 0.8)'
            },
            label: { show: false },
            emphasis: {
              scale: 1.5,
              itemStyle: {
                color: '#fcd34d',
                shadowBlur: 20,
                shadowColor: 'rgba(251, 191, 36, 1)'
              }
            },
            zlevel: 2
          }
        ]
      }
      this.mapChart.setOption(option)
      this.mapChart.on('mouseover', { seriesIndex: 0 }, (params) => {
        if (params.data && params.data.name) {
          const districtData = this.districtData.find(d => d.name === params.data.name)
          if (districtData) {
            this.handleMarkerHover(districtData)
          }
        }
      })
      this.mapChart.on('mouseout', { seriesIndex: 0 }, () => {
        this.handleMarkerLeave()
      })
      this.mapChart.on('click', { seriesIndex: 0 }, (params) => {
        if (params.data && params.data.name) {
          const districtData = this.districtData.find(d => d.name === params.data.name)
          if (districtData) {
            this.handleMarkerClick(districtData)
          }
        }
      })
      this.mapChart.on('finished', () => {
        setTimeout(() => {
          this.clearAllMapHighlights()
          this.calculateMarkerPositions()
        }, 200)
      })
      setTimeout(() => {
        this.clearAllMapHighlights()
        this.calculateMarkerPositions()
      }, 500)
    },
    initAnalysisChart() {
      const chartDom = this.$refs.analysisChart
      if (!chartDom) return
      if (this.analysisChart) {
        this.analysisChart.dispose()
      }
      this.analysisChart = echarts.init(chartDom)
      const years = ['2023', '2024', '2025']
      const serviceData = [25, 35, 38]
      const manufacturingData = [32, 42, 35]
      const totalData = [57, 77, 73]

      const option = {
        backgroundColor: 'transparent',
        grid: { top: 25, right: 20, bottom: 30, left: 45 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 15, 30, 0.95)',
          borderColor: '#22d3ee',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 }
        },
        xAxis: {
          type: 'category',
          data: years,
          axisLine: { lineStyle: { color: '#0e7490' } },
          axisLabel: { color: '#7dd3fc', fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: [
          {
            type: 'value',
            name: '',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 11 },
            splitLine: { lineStyle: { color: 'rgba(34, 211, 238, 0.1)' } }
          },
          {
            type: 'value',
            name: '',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 11 },
            splitLine: { show: false }
          }
        ],
        series: [
          {
            name: '服务',
            type: 'bar',
            data: serviceData,
            itemStyle: { color: '#22d3ee', borderRadius: [4, 4, 0, 0] },
            barWidth: '18%',
            barGap: '20%'
          },
          {
            name: '制造',
            type: 'bar',
            data: manufacturingData,
            itemStyle: { color: '#10b981', borderRadius: [4, 4, 0, 0] },
            barWidth: '18%'
          },
          {
            name: '总数',
            type: 'line',
            yAxisIndex: 1,
            data: totalData,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { color: '#fbbf24', width: 3, shadowBlur: 10, shadowColor: '#fbbf24' },
            itemStyle: { color: '#fbbf24', borderColor: '#fff', borderWidth: 2 }
          }
        ]
      }
      this.analysisChart.setOption(option)
    },
    initEfficiencyChart(data) {
      const chartDom = this.$refs.efficiencyChart
      if (!chartDom) return
      if (this.efficiencyChart) {
        this.efficiencyChart.dispose()
      }
      this.efficiencyChart = echarts.init(chartDom)
      const seriesData = this.efficiencyTab === 0
        ? [
            { name: '生产性服务类', color: '#fbbf24', data: data.map(i => i.serviceOut) },
            { name: '平均值', color: '#22d3ee', data: data.map(i => i.averageOut) },
            { name: '生产制造类', color: '#a855f7', data: data.map(i => i.manufacturingOut) }
          ]
        : [
            { name: '生产性服务类', color: '#fbbf24', data: data.map(i => i.service) },
            { name: '平均值', color: '#22d3ee', data: data.map(i => i.average) },
            { name: '生产制造类', color: '#a855f7', data: data.map(i => i.manufacturing) }
          ]

      const option = {
        backgroundColor: 'transparent',
        grid: { top: 20, right: 20, bottom: 30, left: 45 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 15, 30, 0.95)',
          borderColor: '#22d3ee',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 }
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
          name: '',
          nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
          axisLine: { show: false },
          axisLabel: { color: '#7dd3fc', fontSize: 11 },
          splitLine: { lineStyle: { color: 'rgba(34, 211, 238, 0.1)' } }
        },
        series: seriesData.map(s => ({
          name: s.name,
          type: 'line',
          data: s.data,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: s.color, width: 2, shadowBlur: 8, shadowColor: s.color },
          itemStyle: { color: s.color, borderColor: '#fff', borderWidth: 1.5 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: s.color + '40' },
              { offset: 1, color: s.color + '05' }
            ])
          }
        }))
      }
      this.efficiencyChart.setOption(option)
    },
    initTechInnovGauge() {
      const chartDom = this.$refs.techInnovGauge
      if (!chartDom) return
      if (this.techInnovGauge) {
        this.techInnovGauge.dispose()
      }
      this.techInnovGauge = echarts.init(chartDom)

      const option = {
        backgroundColor: 'transparent',
        series: [
          {
            type: 'gauge',
            startAngle: 90,
            endAngle: -270,
            pointer: { show: false },
            progress: {
              show: true,
              overlap: false,
              roundCap: false,
              clip: false,
              itemStyle: {
                color: '#22d3ee'
              }
            },
            axisLine: {
              lineStyle: {
                width: 8,
                color: [[1, 'rgba(34, 211, 238, 0.15)']]
              }
            },
            splitLine: { show: false },
            axisTick: { show: false },
            axisLabel: { show: false },
            data: [
              { value: 23, name: '科技型企业', detail: { valueAnimation: true, offsetCenter: ['0%', '-5%'] } }
            ],
            detail: {
              width: 50,
              height: 14,
              fontSize: 14,
              color: '#fff',
              backgroundColor: 'transparent',
              borderRadius: 0,
              formatter: '{value}'
            },
            title: {
              offsetCenter: ['0%', '20%'],
              fontSize: 12,
              color: '#7dd3fc'
            }
          },
          {
            type: 'gauge',
            startAngle: 90,
            endAngle: -270,
            pointer: { show: false },
            progress: {
              show: true,
              overlap: false,
              roundCap: false,
              clip: false,
              itemStyle: {
                color: '#10b981'
              }
            },
            axisLine: { show: false },
            splitLine: { show: false },
            axisTick: { show: false },
            axisLabel: { show: false },
            data: [
              { value: 50, name: '高企', detail: { show: false } }
            ],
            radius: '75%',
            detail: { show: false },
            title: { show: false }
          },
          {
            type: 'gauge',
            startAngle: 90,
            endAngle: -270,
            pointer: { show: false },
            progress: {
              show: true,
              overlap: false,
              roundCap: false,
              clip: false,
              itemStyle: {
                color: '#fbbf24'
              }
            },
            axisLine: { show: false },
            splitLine: { show: false },
            axisTick: { show: false },
            axisLabel: { show: false },
            data: [
              { value: 75, name: '浙江制造精品', detail: { show: false } }
            ],
            radius: '60%',
            detail: { show: false },
            title: { show: false }
          }
        ]
      }
      this.techInnovGauge.setOption(option)
    },
    initEnterpriseCultivateChart() {
      const chartDom = this.$refs.enterpriseCultivateChart
      if (!chartDom) return
      if (this.enterpriseCultivateChart) {
        this.enterpriseCultivateChart.dispose()
      }
      this.enterpriseCultivateChart = echarts.init(chartDom)

      const categories = ['线上', '规上', '高新', '省专精特新', '国家专精特新', '单项冠军', '隐形冠军', '独角兽']
      const newData = [15, 28, 22, 18, 8, 5, 3, 2]
      const totalData = [45, 68, 72, 55, 28, 15, 12, 8]

      const option = {
        backgroundColor: 'transparent',
        grid: { top: 15, right: 15, bottom: 25, left: 40 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 15, 30, 0.95)',
          borderColor: '#22d3ee',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 },
          axisPointer: { type: 'shadow' }
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLine: { lineStyle: { color: '#0e7490' } },
          axisLabel: { color: '#7dd3fc', fontSize: 9, interval: 0, rotate: 20 },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisLabel: { color: '#7dd3fc', fontSize: 10 },
          splitLine: { lineStyle: { color: 'rgba(34, 211, 238, 0.08)' } }
        },
        series: [
          {
            name: '新增',
            type: 'bar',
            data: newData,
            itemStyle: { color: '#10b981', borderRadius: [3, 3, 0, 0] },
            barWidth: '30%'
          },
          {
            name: '总数',
            type: 'bar',
            data: totalData,
            itemStyle: { color: '#3b82f6', borderRadius: [3, 3, 0, 0] },
            barWidth: '30%'
          }
        ]
      }
      this.enterpriseCultivateChart.setOption(option)
    },
    initDistrictPerfChart() {
      const chartDom = this.$refs.districtPerfChart
      if (!chartDom) return
      if (this.districtPerfChart) {
        this.districtPerfChart.dispose()
      }
      this.districtPerfChart = echarts.init(chartDom)
      const data = this.performanceData

      const option = {
        backgroundColor: 'transparent',
        grid: { top: 30, right: 40, bottom: 40, left: 40 },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(5, 15, 30, 0.95)',
          borderColor: '#22d3ee',
          borderWidth: 1,
          textStyle: { color: '#fff', fontSize: 12 },
          axisPointer: { type: 'shadow' }
        },
        xAxis: {
          type: 'category',
          data: data.map(i => i.name),
          axisLine: { lineStyle: { color: '#0e7490' } },
          axisLabel: { color: '#7dd3fc', fontSize: 10, interval: 0, rotate: 30 },
          axisTick: { show: false }
        },
        yAxis: [
          {
            type: 'value',
            name: '',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 10 },
            splitLine: { lineStyle: { color: 'rgba(34, 211, 238, 0.08)' } }
          },
          {
            type: 'value',
            name: '',
            nameTextStyle: { color: '#7dd3fc', fontSize: 10 },
            axisLine: { show: false },
            axisLabel: { color: '#7dd3fc', fontSize: 10 },
            splitLine: { show: false }
          }
        ],
        series: [
          { name: '服务类', type: 'bar', stack: 'type', data: data.map(i => i.service), itemStyle: { color: '#5a6478' }, barWidth: 12 },
          { name: '制造类', type: 'bar', stack: 'type', data: data.map(i => i.manufacturing), itemStyle: { color: '#8a96a8' }, barWidth: 12 },
          { name: 'A档', type: 'bar', stack: 'grade', data: data.map(i => i.gradeA), itemStyle: { color: '#22d3ee' }, barWidth: 12 },
          { name: 'B档', type: 'bar', stack: 'grade', data: data.map(i => i.gradeB), itemStyle: { color: '#10b981' }, barWidth: 12 },
          { name: 'C档', type: 'bar', stack: 'grade', data: data.map(i => i.gradeC), itemStyle: { color: '#fbbf24' }, barWidth: 12 },
          { name: 'D档', type: 'bar', stack: 'grade', data: data.map(i => i.gradeD), itemStyle: { color: '#a855f7' }, barWidth: 12 },
          {
            name: '亩均税收',
            type: 'line',
            yAxisIndex: 1,
            data: data.map(i => i.taxPerMu),
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            lineStyle: { color: '#ef4444', width: 2, shadowBlur: 6, shadowColor: '#ef4444' },
            itemStyle: { color: '#ef4444', borderColor: '#fff', borderWidth: 1 }
          },
          {
            name: '亩均产出',
            type: 'line',
            yAxisIndex: 1,
            data: data.map(i => i.revenuePerMu),
            smooth: true,
            symbol: 'circle',
            symbolSize: 5,
            lineStyle: { color: '#3b82f6', width: 2, shadowBlur: 6, shadowColor: '#3b82f6' },
            itemStyle: { color: '#3b82f6', borderColor: '#fff', borderWidth: 1 }
          }
        ]
      }
      this.districtPerfChart.setOption(option)

      this.districtPerfChart.on('mouseover', (params) => {
        if (params.componentType === 'series') {
          const idx = params.dataIndex
          const item = data[idx]
          this.hoveredDistrictPerf = {
            name: item.name,
            total: item.service + item.manufacturing,
            service: item.service,
            manufacturing: item.manufacturing,
            gradeA: item.gradeA,
            gradeB: item.gradeB,
            gradeC: item.gradeC,
            gradeD: item.gradeD
          }
          const chartDom = this.$refs.districtPerfChart
          if (chartDom) {
            const rect = chartDom.getBoundingClientRect()
            const point = this.districtPerfChart.convertToPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [idx, 0])
            this.districtPerfTooltipPos = {
              left: (point[0] - 120) + 'px',
              top: (point[1] - 180) + 'px'
            }
          }
        }
      })
      this.districtPerfChart.on('mouseout', () => {
        this.hoveredDistrictPerf = null
      })
    },
    handleMarkerHover(d) {
      if (this.hoveredDistrict === d.name) return
      if (this.mapChart && this.hoveredDistrict) {
        this.mapChart.dispatchAction({
          type: 'downplay',
          geoIndex: 0,
          name: this.hoveredDistrict
        })
      }
      this.hoveredDistrict = d.name
      let px = [0, 0]
      try {
        px = this.mapChart.convertToPixel({ geoIndex: 0 }, d.position) || [0, 0]
      } catch (e) {}
      this.hoveredDistrictData = {
        ...d,
        _px: px[0],
        _py: px[1]
      }
      if (this.mapChart) {
        this.mapChart.dispatchAction({
          type: 'highlight',
          geoIndex: 0,
          name: d.name
        })
      }
    },
    handleMarkerLeave() {
      if (!this.hoveredDistrict) return
      if (this.mapChart) {
        this.mapChart.dispatchAction({
          type: 'downplay',
          geoIndex: 0,
          name: this.hoveredDistrict
        })
      }
      this.hoveredDistrict = null
      this.hoveredDistrictData = null
    },
    handleMarkerClick(d) {
      if (this.hoveredDistrict === d.name) {
        this.handleMarkerLeave()
      } else {
        this.handleMarkerHover(d)
      }
    },
    clearAllMapHighlights() {
      if (!this.mapChart || !this.districtsConfig) return
      this.districtsConfig.forEach(d => {
        this.mapChart.dispatchAction({
          type: 'downplay',
          geoIndex: 0,
          name: d.name
        })
      })
    },
    handleCardHover(index, side) {
      this.hoveredCard = side + '-' + index
    },
    handleCardLeave(index, side) {
      if (this.hoveredCard === side + '-' + index) {
        this.hoveredCard = null
      }
    },
    handleResize() {
      if (this.activeTab === 'overview') {
        this.mapChart && this.mapChart.resize()
        this.calculateMarkerPositions()
      } else {
        this.analysisChart && this.analysisChart.resize()
        this.efficiencyChart && this.efficiencyChart.resize()
        this.districtPerfChart && this.districtPerfChart.resize()
        this.techInnovGauge && this.techInnovGauge.resize()
        this.enterpriseCultivateChart && this.enterpriseCultivateChart.resize()
      }
    },
    calculateMarkerPositions() {
      if (!this.mapChart || !this.districtsConfig || !this.$refs.mapChart) return
      const chartDom = this.$refs.mapChart
      const W = chartDom.offsetWidth
      const H = chartDom.offsetHeight
      // 3D 变换参数（与 CSS 保持一致）
      const TILT_DEG = 35
      const PERSPECTIVE = 2000
      const SCALE = 1.05
      const theta = TILT_DEG * Math.PI / 180
      const sinT = Math.sin(theta)
      const cosT = Math.cos(theta)
      const industryMap = {
        '上城区': '数字经济/金融服务',
        '拱墅区': '商贸服务/文化创意',
        '西湖区': '互联网/高新技术',
        '滨江区': '物联网/数字经济',
        '萧山区': '智能制造/装备制造',
        '余杭区': '智能制造/生物医药',
        '临平区': '智能制造/时尚产业',
        '钱塘区': '生物医药/智能制造',
        '富阳区': '造纸/新材料',
        '临安区': '绿色制造/新能源',
        '桐庐县': '制笔/医疗器械',
        '淳安县': '生态经济/旅游',
        '建德市': '化工/装备制造'
      }
      this.districtData = this.districtsConfig.map(d => {
        const baseData = {
          ...d,
          _screenX: 0,
          _screenY: 0,
          _beamScreenLen: 50,
          _labelOffsetX: (d.labelOffset && d.labelOffset[0]) || 0,
          parkCount: Math.floor(Math.random() * 80) + 40,
          mainIndustry: industryMap[d.name] || '制造业',
          manufacturingCount: Math.floor(Math.random() * 100) + 20,
          serviceCount: Math.floor(Math.random() * 50) + 10,
          revenuePerMu: (Math.random() * 3 + 0.8).toFixed(2),
          taxPerMu: (Math.random() * 200 + 50).toFixed(2),
          participateEnterpriseCount: Math.floor(Math.random() * 80) + 20,
          nationalSpecializedCount: Math.floor(Math.random() * 8),
          provincialSpecializedCount: Math.floor(Math.random() * 20),
          innovativeSmeCount: Math.floor(Math.random() * 60) + 20
        }
        try {
          const px = this.mapChart.convertToPixel({ geoIndex: 0 }, d.position)
          if (!px) return baseData
          const cx = px[0]
          const cy = px[1]
          // 以画布中心为原点
          const localX = cx - W / 2
          const localY = cy - H / 2
          // CSS 变换顺序: rotateX(35deg) scale(1.05)，原点为中心
          // 1) scale(1.05): (localX, localY) -> (s*localX, s*localY)
          // 2) rotateX(theta): y 方向产生深度 Z = s*localY*sin(theta)
          const scaledY = SCALE * localY
          const z = scaledY * sinT
          // 透视投影
          const factor = PERSPECTIVE / (PERSPECTIVE - z)
          const projectedX = SCALE * localX * factor
          const projectedY = scaledY * cosT * factor
          // 还原成画布局部坐标（以中心为原点）
          const screenX = projectedX + W / 2
          const screenY = projectedY + H / 2
          // 光束屏幕长度：基于 beamHeight
          const beamLen = d.beamHeight || 60
          return {
            ...baseData,
            _screenX: screenX,
            _screenY: screenY,
            _beamScreenLen: beamLen
          }
        } catch (e) {
          return baseData
        }
      })
    }
  },
  watch: {
    efficiencyTab() {
      if (this.activeTab === 'analysis' && this.muJunData) {
        this.initEfficiencyChart(this.muJunData)
      }
    }
  }
}
</script>

<style>
.big-screen {
  width: 100vw;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  background: #030a16;
  color: #fff;
  z-index: 9999;
}

.screen-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 50% 40%, rgba(20, 70, 140, 0.35) 0%, transparent 55%),
    radial-gradient(ellipse at 20% 70%, rgba(34, 211, 238, 0.1) 0%, transparent 40%),
    radial-gradient(ellipse at 80% 70%, rgba(34, 211, 238, 0.08) 0%, transparent 40%),
    linear-gradient(180deg, #030a16 0%, #051325 40%, #030a16 100%);
  z-index: 1;
}

.screen-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(34, 211, 238, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(34, 211, 238, 0.04) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 2;
}

.screen-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, transparent 40%, rgba(0, 0, 0, 0.7) 100%);
  z-index: 3;
  pointer-events: none;
}

.screen-particles {
  position: absolute;
  inset: 0;
  z-index: 4;
  pointer-events: none;
  background-image:
    radial-gradient(circle at 20% 30%, rgba(34, 211, 238, 0.15) 1px, transparent 1px),
    radial-gradient(circle at 80% 60%, rgba(34, 211, 238, 0.1) 1px, transparent 1px),
    radial-gradient(circle at 50% 80%, rgba(34, 211, 238, 0.12) 1px, transparent 1px),
    radial-gradient(circle at 30% 70%, rgba(34, 211, 238, 0.08) 1px, transparent 1px),
    radial-gradient(circle at 70% 40%, rgba(34, 211, 238, 0.06) 1px, transparent 1px);
  background-size: 200px 200px;
  animation: particleFloat 20s ease-in-out infinite;
}

@keyframes particleFloat {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(30px, -20px); }
  50% { transform: translate(-20px, 30px); }
  75% { transform: translate(20px, -10px); }
}

.screen-scan-line {
  position: absolute;
  inset: 0;
  z-index: 5;
  pointer-events: none;
  background: linear-gradient(180deg,
    transparent 0%,
    rgba(34, 211, 238, 0.02) 50%,
    transparent 100%);
  animation: scanLine 8s linear infinite;
}

@keyframes scanLine {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100vh); }
}

.header {
  position: relative;
  z-index: 10;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1920 80'%3E%3Cdefs%3E%3ClinearGradient id='headerGrad' x1='0%25' y1='0%25' x2='100%25' y2='0%25'%3E%3Cstop offset='0%25' style='stop-color:%23030a16;stop-opacity:0' /%3E%3Cstop offset='50%25' style='stop-color:%230a1f3d;stop-opacity:1' /%3E%3Cstop offset='100%25' style='stop-color:%23030a16;stop-opacity:0' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect fill='url(%23headerGrad)' width='1920' height='80'/%3E%3C/svg%3E") center top no-repeat;
  background-size: 100% 100%;
  flex-shrink: 0;
}

.header-left {
  flex: 1;
}

.header-time {
  font-size: 15px;
  color: #7dd3fc;
  font-family: 'Courier New', monospace;
  text-shadow: 0 0 8px rgba(34, 211, 238, 0.5);
  letter-spacing: 2px;
}

.header-center {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.header-decoration-left,
.header-decoration-right {
  width: 180px;
  height: 3px;
  background: linear-gradient(90deg, transparent 0%, #22d3ee 50%, transparent 100%);
  position: relative;
}

.header-decoration-left::before,
.header-decoration-right::before {
  content: '';
  position: absolute;
  top: -6px;
  width: 15px;
  height: 15px;
  border: 2px solid #22d3ee;
  transform: rotate(45deg);
  box-shadow: 0 0 10px rgba(34, 211, 238, 0.6);
}

.header-decoration-left::before {
  right: 0;
  border-left: none;
  border-bottom: none;
}

.header-decoration-right::before {
  left: 0;
  border-right: none;
  border-top: none;
}

.header-title-box {
  margin: 0 30px;
  position: relative;
}

.header-title {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
  letter-spacing: 6px;
  white-space: nowrap;
  text-shadow:
    0 0 10px rgba(34, 211, 238, 0.8),
    0 0 20px rgba(34, 211, 238, 0.5),
    0 0 30px rgba(34, 211, 238, 0.3);
  position: relative;
  z-index: 2;
}

.header-title::before,
.header-title::after {
  content: '';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  border: 2px solid #22d3ee;
}

.header-title::before {
  left: -35px;
  border-right: none;
  border-bottom: none;
}

.header-title::after {
  right: -35px;
  border-left: none;
  border-top: none;
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.year-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: linear-gradient(180deg, rgba(34, 211, 238, 0.15), rgba(5, 15, 30, 0.5));
  border: 1px solid rgba(34, 211, 238, 0.4);
  border-radius: 4px;
  cursor: pointer;
  box-shadow: 0 0 15px rgba(34, 211, 238, 0.2);
  transition: all 0.3s ease;
}

.year-selector:hover {
  background: linear-gradient(180deg, rgba(34, 211, 238, 0.25), rgba(5, 15, 30, 0.6));
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.4);
}

.year-text {
  font-size: 18px;
  font-weight: bold;
  color: #22d3ee;
  text-shadow: 0 0 8px rgba(34, 211, 238, 0.6);
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

.year-icon {
  color: #22d3ee;
  font-size: 12px;
}

.main-content {
  position: relative;
  z-index: 10;
  display: flex;
  flex: 1;
  padding: 15px 25px;
  gap: 20px;
  min-height: 0;
  overflow: hidden;
}

.side-panel {
  width: 200px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.stat-card {
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.6) 0%, rgba(5, 15, 30, 0.4) 100%);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 6px;
  padding: 12px 15px;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, rgba(34, 211, 238, 0.6) 50%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover,
.stat-card.active {
  border-color: rgba(34, 211, 238, 0.5);
  box-shadow:
    0 0 20px rgba(34, 211, 238, 0.2),
    inset 0 0 20px rgba(34, 211, 238, 0.06);
  transform: translateY(-3px);
}

.stat-card:hover::before,
.stat-card.active::before {
  opacity: 1;
}

.card-corner {
  position: absolute;
  width: 10px;
  height: 10px;
  border: 2px solid rgba(34, 211, 238, 0.4);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover .card-corner,
.stat-card.active .card-corner {
  opacity: 1;
}

.card-corner-top-left {
  top: 6px;
  left: 6px;
  border-right: none;
  border-bottom: none;
}

.card-corner-top-right {
  top: 6px;
  right: 6px;
  border-left: none;
  border-bottom: none;
}

.card-corner-bottom-left {
  bottom: 6px;
  left: 6px;
  border-right: none;
  border-top: none;
}

.card-corner-bottom-right {
  bottom: 6px;
  right: 6px;
  border-left: none;
  border-top: none;
}

.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, rgba(34, 211, 238, 0.06) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.4s;
  pointer-events: none;
}

.stat-card:hover .card-glow,
.stat-card.active .card-glow {
  opacity: 1;
}

.card-pulse {
  position: absolute;
  inset: 0;
  border-radius: 6px;
  border: 1px solid rgba(34, 211, 238, 0.3);
  animation: cardPulse 2s ease-in-out infinite;
  opacity: 0;
  pointer-events: none;
}

.stat-card:hover .card-pulse,
.stat-card.active .card-pulse {
  opacity: 0.5;
}

@keyframes cardPulse {
  0% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.015); opacity: 0; }
  100% { transform: scale(1); opacity: 0.5; }
}

.card-top {
  margin-bottom: 6px;
}

.card-label {
  font-size: 12px;
  color: #6a8caf;
  letter-spacing: 1px;
}

.card-center {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 6px;
}

.value-num {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
  text-shadow: 0 0 12px rgba(34, 211, 238, 0.5);
  letter-spacing: 1px;
  transition: all 0.3s;
}

.stat-card:hover .value-num,
.stat-card.active .value-num {
  color: #22d3ee;
  text-shadow: 0 0 20px rgba(34, 211, 238, 0.8);
}

.value-unit {
  font-size: 11px;
  color: #6a8caf;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-change-label {
  font-size: 10px;
  color: #5a7a9a;
}

.card-change-value {
  font-size: 11px;
  font-weight: bold;
}

.card-change-value.up { color: #10b981; }
.card-change-value.down { color: #ef4444; }

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
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(5, 15, 30, 0.8) 0%, rgba(3, 8, 16, 0.9) 100%);
  box-shadow:
    inset 0 0 50px rgba(34, 211, 238, 0.04),
    0 0 20px rgba(34, 211, 238, 0.08);
  perspective: 2000px;
}

.map-3d-layer {
  position: absolute;
  inset: 0;
  transform-style: preserve-3d;
  transform: rotateX(35deg) scale(1.05);
  transform-origin: center center;
}

.map-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(34, 211, 238, 0.08);
  border-radius: 50%;
  pointer-events: none;
}

.map-ring-1 { width: 50%; height: 80%; border-width: 2px; border-color: rgba(34, 211, 238, 0.2); }
.map-ring-2 { width: 65%; height: 100%; border-color: rgba(34, 211, 238, 0.12); }
.map-ring-3 { width: 80%; height: 120%; border-color: rgba(34, 211, 238, 0.06); }
.map-ring-4 { width: 95%; height: 140%; border-style: dashed; border-color: rgba(34, 211, 238, 0.04); }

.map-center-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40%;
  height: 60%;
  background: radial-gradient(ellipse at center, rgba(34, 211, 238, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: centerGlow 4s ease-in-out infinite;
}

@keyframes centerGlow {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.05); }
}

.map-radar {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.map-radar::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 50%;
  height: 80%;
  border: 1px solid rgba(34, 211, 238, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: radarRotate 8s linear infinite;
}

.map-radar::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 50%;
  height: 80%;
  background: conic-gradient(from 0deg, transparent 0deg, rgba(34, 211, 238, 0.2) 30deg, transparent 60deg);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: radarRotate 8s linear infinite;
}

@keyframes radarRotate {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

.map-chart {
  position: absolute;
  inset: 0;
  z-index: 5;
}

.vertical-beams {
  position: absolute;
  inset: 0;
  z-index: 15;
  pointer-events: none;
}

.v-beam {
  position: absolute;
  width: 2px;
  background: linear-gradient(180deg,
    rgba(251, 191, 36, 0.95) 0%,
    rgba(251, 191, 36, 0.5) 60%,
    rgba(251, 191, 36, 0) 100%);
  box-shadow: 0 0 6px rgba(251, 191, 36, 0.6);
  transform-origin: center bottom;
  pointer-events: none;
  border-radius: 1px;
}

.v-beam.active {
  background: linear-gradient(180deg,
    rgba(252, 211, 77, 1) 0%,
    rgba(252, 211, 77, 0.7) 60%,
    rgba(252, 211, 77, 0) 100%);
  box-shadow: 0 0 10px rgba(251, 191, 36, 0.9);
}

.v-label {
  position: absolute;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: bold;
  color: #fbbf24;
  background: rgba(5, 15, 30, 0.9);
  border: 1px solid rgba(251, 191, 36, 0.8);
  border-radius: 3px;
  white-space: nowrap;
  box-shadow: 0 0 8px rgba(251, 191, 36, 0.4);
  pointer-events: auto;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 1px;
}

.v-label:hover,
.v-label.active {
  background: rgba(251, 191, 36, 0.95);
  color: #051325;
  box-shadow: 0 0 16px rgba(251, 191, 36, 0.7);
  border-color: #fbbf24;
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
  transform-origin: center bottom;
  pointer-events: auto;
  cursor: pointer;
  width: 30px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.map-marker:hover,
.map-marker.active {
  transform: translate(-50%, -100%) scale(1.1);
}

.marker-core {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 10px;
  background: #fbbf24;
  border-radius: 50%;
  box-shadow:
    0 0 12px #fbbf24,
    0 0 25px rgba(251, 191, 36, 0.7),
    0 0 35px rgba(251, 191, 36, 0.4);
  z-index: 4;
}

.map-marker:hover .marker-core,
.map-marker.active .marker-core {
  box-shadow:
    0 0 15px #fbbf24,
    0 0 30px rgba(251, 191, 36, 0.9),
    0 0 50px rgba(251, 191, 36, 0.6);
}

.marker-outer-ring {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 20px;
  border: 2px solid rgba(251, 191, 36, 0.6);
  border-radius: 50%;
  animation: markerRingPulse 2s ease-out infinite;
  z-index: 3;
}

@keyframes markerRingPulse {
  0% { transform: translateX(-50%) scale(1); opacity: 0.8; }
  100% { transform: translateX(-50%) scale(2); opacity: 0; }
}

.marker-inner-ring {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 15px;
  height: 15px;
  border: 1px solid rgba(251, 191, 36, 0.8);
  border-radius: 50%;
  animation: markerRingPulse 2s ease-out infinite 0.5s;
  z-index: 3;
}

.marker-label-box {
  position: absolute;
  top: 0;
  left: calc(50% + var(--label-offset-x, 0px));
  transform: translateX(-50%);
  background: rgba(5, 15, 30, 0.95);
  border: 1px solid rgba(251, 191, 36, 0.8);
  padding: 3px 10px;
  border-radius: 3px;
  white-space: nowrap;
  box-shadow: 0 0 12px rgba(251, 191, 36, 0.25);
  z-index: 5;
  transition: all 0.3s;
}

.map-marker:hover .marker-label-box,
.map-marker.active .marker-label-box {
  background: rgba(251, 191, 36, 0.95);
  border-color: #fbbf24;
  box-shadow: 0 0 20px rgba(251, 191, 36, 0.5);
}

.marker-label-text {
  font-size: 11px;
  color: #fbbf24;
  font-weight: 600;
  letter-spacing: 1px;
}

.map-marker:hover .marker-label-text,
.map-marker.active .marker-label-text {
  color: #051325;
}

.marker-beam {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: calc(100% - 14px);
  background: linear-gradient(180deg,
    rgba(251, 191, 36, 0) 0%,
    rgba(251, 191, 36, 0.4) 10%,
    rgba(251, 191, 36, 0.85) 35%,
    rgba(251, 191, 36, 1) 60%,
    rgba(251, 191, 36, 0.95) 100%);
  box-shadow:
    0 0 6px rgba(251, 191, 36, 0.9),
    0 0 12px rgba(251, 191, 36, 0.6);
  z-index: 2;
  border-radius: 2px;
}

.marker-beam::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 14px;
  height: 14px;
  background: radial-gradient(circle,
    rgba(251, 191, 36, 1) 0%,
    rgba(251, 191, 36, 0.6) 40%,
    transparent 70%);
  border-radius: 50%;
  z-index: 3;
}

.district-tooltip {
  position: absolute;
  z-index: 50;
  width: 400px;
  background: linear-gradient(180deg, rgba(10, 25, 50, 0.98) 0%, rgba(5, 15, 30, 0.98) 100%);
  border: 2px solid rgba(251, 191, 36, 0.7);
  border-radius: 8px;
  box-shadow:
    0 0 30px rgba(251, 191, 36, 0.3),
    inset 0 0 25px rgba(251, 191, 36, 0.06);
  animation: tooltipFadeIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

@keyframes tooltipFadeIn {
  from { opacity: 0; transform: translateY(12px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.tooltip-arrow {
  position: absolute;
  left: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid rgba(251, 191, 36, 0.7);
}

.tooltip-header {
  padding: 12px 18px;
  background: linear-gradient(90deg, rgba(251, 191, 36, 0.25) 0%, rgba(251, 191, 36, 0.08) 100%);
  border-bottom: 1px solid rgba(251, 191, 36, 0.4);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tooltip-title {
  font-size: 16px;
  font-weight: bold;
  color: #fbbf24;
  text-shadow: 0 0 8px rgba(251, 191, 36, 0.5);
  letter-spacing: 2px;
}

.tooltip-close {
  width: 18px;
  height: 18px;
  cursor: pointer;
  position: relative;
}

.tooltip-close::before,
.tooltip-close::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 1px;
  background: #fbbf24;
}

.tooltip-close::before { transform: rotate(45deg); }
.tooltip-close::after { transform: rotate(-45deg); }

.tooltip-body {
  padding: 15px 18px;
}

.tooltip-row {
  font-size: 12px;
  color: #7dd3fc;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  line-height: 1.8;
  gap: 6px;
}

.tooltip-row:last-child {
  margin-bottom: 0;
}

.row-icon {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 2px;
  background: linear-gradient(135deg, #22d3ee 0%, #0e7490 100%);
  box-shadow: 0 0 5px rgba(34, 211, 238, 0.4);
  flex-shrink: 0;
}

.row-label {
  color: #6a8caf;
  flex-shrink: 0;
  font-size: 12px;
}

.row-value {
  color: #fff;
  font-weight: 600;
}

.tooltip-footer {
  height: 2px;
  background: linear-gradient(90deg, rgba(251, 191, 36, 0.4) 0%, rgba(251, 191, 36, 0.7) 50%, rgba(251, 191, 36, 0.4) 100%);
}

.compass {
  position: absolute;
  bottom: 12px;
  right: 15px;
  z-index: 15;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.compass-arrow {
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 15px solid #22d3ee;
  filter: drop-shadow(0 0 6px #22d3ee);
  margin-bottom: 2px;
}

.compass-letter {
  font-size: 14px;
  font-weight: bold;
  color: #22d3ee;
  text-shadow: 0 0 6px #22d3ee;
}

.analysis-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  width: 100%;
  height: 100%;
  gap: 15px;
}

.chart-panel {
  min-width: 0;
  min-height: 0;
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.5) 0%, rgba(5, 15, 30, 0.6) 100%);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 8px;
  padding: 12px 15px;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chart-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 10%, rgba(34, 211, 238, 0.6) 50%, transparent 90%);
}

.panel-corner {
  position: absolute;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(34, 211, 238, 0.4);
}

.panel-corner-top-left {
  top: 8px;
  left: 8px;
  border-right: none;
  border-bottom: none;
}

.panel-corner-top-right {
  top: 8px;
  right: 8px;
  border-left: none;
  border-bottom: none;
}

.panel-corner-bottom-left {
  bottom: 8px;
  left: 8px;
  border-right: none;
  border-top: none;
}

.panel-corner-bottom-right {
  bottom: 8px;
  right: 8px;
  border-left: none;
  border-top: none;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.panel-icon {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  background: linear-gradient(135deg, #22d3ee 0%, #0e7490 100%);
  box-shadow: 0 0 8px rgba(34, 211, 238, 0.5);
}

.panel-icon-analysis {
  clip-path: polygon(0 50%, 50% 0, 100% 50%, 50% 100%);
}

.panel-icon-efficiency {
  clip-path: polygon(0 0, 100% 0, 100% 30%, 50% 50%, 0 30%);
}

.panel-icon-performance {
  clip-path: polygon(0 100%, 0 30%, 50% 0, 100% 30%, 100% 100%);
}

.panel-icon-district {
  clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
}

.panel-title {
  font-size: 14px;
  font-weight: bold;
  color: #22d3ee;
  letter-spacing: 1.5px;
}

.panel-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 6px;
  font-size: 10px;
  color: #6a8caf;
  flex-shrink: 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-box {
  width: 12px;
  height: 8px;
  border-radius: 2px;
}

.legend-line {
  width: 14px;
  height: 2px;
  border-radius: 1px;
}

.panel-chart {
  flex: 1;
  min-height: 0;
}

.efficiency-tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 5px;
  flex-shrink: 0;
}

.efficiency-tab {
  font-size: 11px;
  color: #6a8caf;
  cursor: pointer;
  padding: 2px 10px;
  border-radius: 3px;
  transition: all 0.3s;
}

.efficiency-tab:hover { color: #22d3ee; }

.efficiency-tab.active {
  color: #22d3ee;
  background: rgba(34, 211, 238, 0.15);
  font-weight: bold;
}

.efficiency-chart {
  height: calc(100% - 50px);
}

.performance-content {
  display: flex;
  flex: 1;
  min-height: 0;
  gap: 10px;
}

.performance-left {
  flex: 0 0 40%;
  display: flex;
  flex-direction: column;
}

.perf-sub-title {
  font-size: 12px;
  color: #7dd3fc;
  margin-bottom: 5px;
  text-align: center;
}

.tech-gauge {
  flex: 1;
  min-height: 0;
}

.gauge-legend {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 5px 10px;
  font-size: 10px;
  color: #6a8caf;
}

.gauge-legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-bar {
  width: 20px;
  height: 4px;
  border-radius: 2px;
}

.performance-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.perf-right-legend {
  display: flex;
  gap: 12px;
  margin-bottom: 5px;
  font-size: 10px;
  color: #6a8caf;
}

.enterprise-chart {
  flex: 1;
  min-height: 0;
}

.district-performance-panel {
  position: relative;
}

.district-perf-chart {
  position: relative;
  z-index: 2;
}

.district-perf-map-bg {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 50%;
  opacity: 0.15;
  pointer-events: none;
  z-index: 1;
}

.district-perf-tooltip {
  position: absolute;
  z-index: 100;
  background: linear-gradient(180deg, rgba(10, 25, 50, 0.98) 0%, rgba(5, 15, 30, 0.98) 100%);
  border: 1px solid rgba(34, 211, 238, 0.6);
  border-radius: 6px;
  padding: 10px 12px;
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.3);
  pointer-events: none;
  min-width: 200px;
}

.perf-tooltip-title {
  font-size: 12px;
  color: #22d3ee;
  font-weight: bold;
  margin-bottom: 8px;
  text-align: center;
  border-bottom: 1px solid rgba(34, 211, 238, 0.3);
  padding-bottom: 6px;
}

.perf-tooltip-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px 15px;
  font-size: 11px;
}

.perf-tooltip-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.perf-item-label {
  color: #6a8caf;
}

.perf-item-value {
  color: #fff;
  font-weight: 600;
}

.perf-tooltip-item.d-grade .perf-item-label {
  color: #a855f7;
}

.footer {
  position: relative;
  z-index: 10;
  height: 50px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 60px;
  padding: 0 30px;
  flex-shrink: 0;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 50px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 50'%3E%3Cdefs%3E%3ClinearGradient id='footerGrad' x1='0%25' y1='0%25' x2='0%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%230a1f3d;stop-opacity:0' /%3E%3Cstop offset='100%25' style='stop-color:%230a1f3d;stop-opacity:0.8' /%3E%3C/linearGradient%3E%3C/defs%3E%3Cpath d='M0,0 L100,0 L120,20 L280,20 L300,0 L400,0 L400,50 L0,50 Z' fill='url(%23footerGrad)'/%3E%3C/svg%3E") center top no-repeat;
  background-size: 100% 100%;
  pointer-events: none;
}

.footer-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 30px;
  border-radius: 6px 6px 0 0;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
  border-bottom: none;
  position: relative;
  z-index: 2;
  margin-top: 5px;
}

.footer-tab:hover {
  border-color: rgba(34, 211, 238, 0.4);
  background: rgba(34, 211, 238, 0.08);
}

.footer-tab.active {
  border-color: rgba(34, 211, 238, 0.6);
  background: rgba(34, 211, 238, 0.15);
  margin-top: 0;
  padding-top: 13px;
}

.footer-tab.active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 10%, #22d3ee 50%, transparent 90%);
}

.tab-text {
  font-size: 14px;
  color: #6a8caf;
  transition: color 0.3s;
  letter-spacing: 2px;
}

.footer-tab:hover .tab-text { color: #22d3ee; }
.footer-tab.active .tab-text { color: #22d3ee; font-weight: bold; }

.footer-divider {
  width: 1px;
  height: 25px;
  background: rgba(34, 211, 238, 0.2);
  margin-top: 15px;
  position: relative;
  z-index: 2;
}
</style>
