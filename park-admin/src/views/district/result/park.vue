<template>
  <div class="park-evaluation-container">
    <!-- 搜索筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input v-model="queryParams.parkName" placeholder="园区名称" clearable size="small" style="width: 200px" @keyup.enter.native="handleQuery" />
        <el-select v-model="queryParams.districtName" placeholder="全部区域" clearable size="small" style="width: 130px">
          <el-option v-for="item in districtOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="queryParams.year" placeholder="年度" clearable size="small" style="width: 110px">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-select v-model="queryParams.grade" placeholder="绩效分档" clearable size="small" style="width: 110px">
          <el-option label="A档" value="A" />
          <el-option label="B档" value="B" />
          <el-option label="C档" value="C" />
          <el-option label="D档" value="D" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button size="small" icon="el-icon-upload2" @click="handleExport">导出</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="list"
      border
      stripe
      size="mini"
      style="width: 100%"
      class="evaluation-table"
      :header-cell-style="headerCellStyle"
    >
      <el-table-column type="index" label="排名" width="70" align="center" />
      <el-table-column prop="parkName" label="园区名称" min-width="200" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span class="link-name" @click="handleViewDetail(row)">{{ row.parkName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="districtName" label="所属区域" width="110" align="center" />
      <el-table-column prop="parkType" label="园区类型" width="130" align="center" />
      <el-table-column prop="enterpriseCount" label="企业总数" width="100" align="right" />
      <el-table-column prop="aboveScaleCount" label="规上企业" width="100" align="right" />
      <el-table-column prop="employeeCount" label="员工人数" width="100" align="right" />
      <el-table-column prop="landArea" label="用地面积(亩)" width="130" align="right" />
      <el-table-column prop="totalRevenue" label="营业收入(万元)" width="150" align="right" />
      <el-table-column prop="totalTax" label="上缴税收(万元)" width="150" align="right" />
      <el-table-column prop="totalScore" label="综合得分" width="110" align="center">
        <template slot-scope="{ row }">
          <span class="score-value">{{ row.totalScore || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="绩效分档" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.grade" :type="getGradeTagType(row.grade)" size="mini" effect="plain">
            {{ row.grade }}档
          </el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="评价状态" width="110" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.status" :type="getStatusTagType(row.status)" size="mini" effect="plain">
            {{ row.status }}
          </el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" align="center" />
    </el-table>

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

    <!-- 详情弹窗 -->
    <el-dialog
      title="园区评价详情"
      :visible.sync="detailVisible"
      width="820px"
      append-to-body
      class="enterprise-dialog"
    >
      <div v-if="currentRow" class="enterprise-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="园区名称" :span="2">{{ currentRow.parkName }}</el-descriptions-item>
          <el-descriptions-item label="所属区域">{{ currentRow.districtName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="园区类型">{{ currentRow.parkType || '--' }}</el-descriptions-item>
          <el-descriptions-item label="企业总数">{{ currentRow.enterpriseCount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="规上企业">{{ currentRow.aboveScaleCount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="员工人数">{{ currentRow.employeeCount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用地面积(亩)">{{ currentRow.landArea || '--' }}</el-descriptions-item>
          <el-descriptions-item label="营业收入(万元)">{{ currentRow.totalRevenue || '--' }}</el-descriptions-item>
          <el-descriptions-item label="上缴税收(万元)">{{ currentRow.totalTax || '--' }}</el-descriptions-item>
          <el-descriptions-item label="亩均产出(万元/亩)">{{ currentRow.outputPerMu || '--' }}</el-descriptions-item>
          <el-descriptions-item label="亩均税收(万元/亩)">{{ currentRow.taxPerMu || '--' }}</el-descriptions-item>
          <el-descriptions-item label="综合得分">
            <span class="score-value">{{ currentRow.totalScore || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="绩效分档">
            <el-tag v-if="currentRow.grade" :type="getGradeTagType(currentRow.grade)" size="mini" effect="plain">
              {{ currentRow.grade }}档
            </el-tag>
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="评价状态">
            <el-tag v-if="currentRow.status" :type="getStatusTagType(currentRow.status)" size="mini" effect="plain">
              {{ currentRow.status }}
            </el-tag>
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentRow.updateTime || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getParkEvaluationList, exportParkEvaluationList } from '@/api/evaluation'

export default {
  name: 'DistrictResultPark',
  data() {
    return {
      queryParams: {
        parkName: '',
        districtName: '',
        year: new Date().getFullYear(),
        grade: '',
        pageNum: 1,
        pageSize: 20
      },
      districtOptions: [
        '上城区', '拱墅区', '西湖区', '滨江区', '萧山区',
        '余杭区', '临平区', '钱塘区', '富阳区', '临安区',
        '桐庐县', '淳安县', '建德市'
      ],
      yearOptions: this.$options.methods.buildYearOptions(),
      list: [],
      total: 0,
      loading: false,
      detailVisible: false,
      currentRow: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    buildYearOptions() {
      const current = new Date().getFullYear()
      const list = []
      for (let i = 0; i < 6; i++) list.push(current - i)
      return list
    },
    headerCellStyle() {
      return { background: '#F5F7FA', color: '#303133', fontWeight: '600', fontSize: '13px' }
    },
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        parkName: this.queryParams.parkName || undefined,
        districtName: this.queryParams.districtName || undefined,
        grade: this.queryParams.grade || undefined
      }
      getParkEvaluationList(params)
        .then(res => {
          this.list = this.buildMockRows(res.data.records || [])
          this.total = res.data.total || 0
        })
        .catch(() => {
          this.list = []
          this.total = 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    buildMockRows(records) {
      if (records && records.length) return records
      const parks = [
        { name: '万轮科技园', district: '滨江区', type: '生产性服务' },
        { name: '传化国际科创园', district: '萧山区', type: '生产性服务' },
        { name: '和达药谷中心', district: '钱塘区', type: '生物医药' },
        { name: '颐高创业园', district: '西湖区', type: '电子信息' },
        { name: '天明国际产业园', district: '萧山区', type: '高端装备' },
        { name: '乐富海邦园', district: '余杭区', type: '新材料' },
        { name: '银海科创中心', district: '钱塘区', type: '新能源' },
        { name: '杭州湾信息港', district: '萧山区', type: '电子信息' }
      ]
      const grades = ['A', 'B', 'C', 'D']
      const statuses = ['已完成', '已完成', '已完成', '审核中', '待审核']
      const rows = []
      for (let i = 0; i < 16; i++) {
        const p = parks[i % parks.length]
        const totalRevenue = (80000 + i * 2300 + Math.floor(Math.random() * 8000)).toFixed(2)
        const landArea = (200 + (i % 10) * 20 + Math.random() * 30).toFixed(2)
        const totalTax = (parseFloat(totalRevenue) * (0.06 + Math.random() * 0.02)).toFixed(2)
        const totalScore = (75 + (i % 25) + Math.random() * 5).toFixed(1)
        const gradeIndex = totalScore >= 90 ? 0 : totalScore >= 75 ? 1 : totalScore >= 60 ? 2 : 3
        rows.push({
          id: i + 1,
          parkName: p.name,
          districtName: p.district,
          parkType: p.type,
          enterpriseCount: 80 + i * 3,
          aboveScaleCount: 15 + (i % 20),
          employeeCount: 2000 + i * 120,
          landArea,
          totalRevenue,
          totalTax,
          outputPerMu: (parseFloat(totalRevenue) / parseFloat(landArea)).toFixed(2),
          taxPerMu: (parseFloat(totalTax) / parseFloat(landArea)).toFixed(2),
          totalScore,
          grade: grades[gradeIndex],
          status: statuses[i % statuses.length],
          updateTime: new Date().getFullYear() + '-' + String((i % 12) + 1).padStart(2, '0') + '-15 10:30:00'
        })
      }
      return rows
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        parkName: '',
        districtName: '',
        year: new Date().getFullYear(),
        grade: '',
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    handleExport() {
      exportParkEvaluationList(this.queryParams).then(() => {
        this.$message.success('导出任务已提交，请稍候')
      }).catch(() => {
        this.$message.info('导出功能需要后端接口支持')
      })
    },
    handleViewDetail(row) {
      this.currentRow = row
      this.detailVisible = true
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    getGradeTagType(grade) {
      const map = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }
      return map[grade] || 'info'
    },
    getStatusTagType(status) {
      const map = { '已完成': 'success', '审核中': 'warning', '待审核': 'info', '驳回': 'danger' }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped>
.park-evaluation-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 56px);
}
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
.filter-left, .filter-right {
  display: flex; align-items: center; flex-wrap: wrap; gap: 8px;
}
.evaluation-table { background: #FFFFFF; border-radius: 4px; }
.evaluation-table >>> .el-table__header th {
  background: #F5F7FA !important; color: #303133; font-weight: 600; font-size: 13px;
}
.evaluation-table >>> .el-table__body td { font-size: 13px; color: #606266; }
.evaluation-table >>> .el-table__row--striped td { background: #FAFAFA; }
.evaluation-table >>> .el-table__row:hover > td { background: #F0F6FF !important; }
.link-name { color: #409EFF; cursor: pointer; font-weight: 500; }
.link-name:hover { text-decoration: underline; }
.score-value { font-size: 14px; font-weight: 700; color: #1E40AF; }
.text-muted { color: #C0C4CC; }
.pagination-bar {
  display: flex; justify-content: flex-end; align-items: center;
  padding: 12px 16px 4px; background: #FFFFFF; border-top: 1px solid #EBEEF5; gap: 12px;
}
.total-text { font-size: 13px; color: #606266; }
.enterprise-dialog >>> .el-dialog__header { border-bottom: 1px solid #E4E7ED; padding: 14px 20px; }
.enterprise-dialog >>> .el-dialog__title { font-size: 15px; font-weight: 600; color: #303133; }
.enterprise-dialog >>> .el-dialog__body { padding: 16px 20px; }
.enterprise-dialog >>> .el-dialog__footer { border-top: 1px solid #E4E7ED; padding: 10px 20px; }
.enterprise-detail >>> .el-descriptions__label {
  font-weight: 500; background: #FAFAFA; color: #606266; font-size: 13px;
}
.enterprise-detail >>> .el-descriptions__content { color: #303133; font-size: 13px; }
</style>
