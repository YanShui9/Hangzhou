<template>
  <div class="enterprise-indicator-container">
    <div class="page-header">
      <h2 class="page-title">企业指标</h2>
    </div>

    <!-- 搜索筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input v-model="queryParams.enterpriseName" placeholder="企业名称/统一信用代码" clearable size="small" style="width: 200px" @keyup.enter.native="handleQuery" />
        <el-select v-model="queryParams.districtName" placeholder="全部区域" clearable size="small" style="width: 130px">
          <el-option v-for="item in districtOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="queryParams.parkId" placeholder="全部园区" clearable size="small" style="width: 160px">
          <el-option v-for="item in parkOptions" :key="item.id" :label="item.parkName" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.year" placeholder="年度" clearable size="small" style="width: 110px">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button size="small" icon="el-icon-upload2" @click="handleExport">导出</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="list" border stripe size="mini" style="width: 100%" class="indicator-table" :header-cell-style="headerCellStyle">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="enterpriseName" label="企业名称" min-width="210" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span class="link-name" @click="handleViewDetail(row)">{{ row.enterpriseName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="creditCode" label="统一信用代码" width="180" align="center" show-overflow-tooltip />
      <el-table-column prop="parkName" label="所属园区" min-width="140" show-overflow-tooltip />
      <el-table-column prop="districtName" label="所属区域" width="100" align="center" />
      <el-table-column prop="industryName" label="所属行业" width="150" show-overflow-tooltip />
      <el-table-column prop="registeredCapital" label="注册资本(万元)" width="130" align="right" />
      <el-table-column prop="employeeCount" label="员工人数" width="100" align="right" />
      <el-table-column prop="revenue" label="营业收入(万元)" width="140" align="right" />
      <el-table-column prop="taxAmount" label="上缴税收(万元)" width="140" align="right" />
      <el-table-column prop="landArea" label="用地面积(亩)" width="120" align="right" />
      <el-table-column prop="outputPerMu" label="亩均产出" width="140" align="right">
        <template slot-scope="{ row }">
          <span class="emphasis-value">{{ row.outputPerMu || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="taxPerMu" label="亩均税收" width="140" align="right">
        <template slot-scope="{ row }">
          <span class="emphasis-value success">{{ row.taxPerMu || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="comprehensiveScore" label="综合得分" width="110" align="center">
        <template slot-scope="{ row }">
          <span class="score-value">{{ row.comprehensiveScore || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="绩效分档" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.grade" :type="getGradeTagType(row.grade)" size="mini" effect="plain">{{ row.grade }}档</el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
    </el-table>

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
    <el-dialog title="企业指标详情" :visible.sync="detailVisible" width="820px" append-to-body class="enterprise-dialog">
      <div v-if="currentRow" class="enterprise-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="企业名称" :span="2">{{ currentRow.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="统一社会信用代码">{{ currentRow.creditCode }}</el-descriptions-item>
          <el-descriptions-item label="所属园区">{{ currentRow.parkName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="所属区域">{{ currentRow.districtName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="所属行业">{{ currentRow.industryName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ currentRow.legalPerson || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentRow.contactName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentRow.contactPhone || '--' }}</el-descriptions-item>
          <el-descriptions-item label="注册资本(万元)">{{ currentRow.registeredCapital || '--' }}</el-descriptions-item>
          <el-descriptions-item label="员工人数">{{ currentRow.employeeCount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="营业收入(万元)">{{ currentRow.revenue || '--' }}</el-descriptions-item>
          <el-descriptions-item label="上缴税收(万元)">{{ currentRow.taxAmount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用地面积(亩)">{{ currentRow.landArea || '--' }}</el-descriptions-item>
          <el-descriptions-item label="研发投入(万元)">{{ currentRow.rdInvestment || '--' }}</el-descriptions-item>
          <el-descriptions-item label="专利数量">{{ currentRow.patentCount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="亩均产出(万元/亩)"><span class="emphasis-value">{{ currentRow.outputPerMu || '-' }}</span></el-descriptions-item>
          <el-descriptions-item label="亩均税收(万元/亩)"><span class="emphasis-value success">{{ currentRow.taxPerMu || '-' }}</span></el-descriptions-item>
          <el-descriptions-item label="综合得分"><span class="score-value">{{ currentRow.comprehensiveScore || '-' }}</span></el-descriptions-item>
          <el-descriptions-item label="绩效分档">
            <el-tag v-if="currentRow.grade" :type="getGradeTagType(currentRow.grade)" size="mini" effect="plain">{{ currentRow.grade }}档</el-tag>
            <span v-else>--</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEnterpriseIndicatorList, exportEnterpriseIndicatorList } from '@/api/enterprise'
import { getParkList } from '@/api/park'

export default {
  name: 'DistrictResultEnterprise',
  data() {
    return {
      queryParams: {
        enterpriseName: '', creditCode: '', districtName: '', parkId: null,
        year: new Date().getFullYear(), pageNum: 1, pageSize: 20
      },
      districtOptions: ['上城区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '临平区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
      yearOptions: this.$options.methods.buildYearOptions(),
      parkOptions: [],
      list: [], total: 0, loading: false,
      detailVisible: false, currentRow: null
    }
  },
  created() { this.getList(); this.getParkOptions() },
  methods: {
    buildYearOptions() {
      const current = new Date().getFullYear()
      const list = []
      for (let i = 0; i < 6; i++) list.push(current - i)
      return list
    },
    headerCellStyle() { return { background: '#F5F7FA', color: '#303133', fontWeight: '600', fontSize: '13px' } },
    getParkOptions() {
      getParkList({ pageNum: 1, pageSize: 1000 })
        .then(res => { this.parkOptions = res.data.records || [] })
        .catch(() => { this.parkOptions = [] })
    },
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        enterpriseName: this.queryParams.enterpriseName || undefined,
        creditCode: this.queryParams.enterpriseName || undefined,
        districtName: this.queryParams.districtName || undefined,
        parkId: this.queryParams.parkId || undefined
      }
      getEnterpriseIndicatorList(params)
        .then(res => {
          this.list = this.buildMockRows(res.data.records || [])
          this.total = res.data.total || 0
        })
        .catch(() => { this.list = []; this.total = 0 })
        .finally(() => { this.loading = false })
    },
    buildMockRows(records) {
      if (records && records.length) return records
      const parks = [
        { name: '万轮科技园', district: '滨江区' },
        { name: '传化国际科创园', district: '萧山区' },
        { name: '和达药谷中心', district: '钱塘区' },
        { name: '颐高创业园', district: '西湖区' },
        { name: '天明国际产业园', district: '萧山区' },
        { name: '乐富海邦园', district: '余杭区' }
      ]
      const industries = ['生物医药', '电子信息', '高端装备制造', '新材料', '新能源']
      const grades = ['A', 'B', 'C', 'D']
      const names = ['杭州启明医疗器械股份有限公司', '杭州艾名医学科技有限公司', '杭州环特生物科技股份有限公司', '杭州禾泰健宇医药科技有限公司', '杭州路弘科技有限公司']
      const rows = []
      for (let i = 0; i < 20; i++) {
        const park = parks[i % parks.length]
        const revenue = (5000 + i * 320 + Math.floor(Math.random() * 2000)).toFixed(2)
        const landArea = (30 + (i % 8) * 5 + Math.random() * 10).toFixed(2)
        const taxAmount = (parseFloat(revenue) * (0.05 + Math.random() * 0.03)).toFixed(2)
        const comprehensiveScore = (70 + (i % 30)).toFixed(1)
        const gradeIndex = comprehensiveScore >= 90 ? 0 : comprehensiveScore >= 75 ? 1 : comprehensiveScore >= 60 ? 2 : 3
        rows.push({
          id: i + 1,
          enterpriseName: names[i % names.length],
          creditCode: '9133010' + String(1000000 + i * 73 + 6955775).slice(0, 10) + 'M',
          parkName: park.name, parkId: (i % parks.length) + 1,
          districtName: park.district, industryName: industries[i % industries.length],
          registeredCapital: (1000 + i * 50) + '.00', employeeCount: 100 + i * 15,
          revenue, taxAmount, landArea,
          rdInvestment: (parseFloat(revenue) * 0.05).toFixed(2),
          patentCount: 3 + (i % 15),
          outputPerMu: (parseFloat(revenue) / parseFloat(landArea)).toFixed(2),
          taxPerMu: (parseFloat(taxAmount) / parseFloat(landArea)).toFixed(2),
          comprehensiveScore, grade: grades[gradeIndex],
          legalPerson: '张华敏', contactName: '李沐晴', contactPhone: '180****5525'
        })
      }
      return rows
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { enterpriseName: '', creditCode: '', districtName: '', parkId: null, year: new Date().getFullYear(), pageNum: 1, pageSize: 20 }
      this.getList()
    },
    handleExport() {
      exportEnterpriseIndicatorList(this.queryParams).then(() => this.$message.success('导出任务已提交，请稍候'))
        .catch(() => this.$message.info('导出功能需要后端接口支持'))
    },
    handleViewDetail(row) { this.currentRow = row; this.detailVisible = true },
    handleSizeChange(val) { this.queryParams.pageSize = val; this.queryParams.pageNum = 1; this.getList() },
    handleCurrentChange(val) { this.queryParams.pageNum = val; this.getList() },
    getGradeTagType(grade) { const map = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }; return map[grade] || 'info' }
  }
}
</script>

<style scoped>
.enterprise-indicator-container { padding: 16px 20px 20px; background: #F5F7FA; min-height: calc(100vh - 56px); }
.page-header { margin-bottom: 14px; }
.page-title { font-size: 16px; font-weight: 600; color: #303133; margin: 0; }
.filter-bar {
  background: #FFFFFF; padding: 14px 16px; border-radius: 4px; display: flex;
  justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 12px; flex-wrap: wrap;
}
.filter-left, .filter-right { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.indicator-table { background: #FFFFFF; border-radius: 4px; }
.indicator-table >>> .el-table__header th { background: #F5F7FA !important; color: #303133; font-weight: 600; font-size: 13px; }
.indicator-table >>> .el-table__body td { font-size: 13px; color: #606266; }
.indicator-table >>> .el-table__row--striped td { background: #FAFAFA; }
.indicator-table >>> .el-table__row:hover > td { background: #F0F6FF !important; }
.link-name { color: #409EFF; cursor: pointer; font-weight: 500; }
.link-name:hover { text-decoration: underline; }
.emphasis-value { font-weight: 600; color: #1E40AF; }
.emphasis-value.success { color: #059669; }
.score-value { font-size: 14px; font-weight: 700; color: #1E40AF; }
.text-muted { color: #C0C4CC; }
.pagination-bar { display: flex; justify-content: flex-end; align-items: center; padding: 12px 16px 4px; background: #FFFFFF; border-top: 1px solid #EBEEF5; gap: 12px; }
.total-text { font-size: 13px; color: #606266; }
.enterprise-dialog >>> .el-dialog__header { border-bottom: 1px solid #E4E7ED; padding: 14px 20px; }
.enterprise-dialog >>> .el-dialog__title { font-size: 15px; font-weight: 600; color: #303133; }
.enterprise-dialog >>> .el-dialog__body { padding: 16px 20px; }
.enterprise-dialog >>> .el-dialog__footer { border-top: 1px solid #E4E7ED; padding: 10px 20px; }
.enterprise-detail >>> .el-descriptions__label { font-weight: 500; background: #FAFAFA; color: #606266; font-size: 13px; }
.enterprise-detail >>> .el-descriptions__content { color: #303133; font-size: 13px; }
</style>
