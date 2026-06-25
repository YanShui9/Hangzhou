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
          <span class="year-text">{{ year }}</span>
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
                <span class="row-icon icon-factory"></span>
                <span class="row-label">生产制造类</span>
                <span class="row-value">{{ hoveredDistrictData.manufacturingCount }}家</span>
                <span class="row-icon icon-service" style="margin-left:20px"></span>
                <span class="row-label">生产服务类</span>
                <span class="row-value">{{ hoveredDistrictData.serviceCount }}家</span>
              </div>
              <div class="tooltip-row">
                <span class="row-icon icon-enterprise"></span>
                <span class="row-label">入驻企业</span>
                <span class="row-value">{{ hoveredDistrictData.enterpriseCount }}家</span>
                <span class="row-icon icon-employee" style="margin-left:20px"></span>
                <span class="row-label">员工总数</span>
                <span class="row-value">{{ hoveredDistrictData.employeeCount }}人</span>
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
          <div class="card-glow"></div>
          <div class="card-pulse"></div>
        </div>
      </div>
      </template>

      <template v-if="activeTab === 'analysis'">
        <div class="analysis-grid">
          <div class="analysis-card">
            <div class="analysis-header">
              <span class="analysis-title">园区数量类型分析</span>
            </div>
            <div ref="parkTypeChart" class="analysis-chart"></div>
          </div>

          <div class="analysis-card">
            <div class="analysis-header">
              <span class="analysis-title">绩效分析</span>
            </div>
            <div class="analysis-content">
              <div class="performance-row">
                <div ref="techRingChart" class="performance-ring"></div>
                <div ref="cultivationBarChart" class="performance-bar"></div>
              </div>
            </div>
          </div>

          <div class="analysis-card">
            <div class="analysis-header">
              <span class="analysis-title">亩均分析（万元/亩）</span>
              <div class="analysis-subtabs">
                <span
                  :class="{ active: muJunSubtab === 'revenue' }"
                  @click="muJunSubtab = 'revenue'"
                >产值分析</span>
                <span
                  :class="{ active: muJunSubtab === 'tax' }"
                  @click="muJunSubtab = 'tax'"
                >税收分析</span>
              </div>
            </div>
            <div ref="muJunChart" class="analysis-chart"></div>
          </div>

          <div class="analysis-card">
            <div class="analysis-header">
              <span class="analysis-title">各区县园区绩效分档统计</span>
            </div>
            <div ref="districtGradeChart" class="analysis-chart"></div>
          </div>
        </div>
      </template>
    </div>

    <div class="footer">
      <div
        class="footer-tab"
        :class="{ active: activeTab === 'overview' }"
        @click="switchTab('overview')"
      >
        <span class="tab-text">全市园区概况</span>
      </div>
      <div
        class="footer-tab"
        :class="{ active: activeTab === 'analysis' }"
        @click="switchTab('analysis')"
      >
        <span class="tab-text">园区评价分析</span>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import hangzhouMap from '@/../public/hangzhou-map.json'
import '@/styles/big-screen.css'
import { getBigScreenStats, getDistrictData, getEvaluationAnalysis } from '@/api/dashboard-big'

export default {
  name: 'BigScreen',
  data() {
    return {
      year: new Date().getFullYear(),
      stats: {},
      districtData: [],
      evaluationAnalysis: {},
      hoveredDistrict: null,
      hoveredDistrictData: null,
      hoveredCard: null,
      currentTime: '',
      mapChart: null,
      parkTypeChart: null,
      techRingChart: null,
      cultivationBarChart: null,
      muJunChart: null,
      districtGradeChart: null,
      timer: null,
      districtsConfig: [],
      activeTab: 'overview',
      muJunSubtab: 'revenue'
    }
  },
  computed: {
    // 左侧统计卡片：园区维度（共7项）
    leftStats() {
      return [
        { label: '园区总数', value: this.formatNumber(this.stats.parkTotal), unit: '处' },
        { label: '生产制造类园区', value: this.formatNumber(this.stats.manufacturingCount), unit: '家' },
        { label: '生产服务类园区', value: this.formatNumber(this.stats.serviceCount), unit: '家' },
        { label: '四星级园区数', value: this.formatNumber(this.stats.fourStarCount), unit: '家' },
        { label: '五星级园区数', value: this.formatNumber(this.stats.fiveStarCount), unit: '家' },
        { label: '实际用地数', value: this.formatNumber(this.stats.landArea), unit: '亩' },
        { label: '已建建筑面积', value: this.formatNumber(this.stats.buildArea), unit: '㎡' }
      ]
    },
    // 右侧统计卡片：企业/员工/亩均维度（共7项）
    rightStats() {
      return [
        { label: '入驻企业总数', value: this.formatNumber(this.stats.enterpriseTotal), unit: '家' },
        { label: '参评企业总数', value: this.formatNumber(this.stats.participateEnterpriseCount), unit: '家' },
        { label: '入驻企业员工总数', value: this.formatNumber(this.stats.employeeTotal), unit: '人' },
        { label: '国家专精特新小巨人', value: this.formatNumber(this.stats.nationalSpecializedCount), unit: '家' },
        { label: '省专精特新中小企业', value: this.formatNumber(this.stats.provincialSpecializedCount), unit: '家' },
        { label: '创新型中小企业', value: this.formatNumber(this.stats.innovativeSmeCount), unit: '家' },
        { label: '全市平均亩均产值', value: this.formatNumber(this.stats.revenuePerMu), unit: '万元/亩' },
        { label: '全市平均亩均税收', value: this.formatNumber(this.stats.taxPerMu), unit: '万元/亩' }
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
  watch: {
    activeTab(newTab) {
      if (newTab === 'analysis') {
        this.$nextTick(() => {
          setTimeout(() => {
            this.initAnalysisCharts()
          }, 200)
        })
      }
    },
    muJunSubtab() {
      this.$nextTick(() => {
        this.initMuJunChart()
      })
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.timer && clearInterval(this.timer)
    this.mapChart && this.mapChart.dispose()
    this.parkTypeChart && this.parkTypeChart.dispose()
    this.techRingChart && this.techRingChart.dispose()
    this.cultivationBarChart && this.cultivationBarChart.dispose()
    this.muJunChart && this.muJunChart.dispose()
    this.districtGradeChart && this.districtGradeChart.dispose()
  },
  methods: {
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
    // 数字格式化：超过万以"万"为单位，保留2位小数
    formatNumber(num) {
      if (num === null || num === undefined || num === '') return 0
      const n = Number(num)
      if (isNaN(n)) return 0
      // 面积类小数保留2位
      if (!Number.isInteger(n)) {
        return n.toLocaleString('en-US', { maximumFractionDigits: 2 })
      }
      return n.toLocaleString('en-US')
    },
    async loadData() {
      try {
        const [statsRes, districtRes, analysisRes] = await Promise.all([
          getBigScreenStats(this.year),
          getDistrictData(this.year),
          getEvaluationAnalysis(this.year)
        ])
        if (statsRes.code === 200) this.stats = statsRes.data || {}
        if (districtRes.code === 200) this.districtData = districtRes.data || []
        if (analysisRes.code === 200) this.evaluationAnalysis = analysisRes.data || {}
        await this.$nextTick()
        setTimeout(() => {
          this.$nextTick(() => {
            this.initMapChart()
          })
        }, 300)
      } catch (e) {
        console.error('数据大屏加载数据失败:', e)
      }
    },
    switchTab(tab) {
      this.activeTab = tab
    },
    initAnalysisCharts() {
      this.initParkTypeChart()
      this.initTechRingChart()
      this.initCultivationBarChart()
      this.initMuJunChart()
      this.initDistrictGradeChart()
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
      ]

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
      this.mapChart && this.mapChart.resize()
      this.calculateMarkerPositions()
    },
    // 计算3D地图上各区县标签位置（与CSS rotateX(35deg)保持一致）
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

      // 将后端返回的区县数据转为以 name 为 key 的映射
      const dataMap = {}
      this.districtData.forEach(d => { dataMap[d.name] = d })

      this.districtData = this.districtsConfig.map(d => {
        // 取后端真实数据，未匹配则用0
        const real = dataMap[d.name] || {}
        const baseData = {
          ...d,
          name: d.name,
          _screenX: 0,
          _screenY: 0,
          _beamScreenLen: 50,
          _labelOffsetX: (d.labelOffset && d.labelOffset[0]) || 0,
          parkCount: real.parkCount || 0,
          manufacturingCount: real.manufacturingCount || 0,
          serviceCount: real.serviceCount || 0,
          employeeCount: real.employeeCount || 0,
          enterpriseCount: real.enterpriseCount || 0,
          participateEnterpriseCount: real.participateEnterpriseCount || 0,
          nationalSpecializedCount: real.nationalSpecializedCount || 0,
          provincialSpecializedCount: real.provincialSpecializedCount || 0,
          innovativeSmeCount: real.innovativeSmeCount || 0
        }
        try {
          const px = this.mapChart.convertToPixel({ geoIndex: 0 }, d.position)
          if (!px) return baseData
          const cx = px[0]
          const cy = px[1]
          const localX = cx - W / 2
          const localY = cy - H / 2
          const scaledY = SCALE * localY
          const z = scaledY * sinT
          const factor = PERSPECTIVE / (PERSPECTIVE - z)
          const projectedX = SCALE * localX * factor
          const projectedY = scaledY * cosT * factor
          const screenX = projectedX + W / 2
          const screenY = projectedY + H / 2
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
    },
    initParkTypeChart() {
      const dom = this.$refs.parkTypeChart
      if (!dom) return
      if (this.parkTypeChart) this.parkTypeChart.dispose()
      this.parkTypeChart = echarts.init(dom)
      const data = this.evaluationAnalysis.parkTypeYearList || []
      const years = data.map(d => d.year)
      const serviceData = data.map(d => d.serviceCount || 0)
      const mfgData = data.map(d => d.manufacturingCount || 0)
      const totalData = data.map(d => d.totalCount || 0)

      this.parkTypeChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: {
          data: ['服务', '制造', '总数'],
          textStyle: { color: '#a0aec0' },
          bottom: 0
        },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          data: years,
          axisLine: { lineStyle: { color: '#2d3748' } },
          axisLabel: { color: '#a0aec0' }
        },
        yAxis: [
          { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0' } },
          { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0' } }
        ],
        series: [
          { name: '服务', type: 'bar', data: serviceData, itemStyle: { color: '#22d3ee' } },
          { name: '制造', type: 'bar', data: mfgData, itemStyle: { color: '#4ade80' } },
          { name: '总数', type: 'line', yAxisIndex: 1, data: totalData, smooth: true, lineStyle: { color: '#fbbf24', width: 3 }, itemStyle: { color: '#fbbf24' } }
        ]
      })
    },
    initTechRingChart() {
      const dom = this.$refs.techRingChart
      if (!dom) return
      if (this.techRingChart) this.techRingChart.dispose()
      this.techRingChart = echarts.init(dom)
      const stats = this.evaluationAnalysis.techEnterpriseStats || {}
      const total = (stats.techEnterpriseCount || 0) + (stats.highTechCount || 0) + (stats.zhejiangTechCount || 0) + (stats.excellentProductCount || 0)

      this.techRingChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 6, borderColor: '#030a16', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#fff' } },
          data: [
            { value: stats.techEnterpriseCount || 0, name: '科技型企业', itemStyle: { color: '#22d3ee' } },
            { value: stats.highTechCount || 0, name: '高企', itemStyle: { color: '#4ade80' } },
            { value: stats.zhejiangTechCount || 0, name: '省科技型', itemStyle: { color: '#fbbf24' } },
            { value: stats.excellentProductCount || 0, name: '优秀产品', itemStyle: { color: '#f472b6' } }
          ]
        }],
        graphic: [{
          type: 'text', left: 'center', top: 'center',
          style: { text: `${total}`, fontSize: 28, fontWeight: 'bold', fill: '#fff' },
          z: 10
        }, {
          type: 'text', left: 'center', top: '60%',
          style: { text: '科技型企业', fontSize: 12, fill: '#a0aec0' },
          z: 10
        }]
      })
    },
    initCultivationBarChart() {
      const dom = this.$refs.cultivationBarChart
      if (!dom) return
      if (this.cultivationBarChart) this.cultivationBarChart.dispose()
      this.cultivationBarChart = echarts.init(dom)
      const items = ['线上', '规上', '高新', '专精特新', '国家专精特新', '单项冠军', '上市企业', '创新型中小企业']
      const newData = items.map(() => Math.floor(Math.random() * 30))
      const totalData = items.map(() => Math.floor(Math.random() * 80))

      this.cultivationBarChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: {
          data: ['新增', '总数'],
          textStyle: { color: '#a0aec0', fontSize: 10 },
          bottom: 0
        },
        grid: { left: '3%', right: '4%', bottom: '20%', top: '5%', containLabel: true },
        xAxis: {
          type: 'category',
          data: items,
          axisLine: { lineStyle: { color: '#2d3748' } },
          axisLabel: { color: '#a0aec0', fontSize: 9, rotate: 45 }
        },
        yAxis: { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0', fontSize: 10 } },
        series: [
          { name: '新增', type: 'bar', data: newData, itemStyle: { color: '#4ade80' }, barWidth: '35%' },
          { name: '总数', type: 'bar', data: totalData, itemStyle: { color: '#22d3ee' }, barWidth: '35%' }
        ]
      })
    },
    initMuJunChart() {
      const dom = this.$refs.muJunChart
      if (!dom) return
      if (this.muJunChart) this.muJunChart.dispose()
      this.muJunChart = echarts.init(dom)
      const data = this.evaluationAnalysis.muJunYearList || []
      const years = data.map(d => d.year)
      const serviceData = data.map(d => Number(d.serviceMuJun) || 0)
      const avgData = data.map(d => Number(d.averageMuJun) || 0)
      const mfgData = data.map(d => Number(d.manufacturingMuJun) || 0)

      this.muJunChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis' },
        legend: {
          data: ['生产性服务类', '平均值', '生产制造类'],
          textStyle: { color: '#a0aec0' },
          bottom: 0
        },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          data: years,
          axisLine: { lineStyle: { color: '#2d3748' } },
          axisLabel: { color: '#a0aec0' }
        },
        yAxis: { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0' } },
        series: [
          { name: '生产性服务类', type: 'line', data: serviceData, smooth: true, lineStyle: { color: '#22d3ee', width: 3 }, itemStyle: { color: '#22d3ee' } },
          { name: '平均值', type: 'line', data: avgData, smooth: true, lineStyle: { color: '#fbbf24', width: 3 }, itemStyle: { color: '#fbbf24' } },
          { name: '生产制造类', type: 'line', data: mfgData, smooth: true, lineStyle: { color: '#a855f7', width: 3 }, itemStyle: { color: '#a855f7' } }
        ]
      })
    },
    initDistrictGradeChart() {
      const dom = this.$refs.districtGradeChart
      if (!dom) return
      if (this.districtGradeChart) this.districtGradeChart.dispose()
      this.districtGradeChart = echarts.init(dom)
      const data = this.evaluationAnalysis.districtGradeList || []
      const names = data.map(d => d.districtName)
      const gradeA = data.map(d => d.gradeACount || 0)
      const gradeB = data.map(d => d.gradeBCount || 0)
      const gradeC = data.map(d => d.gradeCCount || 0)
      const gradeD = data.map(d => d.gradeDCount || 0)
      const taxData = data.map(d => Number(d.taxPerMu) || 0)
      const revenueData = data.map(d => Number(d.revenuePerMu) || 0)

      this.districtGradeChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: {
          data: ['服务类', '制造类', 'A档', 'B档', 'C档', 'D档', '亩均税收', '亩均产出'],
          textStyle: { color: '#a0aec0', fontSize: 10 },
          bottom: 0
        },
        grid: { left: '3%', right: '4%', bottom: '25%', top: '5%', containLabel: true },
        xAxis: {
          type: 'category',
          data: names,
          axisLine: { lineStyle: { color: '#2d3748' } },
          axisLabel: { color: '#a0aec0', fontSize: 9, rotate: 45 }
        },
        yAxis: [
          { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0', fontSize: 10 } },
          { type: 'value', axisLine: { lineStyle: { color: '#2d3748' } }, axisLabel: { color: '#a0aec0', fontSize: 10 } }
        ],
        series: [
          { name: '服务类', type: 'bar', data: data.map(d => d.serviceCount || 0), itemStyle: { color: '#22d3ee' }, barWidth: '6%' },
          { name: '制造类', type: 'bar', data: data.map(d => d.manufacturingCount || 0), itemStyle: { color: '#4ade80' }, barWidth: '6%' },
          { name: 'A档', type: 'bar', data: gradeA, itemStyle: { color: '#22c55e' }, barWidth: '6%' },
          { name: 'B档', type: 'bar', data: gradeB, itemStyle: { color: '#eab308' }, barWidth: '6%' },
          { name: 'C档', type: 'bar', data: gradeC, itemStyle: { color: '#f97316' }, barWidth: '6%' },
          { name: 'D档', type: 'bar', data: gradeD, itemStyle: { color: '#ef4444' }, barWidth: '6%' },
          { name: '亩均税收', type: 'line', yAxisIndex: 1, data: taxData, smooth: true, lineStyle: { color: '#f87171', width: 2 }, itemStyle: { color: '#f87171' } },
          { name: '亩均产出', type: 'line', yAxisIndex: 1, data: revenueData, smooth: true, lineStyle: { color: '#60a5fa', width: 2 }, itemStyle: { color: '#60a5fa' } }
        ]
      })
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

.header-left { flex: 1; }

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
  box-shadow: 0 0 15px rgba(34, 211, 238, 0.2);
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
  margin-bottom: 8px;
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
}

.value-num {
  font-size: 24px;
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
  padding: 13px 30px 8px;
  border-radius: 6px 6px 0 0;
  border: 1px solid rgba(34, 211, 238, 0.6);
  border-bottom: none;
  position: relative;
  z-index: 2;
  background: rgba(34, 211, 238, 0.15);
}

.footer-tab::before {
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
  color: #22d3ee;
  font-weight: bold;
  letter-spacing: 2px;
}

.footer-tab {
  cursor: pointer;
  padding: 12px 24px;
  background: rgba(10, 30, 60, 0.5);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 6px;
  margin-right: 12px;
  transition: all 0.3s;
}

.footer-tab:hover {
  border-color: rgba(34, 211, 238, 0.5);
  background: rgba(34, 211, 238, 0.1);
}

.footer-tab.active {
  background: rgba(34, 211, 238, 0.2);
  border-color: rgba(34, 211, 238, 0.6);
}

.footer-tab.active .tab-text {
  color: #fff;
  text-shadow: 0 0 10px rgba(34, 211, 238, 0.8);
}

.analysis-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 16px;
  padding: 16px;
  min-height: 0;
}

.analysis-card {
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.7) 0%, rgba(5, 15, 30, 0.5) 100%);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.analysis-title {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
}

.analysis-subtabs {
  display: flex;
  gap: 8px;
}

.analysis-subtabs span {
  font-size: 12px;
  color: #6a8caf;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.analysis-subtabs span:hover {
  color: #22d3ee;
}

.analysis-subtabs span.active {
  color: #fff;
  background: rgba(34, 211, 238, 0.2);
  border: 1px solid rgba(34, 211, 238, 0.4);
}

.analysis-chart {
  flex: 1;
  min-height: 0;
}

.analysis-content {
  flex: 1;
  display: flex;
  min-height: 0;
}

.performance-row {
  flex: 1;
  display: flex;
  gap: 12px;
}

.performance-ring {
  width: 35%;
  min-width: 0;
}

.performance-bar {
  flex: 1;
  min-width: 0;
}
</style>
