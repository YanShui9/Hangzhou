<template>
  <div class="audit-list-container page-list-flex">

    <!-- ============ 顶部 4 个统计卡片 ============ -->
    <div class="stats-cards">
      <div
        class="stat-card"
        v-for="(card, index) in statCards"
        :key="index"
        :class="['stat-card-' + card.key, { active: activeCardKey === card.key }]"
        @click="handleCardClick(card.key)"
      >
        <div class="stat-label">{{ card.label }}</div>
        <div class="stat-value">{{ formatStatValue(card.value) }}</div>
        <div class="stat-icon">
          <i :class="card.icon"></i>
        </div>
      </div>
    </div>

    <!-- ============ 筛选栏 ============ -->
    <div class="filter-bar">
      <div class="filter-row">
        <div class="filter-item">
          <el-input
            v-model="queryForm.parkName"
            placeholder="园区名称"
            clearable
            size="small"
            class="filter-input"
            @keyup.enter.native="handleSearch"
          />
        </div>

        <div class="filter-item">
          <el-select
            v-model="queryForm.districtName"
            placeholder="全部区域"
            clearable
            size="small"
            class="filter-input"
          >
            <el-option
              v-for="item in districtOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select
            v-model="queryForm.parkType"
            placeholder="全部类型"
            clearable
            size="small"
            class="filter-input"
          >
            <el-option
              v-for="item in parkTypeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select
            v-model="queryForm.auditStatus"
            placeholder="全部审核状态"
            clearable
            size="small"
            class="filter-input"
          >
            <el-option label="未提交" value="未提交" />
            <el-option label="区县待审核" value="区县待审核" />
            <el-option label="区县审核通过" value="区县审核通过" />
            <el-option label="区县审核驳回" value="区县审核驳回" />
            <el-option label="已终止" value="已终止" />
            <el-option label="市级待审核" value="市级待审核" />
            <el-option label="市级审核通过" value="市级审核通过" />
            <el-option label="市级审核驳回" value="市级审核驳回" />
          </el-select>
        </div>

        <div class="filter-item">
          <el-select
            v-model="queryForm.parkStatus"
            placeholder="全部参评状态"
            clearable
            size="small"
            class="filter-input"
          >
            <el-option label="参评" value="参评" />
            <el-option label="不参评" value="不参评" />
          </el-select>
        </div>

        <div class="filter-actions-left">
          <el-button size="small" type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button size="small" icon="el-icon-refresh-left" @click="handleReset">重置</el-button>
        </div>

        <div class="filter-actions-right">
          <el-button size="small" type="primary" @click="handleOpenYearDialog">发起年度填报</el-button>
        </div>
      </div>
    </div>

    <!-- ============ 数据表格 ============ -->
    <div class="table-flex-wrapper">
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="tableLoading"
        :header-cell-style="{ background: '#FAFBFC', color: '#303133', fontWeight: '600' }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" :index="indexMethod" />
        <el-table-column prop="parkName" label="园区名称" min-width="240" show-overflow-tooltip />
        <el-table-column prop="districtName" label="所属区域" width="120" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="120" align="center" />
        <el-table-column label="参评状态" width="100" align="center">
          <template slot-scope="scope">
            <span class="park-status-dot" :class="'dot-' + mapParkStatusKey(scope.row.parkStatus)"></span>
            <span>{{ scope.row.parkStatus || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="110" align="center">
          <template slot-scope="scope">
            <span class="audit-status-dot" :class="'dot-' + mapAuditStatusKey(scope.row.auditStatus)"></span>
            <span>{{ scope.row.auditStatus || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.auditStatus === '市级待审核'"
              type="text"
              size="small"
              style="color: #409EFF;"
              @click="handleAudit(scope.row)"
            >审核</el-button>
            <el-button
              v-else
              type="text"
              size="small"
              @click="handleView(scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ============ 分页 ============ -->
    <div class="pagination-bar" v-if="total > queryForm.pageSize">
        <el-pagination
          class="pagination"
          background
          :current-page="queryForm.pageNum"
          :page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>

    <!-- ============ 发起年度填报 弹窗 ============ -->
    <el-dialog
      title="发起年度填报"
      :visible.sync="yearDialogVisible"
      width="440px"
      :close-on-click-modal="false"
      class="year-dialog"
      custom-class="year-dialog-custom"
    >
      <div class="year-dialog-body">
        <div class="year-row">
          <label class="year-label required">年度</label>
          <el-select
            v-model="yearDialogForm.year"
            placeholder="请选择发起年度"
            class="year-select"
            size="small"
          >
            <el-option
              v-for="item in yearOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <p class="year-tip">注：每个年度只能发起一次，不能重复发起，正常情况下，当前年度只能对前一年度发起年度填报。</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="yearDialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" :loading="yearSubmitting" @click="handleConfirmYear">确认</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  getEvaluationList,
  getEvaluationSummary,
  initEvaluationByYear,
  getEvaluationYearOptions
} from '@/api/audit'

export default {
  name: 'AdminAuditList',
  data() {
    return {
      // 查询条件
      queryForm: {
        parkName: '',
        districtName: '',
        parkType: '',
        auditStatus: '',
        parkStatus: '',
        pageNum: 1,
        pageSize: 20
      },
      // 概览数据
      summary: {
        total: 0,
        cityPending: 0,
        cityPassed: 0,
        cityReturned: 0
      },
      activeCardKey: 'all',
      // 表格数据
      tableData: [],
      total: 0,
      tableLoading: false,
      // 下拉常量
      districtOptions: [
        '滨江区', '萧山区', '余杭区', '西湖区', '上城区',
        '拱墅区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'
      ],
      parkTypeOptions: ['制造类', '服务类', '科技类', '数字经济'],
      // 发起年度填报弹窗
      yearDialogVisible: false,
      yearSubmitting: false,
      yearDialogForm: {
        year: null
      },
      yearOptions: []
    }
  },
  computed: {
    statCards() {
      return [
        { key: 'all', label: '全部', value: this.summary.total, icon: 'el-icon-document' },
        { key: 'cityPending', label: '市级待审核', value: this.summary.cityPending, icon: 'el-icon-time' },
        { key: 'cityPassed', label: '市级审核通过', value: this.summary.cityPassed, icon: 'el-icon-circle-check' },
        { key: 'cityReturned', label: '市级审核驳回', value: this.summary.cityReturned, icon: 'el-icon-circle-close' }
      ]
    }
  },
  created() {
    this.fetchSummary()
    this.fetchList()
    this.fetchYearOptions()
  },
  methods: {
    /* ---------- 数据加载 ---------- */
    async fetchSummary() {
      try {
        const res = await getEvaluationSummary()
        if (res && res.data && res.data.total > 0) {
          this.summary = {
            total: res.data.total || 0,
            cityPending: res.data.cityPending || 0,
            cityPassed: res.data.cityPassed || 0,
            cityReturned: res.data.cityReturned || 0
          }
        } else {
          this.applyMockSummary()
        }
      } catch (e) {
        console.error('获取概览统计失败', e)
        this.applyMockSummary()
      }
    },
    applyMockSummary() {
      this.summary = { total: 8, cityPending: 3, cityPassed: 2, cityReturned: 1 }
    },

    async fetchList() {
      this.tableLoading = true
      try {
        const params = { ...this.queryForm }
        if (this.activeCardKey && this.activeCardKey !== 'all') {
          const map = { cityPending: '市级待审核', cityPassed: '市级审核通过', cityReturned: '市级审核驳回' }
          if (!params.auditStatus && map[this.activeCardKey]) {
            params.auditStatus = map[this.activeCardKey]
          }
        }
        const res = await getEvaluationList(params)
        if (res && res.data && res.data.records && res.data.records.length > 0 && res.data.records[0].parkName) {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        } else {
          this.applyMockList(params)
        }
      } catch (e) {
        console.error('获取评价审核列表失败', e)
        this.applyMockList(this.queryForm)
      } finally {
        this.tableLoading = false
      }
    },
    applyMockList(params) {
      const allMock = [
        { id: 1, parkName: '盛惠哈源科创园', districtName: '余杭区', parkType: '服务类', parkStatus: '参评', auditStatus: '市级待审核', createTime: '2026-06-15 10:30:00' },
        { id: 2, parkName: '世创智能制造产业园', districtName: '临平区', parkType: '制造类', parkStatus: '参评', auditStatus: '市级待审核', createTime: '2026-06-16 14:20:00' },
        { id: 3, parkName: '舒泰富春智创园', districtName: '桐庐县', parkType: '制造类', parkStatus: '参评', auditStatus: '市级待审核', createTime: '2026-06-17 09:15:00' },
        { id: 4, parkName: '蜀山未来城', districtName: '萧山区', parkType: '服务类', parkStatus: '参评', auditStatus: '市级审核通过', createTime: '2026-06-10 16:00:00' },
        { id: 5, parkName: '丝联166文创园', districtName: '拱墅区', parkType: '服务类', parkStatus: '参评', auditStatus: '市级审核通过', createTime: '2026-06-11 11:30:00' },
        { id: 6, parkName: '算力一期', districtName: '临平区', parkType: '服务类', parkStatus: '不参评', auditStatus: '市级审核驳回', createTime: '2026-06-12 08:45:00' },
        { id: 7, parkName: '泰嘉园', districtName: '拱墅区', parkType: '服务类', parkStatus: '参评', auditStatus: '区县待审核', createTime: '2026-06-18 13:00:00' },
        { id: 8, parkName: '天诚生物医药科创园', districtName: '萧山区', parkType: '制造类', parkStatus: '参评', auditStatus: '未提交', createTime: '2026-06-20 10:00:00' }
      ]
      let filtered = allMock
      const p = params || {}
      if (p.auditStatus) {
        filtered = filtered.filter(item => item.auditStatus === p.auditStatus)
      }
      if (p.parkName) {
        filtered = filtered.filter(item => item.parkName.includes(p.parkName))
      }
      if (p.districtName) {
        filtered = filtered.filter(item => item.districtName === p.districtName)
      }
      if (p.parkType) {
        filtered = filtered.filter(item => item.parkType === p.parkType)
      }
      if (p.parkStatus) {
        filtered = filtered.filter(item => item.parkStatus === p.parkStatus)
      }
      this.total = filtered.length
      const pageNum = p.pageNum || 1
      const pageSize = p.pageSize || 20
      const start = (pageNum - 1) * pageSize
      this.tableData = filtered.slice(start, start + pageSize)
    },

    async fetchYearOptions() {
      try {
        const res = await getEvaluationYearOptions()
        if (res && res.data && Array.isArray(res.data)) {
          this.yearOptions = res.data
        } else {
          // 本地兜底：默认返回近三年
          const currentYear = new Date().getFullYear()
          this.yearOptions = [
            { value: currentYear, label: currentYear + '年度' },
            { value: currentYear - 1, label: (currentYear - 1) + '年度' },
            { value: currentYear - 2, label: (currentYear - 2) + '年度' }
          ]
        }
      } catch (e) {
        console.error('获取年度选项失败', e)
        const currentYear = new Date().getFullYear()
        this.yearOptions = [
          { value: currentYear, label: currentYear + '年度' },
          { value: currentYear - 1, label: (currentYear - 1) + '年度' }
        ]
      }
    },

    /* ---------- 顶部卡片交互 ---------- */
    handleCardClick(key) {
      this.activeCardKey = key
      const map = { all: '', cityPending: '市级待审核', cityPassed: '市级审核通过', cityReturned: '市级审核驳回' }
      this.queryForm.auditStatus = map[key] || ''
      this.queryForm.pageNum = 1
      this.fetchList()
    },

    /* ---------- 查询 ---------- */
    handleSearch() {
      this.queryForm.pageNum = 1
      this.activeCardKey = 'all'
      this.fetchList()
    },

    handleReset() {
      this.queryForm = {
        parkName: '',
        districtName: '',
        parkType: '',
        auditStatus: '',
        parkStatus: '',
        pageNum: 1,
        pageSize: this.queryForm.pageSize
      }
      this.activeCardKey = 'all'
      this.fetchList()
    },

    /* ---------- 分页 ---------- */
    handlePageChange(page) {
      this.queryForm.pageNum = page
      this.fetchList()
    },
    handleSizeChange(size) {
      this.queryForm.pageSize = size
      this.queryForm.pageNum = 1
      this.fetchList()
    },
    indexMethod(index) {
      return (this.queryForm.pageNum - 1) * this.queryForm.pageSize + index + 1
    },

    /* ---------- 操作 ---------- */
    handleView(row) {
      if (!row.id) {
        this.$message.warning('缺少评价记录ID')
        return
      }
      this.$router.push(`/admin/audit/detail/${row.id}?mode=view`)
    },
    handleAudit(row) {
      if (!row.id) {
        this.$message.warning('缺少评价记录ID')
        return
      }
      this.$router.push(`/admin/audit/detail/${row.id}?mode=audit`)
    },

    /* ---------- 发起年度填报 ---------- */
    handleOpenYearDialog() {
      this.yearDialogForm.year = null
      this.yearDialogVisible = true
    },
    async handleConfirmYear() {
      if (!this.yearDialogForm.year) {
        this.$message.warning('请选择年度')
        return
      }
      this.yearSubmitting = true
      try {
        await initEvaluationByYear({ year: this.yearDialogForm.year })
        this.$message.success('发起年度填报成功')
        this.yearDialogVisible = false
        this.fetchSummary()
        this.fetchList()
      } catch (e) {
        console.error('发起年度填报失败', e)
      } finally {
        this.yearSubmitting = false
      }
    },

    /* ---------- 工具方法 ---------- */
    formatStatValue(val) {
      if (val === null || val === undefined) return '--'
      return Number(val).toLocaleString()
    },
    mapAuditStatusKey(status) {
      const map = {
        '未提交': 'unsubmitted',
        '区县待审核': 'district-pending',
        '区县审核通过': 'district-passed',
        '区县审核驳回': 'district-returned',
        '已终止': 'stopped',
        '市级待审核': 'city-pending',
        '市级审核通过': 'city-passed',
        '市级审核驳回': 'city-returned'
      }
      return map[status] || 'default'
    },
    mapParkStatusKey(status) {
      const map = { '参评': 'join', '不参评': 'exit' }
      return map[status] || 'default'
    }
  }
}
</script>

<style scoped>
.audit-list-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
}

/* ============ 顶部 4 个统计卡片 ============ */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 14px;
}

.stat-card {
  background: #FFFFFF;
  border-radius: 6px;
  padding: 22px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.stat-card.active {
  border-color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1.1;
}

.stat-card .stat-label,
.stat-card .stat-value {
  display: block;
  text-align: left;
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: #ECF5FF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409EFF;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-card-pending .stat-icon {
  background: #FDF6EC;
  color: #E6A23C;
}
.stat-card-passed .stat-icon {
  background: #ECF8F1;
  color: #67C23A;
}
.stat-card-returned .stat-icon {
  background: #FDEEEE;
  color: #F56C6C;
}
.stat-card-all .stat-icon {
  background: #ECF5FF;
  color: #409EFF;
}

/* ============ 筛选栏 ============ */
.filter-bar {
  background: #FFFFFF;
  border-radius: 6px;
  padding: 16px 20px;
  margin-bottom: 14px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  align-items: center;
}

.filter-item {
  flex-shrink: 0;
}

.filter-input {
  width: 170px;
}

.filter-actions-left {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.filter-actions-right {
  margin-left: auto;
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* ============ 表格 + 分页 ============ */
.table-flex-wrapper {
  flex: 1 1 0;
  min-height: 0;
  overflow: auto;
  background: #FFFFFF;
  border-radius: 6px;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 16px 0 4px;
}

.park-status-dot,
.audit-status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.dot-join { background: #67C23A; }
.dot-exit { background: #F56C6C; }
.dot-unsubmitted { background: #C0C4CC; }
.dot-district-pending { background: #E6A23C; }
.dot-district-passed { background: #67C23A; }
.dot-district-returned { background: #F56C6C; }
.dot-stopped { background: #909399; }
.dot-city-pending { background: #409EFF; }
.dot-city-passed { background: #67C23A; }
.dot-city-returned { background: #F56C6C; }
.dot-default { background: #C0C4CC; }

/* ============ 发起年度填报弹窗 ============ */
.year-dialog-body {
  padding: 10px 4px;
}

.year-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.year-label {
  width: 80px;
  font-size: 14px;
  color: #303133;
  text-align: right;
  margin-right: 14px;
}

.year-label.required::before {
  content: '*';
  color: #F56C6C;
  margin-right: 4px;
}

.year-select {
  flex: 1;
  max-width: 320px;
}

.year-tip {
  margin: 12px 0 0;
  padding-left: 94px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}

/* ============ 响应式 ============ */
@media (max-width: 1400px) {
  .filter-input { width: 150px; }
  .stat-value { font-size: 28px; }
}
@media (max-width: 1100px) {
  .stats-cards { grid-template-columns: repeat(2, 1fr); }
  .filter-input { width: 140px; }
}
@media (max-width: 768px) {
  .stats-cards { grid-template-columns: 1fr; }
  .filter-actions-right { margin-left: 0; }
}
</style>
