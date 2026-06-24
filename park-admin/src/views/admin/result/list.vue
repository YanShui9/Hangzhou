<template>
  <div class="park-result-container page-list-flex">
    <!-- 筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select
          v-model="queryParams.year"
          placeholder="选择年度"
          size="small"
          style="width: 120px"
        >
          <el-option
            v-for="y in yearOptions"
            :key="y"
            :label="y + '年度'"
            :value="y"
          />
        </el-select>
        <el-input
          v-model="queryParams.parkName"
          placeholder="园区名称（模糊搜索）"
          clearable
          size="small"
          style="width: 170px"
        />
        <el-select
          v-model="queryParams.region"
          placeholder="全部区域"
          clearable
          size="small"
          style="width: 130px"
        >
          <el-option
            v-for="item in regionOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select
          v-model="queryParams.type"
          placeholder="全部类型"
          clearable
          size="small"
          style="width: 130px"
        >
          <el-option
            v-for="item in typeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
        <el-button size="small" @click="handleReset">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button type="primary" size="small" @click="handleGradeEvaluate">绩效评定</el-button>
        <el-button size="small" @click="handleExport">导出</el-button>
        <div class="view-toggle">
          <button
            class="toggle-btn"
            :class="{ active: viewMode === 'detail' }"
            @click="switchView('detail')"
          >详版</button>
          <button
            class="toggle-btn"
            :class="{ active: viewMode === 'simple' }"
            @click="switchView('simple')"
          >简版</button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <div class="table-title">园区评价汇总表（{{ viewMode === 'detail' ? '详版' : '简版' }}）</div>

      <div class="table-flex-wrapper">
      <!-- 简版表格 -->
      <el-table
        v-if="viewMode === 'simple'"
        :key="'simple'"
        v-loading="loading"
        :data="simpleList"
        border
        stripe
        size="mini"
        class="result-table simple-table"
        :header-cell-style="headerCellStyle"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="parkName" label="园区名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="revenuePerMu" label="亩均营收/万元" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.revenuePerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taxPerMu" label="亩均税收/万元" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.taxPerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="industryDevScore" label="产业发展" width="110" align="center" />
        <el-table-column prop="enterpriseCultivateScore" label="企业培育" width="110" align="center" />
        <el-table-column prop="techInnovationScore" label="科技创新" width="110" align="center" />
        <el-table-column prop="serviceCapabilityScore" label="服务能力" width="110" align="center" />
        <el-table-column prop="benefitOutputScore" label="效益产出" width="110" align="center" />
        <el-table-column prop="safetyProductionScore" label="安全生产" width="110" align="center" />
        <el-table-column prop="otherScore" label="其他" width="110" align="center" />
        <el-table-column prop="totalScore" label="总得分" width="110" align="right">
          <template slot-scope="{ row }">
            <span class="total-score-value">{{ row.totalScore || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="绩效分档" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag
              v-if="row.grade"
              :type="getGradeTagType(row.grade)"
              size="mini"
              effect="plain"
            >{{ row.grade }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 详版表格 -->
      <el-table
        v-else
        :key="'detail'"
        v-loading="loading"
        :data="detailList"
        border
        stripe
        size="mini"
        class="result-table"
        :header-cell-style="headerCellStyle"
      >
        <!-- 左侧固定列 -->
        <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
        <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip fixed="left" />
        <el-table-column prop="districtName" label="所属区域" width="90" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="90" align="center" />
        <el-table-column prop="enterpriseTotal" label="参评企业总数" width="100" align="center" />
        <el-table-column prop="leadingIndustry" label="主导产业" width="130" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <span class="industry-text">{{ row.leadingIndustry || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="revenuePerMu" label="亩均营收" width="90" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.revenuePerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taxPerMu" label="亩均税收" width="90" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.taxPerMu || '-' }}</span>
          </template>
        </el-table-column>

        <!-- 产业发展 -->
        <el-table-column label="产业发展" align="center">
          <el-table-column prop="industryDev_1" label="①主导产业产值占比" width="130" align="center" />
          <el-table-column prop="industryDev_2" label="②入驻企业数占比" width="130" align="center" />
          <el-table-column prop="industryDev_3" label="③企业类型产值占比" width="130" align="center" />
          <el-table-column prop="industryDev_4" label="指标4" width="80" align="center" />
          <el-table-column prop="industryDev_5" label="指标5" width="80" align="center" />
          <el-table-column prop="industryDev_6" label="指标6" width="80" align="center" />
          <el-table-column prop="industryDev_7" label="指标7" width="80" align="center" />
          <el-table-column prop="industryDev_8" label="指标8" width="80" align="center" />
          <el-table-column prop="industryDev_9" label="指标9" width="80" align="center" />
          <el-table-column prop="industryDev_10" label="指标10" width="80" align="center" />
          <el-table-column prop="industryDev_11" label="指标11" width="80" align="center" />
          <el-table-column prop="industryDev_12" label="指标12" width="80" align="center" />
          <el-table-column prop="industryDev_13" label="指标13" width="80" align="center" />
          <el-table-column prop="industryDev_14" label="指标14" width="80" align="center" />
          <el-table-column prop="industryDev_total" label="产业发展合计" width="90" align="center" />
        </el-table-column>

        <!-- 企业培育 -->
        <el-table-column label="企业培育" align="center">
          <el-table-column prop="entCultivate_1" label="①存量规上企业" width="110" align="center" />
          <el-table-column prop="entCultivate_2" label="②新增规上企业" width="110" align="center" />
          <el-table-column prop="entCultivate_3" label="③管理端打分" width="100" align="center" />
          <el-table-column prop="entCultivate_4" label="指标4" width="80" align="center" />
          <el-table-column prop="entCultivate_5" label="指标5" width="80" align="center" />
          <el-table-column prop="entCultivate_6" label="指标6" width="80" align="center" />
          <el-table-column prop="entCultivate_7" label="指标7" width="80" align="center" />
          <el-table-column prop="entCultivate_8" label="指标8" width="80" align="center" />
          <el-table-column prop="entCultivate_9" label="指标9" width="80" align="center" />
          <el-table-column prop="entCultivate_10" label="指标10" width="80" align="center" />
          <el-table-column prop="entCultivate_11" label="指标11" width="80" align="center" />
          <el-table-column prop="entCultivate_12" label="指标12" width="80" align="center" />
          <el-table-column prop="entCultivate_13" label="指标13" width="80" align="center" />
          <el-table-column prop="entCultivate_14" label="指标14" width="80" align="center" />
          <el-table-column prop="entCultivate_15" label="指标15" width="80" align="center" />
          <el-table-column prop="entCultivate_16" label="指标16" width="80" align="center" />
          <el-table-column prop="entCultivate_17" label="指标17" width="80" align="center" />
          <el-table-column prop="entCultivate_18" label="指标18" width="80" align="center" />
          <el-table-column prop="entCultivate_19" label="指标19" width="80" align="center" />
          <el-table-column prop="entCultivate_total" label="企业培育合计" width="90" align="center" />
        </el-table-column>

        <!-- 科技创新 -->
        <el-table-column label="科技创新" align="center">
          <el-table-column prop="techInnovation_1" label="①国家级研发机构" width="110" align="center" />
          <el-table-column prop="techInnovation_2" label="②省级研发机构" width="110" align="center" />
          <el-table-column prop="techInnovation_3" label="③市级研发机构" width="110" align="center" />
          <el-table-column prop="techInnovation_4" label="④管理端打分" width="100" align="center" />
          <el-table-column prop="techInnovation_5" label="⑤管理端打分" width="100" align="center" />
          <el-table-column prop="techInnovation_6" label="指标6" width="80" align="center" />
          <el-table-column prop="techInnovation_7" label="指标7" width="80" align="center" />
          <el-table-column prop="techInnovation_8" label="指标8" width="80" align="center" />
          <el-table-column prop="techInnovation_9" label="指标9" width="80" align="center" />
          <el-table-column prop="techInnovation_10" label="指标10" width="80" align="center" />
          <el-table-column prop="techInnovation_11" label="指标11" width="80" align="center" />
          <el-table-column prop="techInnovation_12" label="指标12" width="80" align="center" />
          <el-table-column prop="techInnovation_13" label="指标13" width="80" align="center" />
          <el-table-column prop="techInnovation_14" label="指标14" width="80" align="center" />
          <el-table-column prop="techInnovation_15" label="指标15" width="80" align="center" />
          <el-table-column prop="techInnovation_16" label="指标16" width="80" align="center" />
          <el-table-column prop="techInnovation_17" label="指标17" width="80" align="center" />
          <el-table-column prop="techInnovation_18" label="指标18" width="80" align="center" />
          <el-table-column prop="techInnovation_19" label="指标19" width="80" align="center" />
          <el-table-column prop="techInnovation_total" label="科技创新合计" width="90" align="center" />
        </el-table-column>

        <!-- 服务能力 -->
        <el-table-column label="服务能力" align="center">
          <el-table-column prop="serviceCap_1" label="①管理端打分" width="100" align="center" />
          <el-table-column prop="serviceCap_2" label="②管理端打分" width="100" align="center" />
          <el-table-column prop="serviceCap_3" label="③管理端打分" width="100" align="center" />
          <el-table-column prop="serviceCap_4" label="④管理端打分" width="100" align="center" />
          <el-table-column prop="serviceCap_5" label="⑤管理端打分" width="100" align="center" />
          <el-table-column prop="serviceCap_6" label="指标6" width="80" align="center" />
          <el-table-column prop="serviceCap_7" label="指标7" width="80" align="center" />
          <el-table-column prop="serviceCap_total" label="服务能力合计" width="100" align="center" />
        </el-table-column>

        <!-- 效益产出 -->
        <el-table-column label="效益产出" align="center">
          <el-table-column prop="benefitOutput_1" label="①管理员打分" width="100" align="center" />
          <el-table-column prop="benefitOutput_2" label="②管理员打分" width="100" align="center" />
          <el-table-column prop="benefitOutput_3" label="③管理员打分" width="100" align="center" />
          <el-table-column prop="benefitOutput_4" label="④亩均税收系数" width="110" align="center" />
          <el-table-column prop="benefitOutput_5" label="⑤亩均产出系数" width="110" align="center" />
          <el-table-column prop="benefitOutput_6" label="⑥管理员打分" width="100" align="center" />
          <el-table-column prop="benefitOutput_7" label="指标7" width="80" align="center" />
          <el-table-column prop="benefitOutput_8" label="指标8" width="80" align="center" />
          <el-table-column prop="benefitOutput_9" label="指标9" width="80" align="center" />
          <el-table-column prop="benefitOutput_total" label="效益产出合计" width="100" align="center" />
        </el-table-column>

        <!-- 安全生产 -->
        <el-table-column label="安全生产" align="center">
          <el-table-column prop="safetyProd_1" label="①未落实通则" width="100" align="center" />
          <el-table-column prop="safetyProd_2" label="②未签责任书" width="100" align="center" />
          <el-table-column prop="safetyProd_3" label="③未落实培训" width="100" align="center" />
          <el-table-column prop="safetyProd_4" label="④消防设施" width="100" align="center" />
          <el-table-column prop="safetyProd_5" label="⑤被通报" width="100" align="center" />
          <el-table-column prop="safetyProd_6" label="指标6" width="80" align="center" />
          <el-table-column prop="safetyProd_7" label="指标7" width="80" align="center" />
          <el-table-column prop="safetyProd_8" label="指标8" width="80" align="center" />
          <el-table-column prop="safetyProd_9" label="指标9" width="80" align="center" />
          <el-table-column prop="safetyProd_total" label="安全生产合计" width="110" align="center" />
        </el-table-column>

        <!-- 其他 -->
        <el-table-column label="其他" align="center">
          <el-table-column prop="other_1" label="①季度数据" width="100" align="center" />
          <el-table-column prop="other_2" label="②年度数据" width="100" align="center" />
          <el-table-column prop="other_total" label="其他合计" width="90" align="center" />
        </el-table-column>

        <!-- 右侧固定列 -->
        <el-table-column prop="totalScore" label="总得分" width="80" align="right" fixed="right">
          <template slot-scope="{ row }">
            <span class="total-score-value">{{ row.totalScore || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="绩效分档" width="80" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-tag
              v-if="row.grade"
              :type="getGradeTagType(row.grade)"
              size="mini"
              effect="plain"
            >{{ row.grade }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-bar">
        <span class="total-text">共{{ total }}条</span>
        <el-select
          v-model="queryParams.pageSize"
          size="small"
          @change="handleSizeChange"
          style="width: 110px; margin: 0 10px"
        >
          <el-option
            v-for="item in [20, 50, 100]"
            :key="item"
            :label="item + '条/页'"
            :value="item"
          />
        </el-select>
        <el-pagination
          class="page-numbers"
          background
          layout="prev, pager, next"
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          :pager-count="7"
          @current-change="handleCurrentChange"
        />
        <span class="jump-text">前往</span>
        <el-input
          v-model="jumpPage"
          size="small"
          @keyup.enter.native="handleJump"
          style="width: 50px; margin: 0 6px"
        />
        <span class="jump-text">页</span>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getParkEvaluationList,
  exportParkEvaluationList,
  getParkEvaluationDetail,
  exportParkEvaluationDetail,
  performParkEvaluation
} from '@/api/evaluation'

export default {
  name: 'AdminResultPark',
  data() {
    return {
      viewMode: 'simple', // simple | detail
      queryParams: {
        year: 2026,
        parkName: '',
        region: '',
        type: '',
        pageNum: 1,
        pageSize: 20
      },
      yearOptions: this.buildYearOptions(),
      regionOptions: ['滨江区', '萧山区', '余杭区', '西湖区', '上城区', '拱墅区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
      typeOptions: ['生产性制造类', '生产性服务类'],
      simpleList: [],
      detailList: [],
      total: 0,
      loading: false,
      jumpPage: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    buildYearOptions() {
      const currentYear = new Date().getFullYear()
      const years = []
      for (let i = 0; i < 11; i++) {
        years.push(currentYear - i)
      }
      return years
    },
    headerCellStyle() {
      return {
        background: '#FFFFFF',
        color: '#303133',
        fontWeight: '600',
        fontSize: '12px'
      }
    },
    switchView(mode) {
      if (this.viewMode === mode) return
      this.viewMode = mode
      this.queryParams.pageNum = 1
      this.getList()
    },
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        parkName: this.queryParams.parkName || undefined,
        region: this.queryParams.region || undefined,
        type: this.queryParams.type || undefined
      }
      const apiFn = this.viewMode === 'simple' ? getParkEvaluationList : getParkEvaluationDetail
      apiFn(params)
        .then(res => {
          if (res && res.data && res.data.records && res.data.records.length) {
            if (this.viewMode === 'simple') {
              this.simpleList = res.data.records
            } else {
              this.detailList = res.data.records
            }
            this.total = res.data.total || res.data.records.length
          } else {
            if (this.viewMode === 'simple') {
              this.simpleList = []
            } else {
              this.detailList = []
            }
            this.total = 0
          }
        })
        .catch(() => {
          if (this.viewMode === 'simple') {
            this.simpleList = []
          } else {
            this.detailList = []
          }
          this.total = 0
        })
        .finally(() => {
          this.loading = false
          this.jumpPage = this.queryParams.pageNum
        })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.queryParams = {
        year: 2026,
        parkName: '',
        region: '',
        type: '',
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    handleGradeEvaluate() {
      this.$confirm('确定对' + this.queryParams.year + '年度的所有园区进行绩效评定吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        performParkEvaluation(this.queryParams.year)
          .then(res => {
            this.$message.success(res.msg || '绩效评定完成')
            this.getList()
          })
          .catch(err => {
            const msg = (err && err.response && err.response.data && err.response.data.msg) || '绩效评定失败'
            this.$message.error(msg)
          })
      }).catch(() => {})
    },
    handleExport() {
      const exportFn = this.viewMode === 'simple' ? exportParkEvaluationList : exportParkEvaluationDetail
      const filename = this.viewMode === 'simple'
        ? '园区评价统计简化版.xlsx'
        : '园区评价统计详细版.xlsx'
      this.loading = true
      exportFn(this.queryParams)
        .then(blob => {
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = filename
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        })
        .catch(() => {
          this.$message.error('导出失败')
        })
        .finally(() => {
          this.loading = false
        })
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
    handleJump() {
      const page = parseInt(this.jumpPage)
      const maxPage = Math.ceil(this.total / this.queryParams.pageSize) || 1
      if (!page || page < 1 || page > maxPage) {
        this.$message.warning('请输入 1-' + maxPage + ' 之间的页码')
        return
      }
      this.queryParams.pageNum = page
      this.getList()
    },
    getGradeTagType(grade) {
      const map = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }
      return map[grade] || 'info'
    }
  }
}
</script>

<style scoped>
.park-result-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  height: 100%;
  overflow: hidden;
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

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.view-toggle {
  display: inline-flex;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  overflow: hidden;
  margin-left: 4px;
}

.toggle-btn {
  padding: 7px 14px;
  border: none;
  background: #FFFFFF;
  color: #606266;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-right: 1px solid #DCDFE6;
}

.toggle-btn:last-child {
  border-right: none;
}

.toggle-btn:hover {
  color: #409EFF;
  background: #ECF5FF;
}

.toggle-btn.active {
  background: #409EFF;
  color: #FFFFFF;
}

.table-wrapper {
  background: #FFFFFF;
  border-radius: 4px;
  padding: 8px 12px 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.table-flex-wrapper {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.table-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding: 8px 0 12px;
  flex-shrink: 0;
}

.result-table >>> .el-table__header th {
  background: #FFFFFF !important;
  color: #303133;
  font-weight: 600;
  font-size: 12px;
  padding: 8px 4px;
  white-space: normal;
  word-break: break-all;
  line-height: 1.4;
}

.result-table >>> .el-table__body td {
  font-size: 12px;
  color: #606266;
}

.result-table >>> .el-table__row--striped td {
  background: #FAFAFA;
}

.result-table >>> .el-table__row:hover > td {
  background: #F0F6FF !important;
}

.industry-text {
  color: #606266;
  font-size: 12px;
}

.numeric-text {
  font-size: 12px;
  color: #606266;
}

.total-score-value {
  font-weight: 700;
  color: #1E40AF;
  font-size: 13px;
}

.text-muted {
  color: #C0C4CC;
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 0 6px;
  font-size: 13px;
  color: #606266;
  flex-shrink: 0;
  background: #FFFFFF;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

.page-numbers {
  display: inline-block;
}

.jump-text {
  font-size: 13px;
  color: #606266;
}
</style>
