<template>
  <div class="enterprise-list-container page-list-flex">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">入驻企业</h2>
    </div>

    <!-- 搜索过滤区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input
          v-model="queryParams.enterpriseName"
          placeholder="企业名称/统一信用代码"
          clearable
          size="small"
          class="filter-item"
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
        <el-select
          v-model="queryParams.districtName"
          placeholder="所属区域"
          clearable
          size="small"
          class="filter-item"
          style="width: 130px"
        >
          <el-option
            v-for="item in districtOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select
          v-model="queryParams.parkId"
          placeholder="所属园区"
          clearable
          size="small"
          class="filter-item"
          style="width: 160px"
        >
          <el-option
            v-for="item in parkOptions"
            :key="item.id"
            :label="item.parkName"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.enterpriseHonor"
          placeholder="企业荣誉"
          clearable
          size="small"
          class="filter-item"
          style="width: 160px"
        >
          <el-option
            v-for="item in honorOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="queryParams.isParticipate"
          placeholder="参评状态"
          clearable
          size="small"
          class="filter-item"
          style="width: 120px"
        >
          <el-option label="参评" :value="1" />
          <el-option label="不参评" :value="0" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-dropdown size="small" type="primary" @command="handleExport">
          <el-button type="primary" size="small">
            <i class="el-icon-upload2" style="margin-right:4px"></i>导出数据<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="participate">导出参评企业</el-dropdown-item>
            <el-dropdown-item command="all">导出所有企业</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
    <el-table
      v-loading="loading"
      :data="enterpriseList"
      border
      stripe
      size="mini"
      class="enterprise-table"
      :header-cell-style="headerCellStyle"
      :row-style="rowStyle"
    >
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="enterpriseName" label="企业名称" min-width="200" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span class="enterprise-name-link" @click="handleViewDetail(row)">{{ row.enterpriseName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="creditCode" label="统一信用代码" width="180" align="center" show-overflow-tooltip />
      <el-table-column prop="districtName" label="所属区域" width="90" align="center" show-overflow-tooltip />
      <el-table-column prop="parkName" label="所属园区" min-width="150" show-overflow-tooltip />
      <el-table-column prop="enterpriseHonor" label="企业荣誉" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span>{{ row.enterpriseHonor || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="企业状态" width="90" align="center">
        <template slot-scope="{ row }">
          <span
            v-if="row.isParticipate !== undefined && row.isParticipate !== null"
            class="status-text"
            :class="getParticipateClass(row.isParticipate)"
          >
            <span class="status-dot"></span>
            {{ row.isParticipate === 1 ? '参评' : '不参评' }}
          </span>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column label="登记状态" width="90" align="center">
        <template slot-scope="{ row }">
          <el-tag
            v-if="row.status"
            :type="getStatusType(row.status)"
            size="mini"
            effect="plain"
          >
            {{ row.status }}
          </el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="legalPerson" label="法定代表人" width="100" align="center" />
      <el-table-column prop="contactName" label="联系人" width="90" align="center" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" align="center" show-overflow-tooltip />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span>{{ row.remark || row.participateReason || '--' }}</span>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 条</span>
      <el-pagination
        :current-page.sync="queryParams.pageNum"
        :page-sizes="[20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="sizes, prev, pager, next, jumper"
        background
        small
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 查看详情对话框 -->
    <el-dialog
      title="企业详情"
      :visible.sync="detailVisible"
      width="760px"
      append-to-body
      class="enterprise-dialog"
    >
      <div v-if="currentEnterprise" class="enterprise-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="企业名称" :span="2">{{ currentEnterprise.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="统一社会信用代码">{{ currentEnterprise.creditCode }}</el-descriptions-item>
          <el-descriptions-item label="所属园区">{{ currentEnterprise.parkName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="所属区域">{{ currentEnterprise.districtName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ currentEnterprise.industryName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ currentEnterprise.legalPerson || '--' }}</el-descriptions-item>
          <el-descriptions-item label="经营状态">{{ currentEnterprise.status || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentEnterprise.contactName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentEnterprise.contactPhone || '--' }}</el-descriptions-item>
          <el-descriptions-item label="注册资本（万元）">{{ currentEnterprise.registeredCapital || '--' }}</el-descriptions-item>
          <el-descriptions-item label="注册日期">{{ currentEnterprise.registerDate || '--' }}</el-descriptions-item>
          <el-descriptions-item label="企业荣誉" :span="2">{{ currentEnterprise.enterpriseHonor || '--' }}</el-descriptions-item>
          <el-descriptions-item label="是否参评">
            <span v-if="currentEnterprise.isParticipate !== undefined && currentEnterprise.isParticipate !== null">
              {{ currentEnterprise.isParticipate === 1 ? '参评' : '不参评' }}
            </span>
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="备注/原因" :span="2">{{ currentEnterprise.remark || currentEnterprise.participateReason || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEnterpriseList, getEnterpriseDetail, exportEnterpriseList } from '@/api/enterprise'
import { getParkList } from '@/api/park'

export default {
  name: 'AdminEnterpriseList',
  data() {
    return {
      // 查询参数
      queryParams: {
        enterpriseName: '',
        creditCode: '',
        districtName: '',
        parkId: null,
        enterpriseHonor: '',
        isParticipate: null,
        pageNum: 1,
        pageSize: 20
      },
      // 区县选项（杭州市辖区县）
      districtOptions: [
        '上城区', '拱墅区', '西湖区', '滨江区', '萧山区',
        '余杭区', '临平区', '钱塘区', '富阳区', '临安区',
        '桐庐县', '淳安县', '建德市'
      ],
      // 园区选项（从接口获取）
      parkOptions: [],
      // 企业荣誉选项
      honorOptions: [
        { label: '专精特新小巨人', value: '专精特新小巨人' },
        { label: '省级隐形冠军', value: '省级隐形冠军' },
        { label: '省专精特新中小企业', value: '省专精特新中小企业' },
        { label: '单项冠军', value: '单项冠军' },
        { label: '国高/国家高新技术企业', value: '国高/国家高新技术企业' },
        { label: '创新型中小企业', value: '创新型中小企业' },
        { label: '省科技型中小企业', value: '省科技型中小企业' }
      ],
      // 表格数据
      enterpriseList: [],
      total: 0,
      loading: false,
      // 详情对话框
      detailVisible: false,
      currentEnterprise: null
    }
  },
  created() {
    this.getList()
    this.getParkOptions()
  },
  methods: {
    /** 表头样式 */
    headerCellStyle() {
      return {
        background: '#F5F7FA',
        color: '#303133',
        fontWeight: '600',
        fontSize: '13px'
      }
    },
    /** 行样式 - hover */
    rowStyle() {
      return { fontSize: '13px', color: '#606266' }
    },
    /** 获取园区下拉选项 */
    getParkOptions() {
      getParkList({ pageNum: 1, pageSize: 1000 })
        .then(res => {
          this.parkOptions = res.data.records || []
        })
        .catch(() => {
          this.parkOptions = []
        })
    },
    /** 获取企业列表 */
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        enterpriseName: this.queryParams.enterpriseName || undefined,
        creditCode: this.queryParams.enterpriseName || undefined,
        districtName: this.queryParams.districtName || undefined,
        parkId: this.queryParams.parkId || undefined,
        enterpriseHonor: this.queryParams.enterpriseHonor || undefined,
        isParticipate: this.queryParams.isParticipate !== null && this.queryParams.isParticipate !== undefined ? this.queryParams.isParticipate : undefined
      }
      getEnterpriseList(params)
        .then(res => {
          this.enterpriseList = res.data.records || []
          this.total = res.data.total || 0
        })
        .catch(() => {
          this.enterpriseList = []
          this.total = 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    /** 查询 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置 */
    resetQuery() {
      this.queryParams = {
        enterpriseName: '',
        creditCode: '',
        districtName: '',
        parkId: null,
        enterpriseHonor: '',
        isParticipate: null,
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    /**
     * 导出数据
     * @param type 导出类型：participate=参评企业, all=所有企业
     */
    handleExport(type) {
      let confirmText = ''
      let filename = ''
      let exportParams = { ...this.queryParams }

      if (type === 'participate') {
        confirmText = '确认导出当前查询条件下的参评企业吗？'
        filename = '参评企业列表'
        exportParams.isParticipate = 1
      } else if (type === 'all') {
        confirmText = '确认导出当前查询条件下的所有企业吗？'
        filename = '入驻企业列表'
      }

      this.$confirm(confirmText, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        const params = {
          ...exportParams,
          enterpriseName: exportParams.enterpriseName || undefined,
          creditCode: exportParams.enterpriseName || undefined,
          districtName: exportParams.districtName || undefined,
          parkId: exportParams.parkId || undefined,
          enterpriseHonor: exportParams.enterpriseHonor || undefined,
          isParticipate: exportParams.isParticipate !== null && exportParams.isParticipate !== undefined ? exportParams.isParticipate : undefined
        }
        exportEnterpriseList(params).then(blob => {
          const url = window.URL.createObjectURL(new Blob([blob]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', `${filename}_${this.formatDate(new Date())}.xlsx`)
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        }).catch(() => {
          this.$message.error('导出失败，请稍后重试')
        })
      }).catch(() => {})
    },
    /** 查看详情 - 跳转到企业详情页 */
    handleViewDetail(row) {
      if (!row || !row.id) {
        this.$message.warning('企业信息不完整')
        return
      }
      this.$router.push(`/admin/enterprise/detail/${row.id}`)
    },
    isMockRow(row) {
      return row && typeof row.id === 'number' && row.creditCode && row.creditCode.includes('*') === false && row.creditCode.endsWith('M')
    },
    /** 分页大小 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 页码 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    /** 企业状态（是否参评）样式 */
    getParticipateClass(val) {
      if (val === 1) return 'status-success'
      if (val === 0) return 'status-warning'
      return 'status-default'
    },
    /** 经营状态 tag 类型 */
    getStatusType(status) {
      if (!status) return 'info'
      if (status.includes('在业') || status.includes('存续')) return 'success'
      if (status.includes('迁出') || status.includes('停业') || status.includes('歇业')) return 'warning'
      if (status.includes('注销') || status.includes('吊销') || status.includes('撤销') || status.includes('除名') || status.includes('关闭')) return 'danger'
      return 'info'
    },
    /** 日期格式化 */
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}${m}${d}`
    }
  }
}
</script>

<style scoped>
.enterprise-list-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  height: 100%;
  overflow: hidden;
}

/* 页面标题 */
.page-header {
  margin-bottom: 14px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 搜索过滤区 - 紧凑横向布局 */
.filter-bar {
  background: #FFFFFF;
  padding: 14px 16px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-item {
  margin-right: 0 !important;
}

/* 数据表格 */
.enterprise-table {
  background: #FFFFFF;
  border-radius: 4px;
}

.enterprise-table >>> .el-table__header th {
  background: #F5F7FA !important;
  color: #303133;
  font-weight: 600;
  font-size: 13px;
}

.enterprise-table >>> .el-table__body td {
  font-size: 13px;
  color: #606266;
}

.enterprise-table >>> .el-table__row--striped td {
  background: #FAFAFA;
}

.enterprise-table >>> .el-table__row:hover > td {
  background: #F0F6FF !important;
}

/* 企业名称链接样式 */
.enterprise-name-link {
  color: #409EFF;
  cursor: pointer;
  font-weight: 500;
}

.enterprise-name-link:hover {
  text-decoration: underline;
}

/* 状态显示 - 带圆点 */
.status-text {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
}

.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  background: #909399;
}

.status-success {
  color: #67C23A;
}
.status-success .status-dot {
  background: #67C23A;
  box-shadow: 0 0 4px rgba(103, 194, 58, 0.5);
}

.status-warning {
  color: #E6A23C;
}
.status-warning .status-dot {
  background: #E6A23C;
  box-shadow: 0 0 4px rgba(230, 162, 60, 0.5);
}

.status-info {
  color: #409EFF;
}
.status-info .status-dot {
  background: #409EFF;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.5);
}

.status-default {
  color: #909399;
}

.text-muted {
  color: #C0C4CC;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 12px 16px 4px;
  background: #FFFFFF;
  margin-top: 0;
  border-top: 1px solid #EBEEF5;
  gap: 12px;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

/* 对话框 */
.enterprise-dialog >>> .el-dialog__header {
  border-bottom: 1px solid #E4E7ED;
  padding: 14px 20px;
}

.enterprise-dialog >>> .el-dialog__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.enterprise-dialog >>> .el-dialog__body {
  padding: 16px 20px;
}

.enterprise-dialog >>> .el-dialog__footer {
  border-top: 1px solid #E4E7ED;
  padding: 10px 20px;
}

/* 详情页 */
.enterprise-detail >>> .el-descriptions__label {
  font-weight: 500;
  background: #FAFAFA;
  color: #606266;
  font-size: 13px;
}

.enterprise-detail >>> .el-descriptions__content {
  color: #303133;
  font-size: 13px;
}
</style>
