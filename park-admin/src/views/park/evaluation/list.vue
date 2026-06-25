<template>
  <div class="evaluation-container">
    <div class="page-header">
      <div class="header-left">
        <div class="tab-bar">
          <div
            v-for="tab in tabs"
            :key="tab.name"
            :class="['tab-item', { 'tab-item--active': activeTab === tab.name }]"
            @click="handleTabChange(tab)"
          >{{ tab.label }}</div>
        </div>
        <el-select
          v-model="yearFilter"
          placeholder="选择年份"
          style="width: 120px; margin-left: 16px;"
          clearable
          @change="handleYearChange"
        >
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
      </div>
      <el-button class="add-btn" icon="el-icon-plus" plain @click="handleAddClick">新增评价</el-button>
    </div>

    <div class="table-scroll-container">
      <div class="table-card">
        <el-table :data="filteredList" stripe style="width: 100%;" v-loading="loading">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column label="园区名称" width="160" align="center">
            <template slot-scope="scope">{{ parkInfo.parkName || '-' }}</template>
          </el-table-column>
          <el-table-column label="所属区域" width="120" align="center">
            <template slot-scope="scope">{{ parkInfo.districtName || '-' }}</template>
          </el-table-column>
          <el-table-column label="评价年度" width="120" align="center">
            <template slot-scope="scope">{{ scope.row.year || '-' }}</template>
          </el-table-column>
          <el-table-column label="园区类型" width="140" align="center">
            <template slot-scope="scope">{{ getParkTypeLabel() }}</template>
          </el-table-column>
          <el-table-column label="参评状态" width="100" align="center">
            <template slot-scope="scope">
              <span class="status-indicator">
                <span :class="['status-dot', 'status-dot--' + getParticipateDotClass(scope.row)]"></span>
                <span class="status-text">{{ getParticipateLabel(scope.row) }}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="审核状态" width="140" align="center">
            <template slot-scope="scope">
              <span class="status-indicator">
                <span :class="['status-dot', 'status-dot--' + getAuditDotClass(scope.row.status)]"></span>
                <span class="status-text">{{ getAuditLabel(scope.row.status) }}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            :page-size="query.pageSize"
            :current-page="query.pageNum"
            :page-sizes="[10, 20, 50]"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </div>

    <div v-if="addDialogVisible" class="add-overlay">
      <div class="add-overlay-content">
        <div class="add-breadcrumb">
          <span class="breadcrumb-item" @click="addDialogVisible = false">评价列表</span>
          <span class="breadcrumb-separator">&gt;</span>
          <span class="breadcrumb-current">新增</span>
        </div>

        <div class="add-layout">
          <div class="category-sidebar">
            <div
              v-for="category in categories"
              :key="category.id"
              :class="['category-item', { 'category-item--active': activeCategory === category.id }]"
              @click="handleCategoryClick(category)"
            >
              <span class="category-index">{{ category.id }}.</span>
              <span class="category-name">{{ category.name }}</span>
              <i class="el-icon-arrow-right category-arrow"></i>
            </div>
          </div>

          <div class="category-content">
            <div v-if="activeCategory" class="content-header">
              <span class="content-title">{{ getCategoryName(activeCategory) }}</span>
            </div>
            <div class="content-body">
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog title="评价详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价年份">{{ detail.year }}年</el-descriptions-item>
        <el-descriptions-item label="绩效分档">{{ detail.grade || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价总分">{{ detail.totalScore || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <span class="status-indicator">
            <span :class="['status-dot', 'status-dot--' + getAuditDotClass(detail.status)]"></span>
            <span class="status-text">{{ getAuditLabel(detail.status) }}</span>
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="驳回类别" :span="2">{{ detail.rejectCategory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核历史" :visible.sync="historyDialogVisible" width="600px">
      <el-table :data="auditHistory" border stripe>
        <el-table-column prop="auditorName" label="审核人" width="100" align="center" />
        <el-table-column prop="auditorRole" label="审核级别" width="100" align="center">
          <template slot-scope="scope">
            {{ scope.row.auditorRole === 1 ? '市级终审' : '区县初审' }}
          </template>
        </el-table-column>
        <el-table-column prop="action" label="审核结果" width="100" align="center">
          <template slot-scope="scope">
            <span class="status-indicator">
              <span :class="['status-dot', scope.row.action === 1 ? 'status-dot--green' : 'status-dot--red']"></span>
              <span class="status-text">{{ scope.row.action === 1 ? '通过' : '驳回' }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="opinion" label="审核意见" min-width="200" />
        <el-table-column prop="createTime" label="审核时间" width="180" align="center" />
      </el-table>
      <div slot="footer">
        <el-button @click="historyDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择评价年度" :visible.sync="yearSelectVisible" width="360px" :close-on-click-modal="false">
      <div class="year-select-content">
        <div class="year-select-label">请选择评价年度</div>
        <el-radio-group v-model="selectedYear" class="year-radio-group">
          <el-radio v-for="y in yearOptions" :key="y" :label="y">{{ y }}年</el-radio>
        </el-radio-group>
      </div>
      <div slot="footer">
        <el-button @click="yearSelectVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmYearSelect" :disabled="!selectedYear">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEvaluationPage, submitEvaluation, getEvaluationById, checkSubmittedEvaluation } from '@/api/evaluation'
import { getAuditHistory } from '@/api/audit'
import { getParkDetail } from '@/api/park'
import { mapGetters } from 'vuex'

export default {
  name: 'ParkEvaluationList',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      activeTab: 'all',
      tabs: [
        { label: '全部', name: 'all' },
        { label: '待审核', name: 'pending' },
        { label: '已通过', name: 'passed' },
        { label: '已驳回', name: 'rejected' }
      ],
      yearOptions: [currentYear, currentYear - 1, currentYear - 2],
      yearFilter: null,
      parkInfo: {},
      loading: false,
      list: [],
      total: 0,
      query: {
        pageNum: 1,
        pageSize: 10
      },
      addDialogVisible: false,
      activeCategory: null,
      categories: [
        { id: 1, name: '基础指标' },
        { id: 2, name: '产业发展' },
        { id: 3, name: '企业培育' },
        { id: 4, name: '科技创新' },
        { id: 5, name: '服务能力' },
        { id: 6, name: '效益产出' },
        { id: 7, name: '安全生产' },
        { id: 8, name: '其他' }
      ],
      detailDialogVisible: false,
      detail: {},
      historyDialogVisible: false,
      auditHistory: [],
      yearSelectVisible: false,
      selectedYear: null,
      checkLoading: false
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    filteredList() {
      if (this.activeTab === 'all') return this.list
      const statusList = this.getTabStatusList()
      if (!statusList) return this.list
      return this.list.filter(item => statusList.includes(item.status))
    }
  },
  created() {
    this.fetchParkInfo()
    this.fetchList()
  },
  methods: {
    async fetchParkInfo() {
      try {
        const res = await getParkDetail(this.userInfo.parkId)
        this.parkInfo = res.data || {}
      } catch (e) {
        console.error('获取园区信息失败', e)
      }
    },

    async fetchList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize,
          parkId: this.userInfo.parkId
        }
        if (this.yearFilter) {
          params.year = this.yearFilter
        }
        const statusValue = this.getTabStatusValue()
        if (statusValue !== null) {
          params.status = statusValue
        }
        const res = await getEvaluationPage(params)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('获取评价列表失败', e)
      } finally {
        this.loading = false
      }
    },

    handleTabChange(tab) {
      this.activeTab = tab.name
      this.query.pageNum = 1
      this.fetchList()
    },

    handleYearChange() {
      this.query.pageNum = 1
      this.fetchList()
    },

    getTabStatusValue() {
      switch (this.activeTab) {
        case 'passed': return 3
        case 'rejected': return 4
        default: return null
      }
    },

    getTabStatusList() {
      switch (this.activeTab) {
        case 'pending': return [0, 1, 2]
        case 'passed': return [3]
        case 'rejected': return [4]
        default: return null
      }
    },

    getParticipateDotClass(row) {
      const performance = this.parkInfo.performance || ''
      if (performance === 'A' || performance === 'B' || performance === 'C' || performance === 'D') {
        return 'green'
      }
      return 'gray'
    },

    getParticipateLabel(row) {
      const performance = this.parkInfo.performance || ''
      if (performance === 'A' || performance === 'B' || performance === 'C' || performance === 'D') {
        return '已参评'
      }
      return '未参评'
    },

    getAuditDotClass(status) {
      if (status === 0) return 'orange'
      if (status === 1) return 'orange'
      if (status === 2) return 'blue'
      if (status === 3) return 'green'
      return 'red'
    },

    getAuditLabel(status) {
      if (status === 0) return '未提交'
      if (status === 1) return '区县待审核'
      if (status === 2) return '市级待审核'
      if (status === 3) return '审核通过'
      return '退回'
    },

    getParkTypeLabel() {
      const type = this.parkInfo.parkType
      if (!type) return '-'
      if (type.includes('制造')) return '生产性制造类'
      if (type.includes('服务')) return '生产性服务类'
      return type
    },

    handleAddClick() {
      this.selectedYear = new Date().getFullYear()
      this.yearSelectVisible = true
    },

    async confirmYearSelect() {
      if (!this.selectedYear) return
      this.yearSelectVisible = false
      this.checkLoading = true
      try {
        const res = await checkSubmittedEvaluation({
          parkId: this.userInfo.parkId,
          year: this.selectedYear
        })
        if (res.data && res.data.submitted) {
          this.$message.warning('每年只允许提交一次评价材料，请勿重复提交！')
          return
        }
        this.$router.push(`/park/evaluation/add?year=${this.selectedYear}`)
      } catch (e) {
        console.error('检查评价状态失败', e)
        this.$message.error('检查评价状态失败，请稍后重试')
      } finally {
        this.checkLoading = false
      }
    },

    handleEdit(row) {
      this.$router.push(`/park/evaluation/add?id=${row.id}&year=${row.year}`)
    },

    handleCategoryClick(category) {
      this.activeCategory = category.id
    },

    getCategoryName(id) {
      const cat = this.categories.find(c => c.id === id)
      return cat ? cat.name : ''
    },

    async handleSubmit(row) {
      this.$confirm('确定要提交该评价记录吗？提交后将进入审核流程。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          await submitEvaluation(row.id)
          this.$message.success('提交成功')
          this.fetchList()
        } catch (e) {
          console.error('提交失败', e)
        }
      }).catch(() => {})
    },

    async handleView(row) {
      if (row.status === 0) {
        this.$router.push(`/park/evaluation/add?id=${row.id}&year=${row.year}`)
      } else {
        this.$router.push(`/park/evaluation/add?id=${row.id}&year=${row.year}&view=1`)
      }
    },

    async viewAuditHistory(row) {
      try {
        const res = await getAuditHistory(row.id)
        this.auditHistory = res.data || []
        this.historyDialogVisible = true
      } catch (e) {
        console.error('获取审核历史失败', e)
      }
    },

    handlePageChange(page) {
      this.query.pageNum = page
      this.fetchList()
    },
    handleSizeChange(size) {
      this.query.pageSize = size
      this.query.pageNum = 1
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.evaluation-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: #f5f7fa;
  height: calc(100vh - 84px);
  position: relative;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}

.table-scroll-container {
  flex: 1;
  overflow-y: auto;
}

.header-left {
  display: flex;
  align-items: center;
}

.tab-bar {
  display: flex;
}

.tab-item {
  padding: 8px 24px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;
  user-select: none;
}

.tab-item:hover {
  color: #409EFF;
}

.tab-item--active {
  color: #409EFF;
  font-weight: 500;
  border-bottom-color: #409EFF;
}

.add-btn {
  border-radius: 20px !important;
}

.table-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.status-indicator {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot--green  { background-color: #67C23A; }
.status-dot--red    { background-color: #F56C6C; }
.status-dot--orange { background-color: #E6A23C; }
.status-dot--blue   { background-color: #409EFF; }
.status-dot--gray   { background-color: #909399; }

.status-text {
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 15px 0 0 0;
}

.add-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f7fa;
  z-index: 10;
  overflow-y: auto;
}

.add-overlay-content {
  padding: 24px 32px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.add-breadcrumb {
  font-size: 14px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEEF5;
  flex-shrink: 0;
}

.breadcrumb-item {
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-item:hover {
  color: #409EFF;
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #C0C4CC;
}

.breadcrumb-current {
  color: #303133;
  font-weight: 500;
}

.add-layout {
  display: flex;
  flex: 1;
  min-height: 0;
  gap: 0;
}

.category-sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow-y: auto;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background-color: #F5F7FA;
}

.category-item--active {
  background-color: #ECF5FF;
  border-left-color: #409EFF;
}

.category-item--active .category-name {
  color: #409EFF;
  font-weight: 500;
}

.category-item--active .category-index {
  color: #409EFF;
}

.category-index {
  font-size: 14px;
  color: #909399;
  min-width: 28px;
}

.category-name {
  font-size: 14px;
  color: #303133;
  flex: 1;
}

.category-arrow {
  font-size: 12px;
  color: #C0C4CC;
  transition: color 0.2s, transform 0.2s;
}

.category-item:hover .category-arrow {
  color: #409EFF;
  transform: translateX(4px);
}

.category-item--active .category-arrow {
  color: #409EFF;
}

.category-content {
  flex: 1;
  margin-left: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.content-header {
  padding: 18px 24px;
  border-bottom: 1px solid #EBEEF5;
  flex-shrink: 0;
}

.content-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.content-body {
  flex: 1;
  padding: 24px;
  min-height: 300px;
}

.year-select-content {
  padding: 20px 0;
}

.year-select-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.year-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
