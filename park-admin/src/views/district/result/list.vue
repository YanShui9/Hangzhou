<template>
  <div class="app-container">
    <!-- 顶部提示条 -->
    <div class="top-tip">
      <i class="el-icon-info"></i>
      <span>导出功能开发中</span>
    </div>

    <el-card>
      <div slot="header" class="clearfix">
        <span>园区评价汇总表（简版）</span>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 140px;" clearable>
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
        </el-select>
        <el-select v-model="query.parkName" placeholder="园区名称（模糊搜索）" style="width: 220px;" clearable>
          <el-option v-for="park in parkOptions" :key="park.id" :label="park.name" :value="park.name" />
        </el-select>
        <el-select v-model="query.parkType" placeholder="全部类型" style="width: 140px;" clearable>
          <el-option label="全部类型" value="" />
          <el-option label="制造类" value="1" />
          <el-option label="服务类" value="2" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" class="query-btn" @click="fetchList">查询</el-button>
        <el-button type="success" icon="el-icon-download" class="export-btn" @click="handleExport">导出</el-button>
      </div>

      <!-- 表格容器 -->
      <div class="table-container">
        <el-table :data="list" border stripe style="width: 100%;" v-loading="loading" max-height="500">
          <el-table-column type="index" label="序号" width="60" align="center" fixed />
          <el-table-column prop="parkName" label="园区名称" width="180" fixed />
          <el-table-column prop="muRevenue" label="亩均营收（万元）" width="140" align="right">
            <template slot-scope="scope">
              <span>{{ formatNumber(scope.row.muRevenue) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="muTax" label="亩均税收（万元）" width="140" align="right">
            <template slot-scope="scope">
              <span>{{ formatNumber(scope.row.muTax) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="industryDev" label="产业发展" width="100" align="center" />
          <el-table-column prop="enterpriseCult" label="企业培育" width="100" align="center" />
          <el-table-column prop="techInnov" label="科技创新" width="100" align="center" />
          <el-table-column prop="serviceAbility" label="服务能力" width="100" align="center" />
          <el-table-column prop="benefitOutput" label="效益产出" width="100" align="center" />
          <el-table-column prop="safetyProd" label="安全生产" width="100" align="center" />
          <el-table-column prop="other" label="其他" width="80" align="center" />
          <el-table-column prop="totalScore" label="总得分" width="100" align="center">
            <template slot-scope="scope">
              <span style="font-weight: bold; color: #1E40AF; font-size: 16px;">{{ scope.row.totalScore || '-' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <el-pagination
        style="margin-top: 15px; text-align: right;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'DistrictResultList',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      yearOptions: [currentYear, currentYear - 1, currentYear - 2],
      parkOptions: [
        { id: 1, name: '传化国际科创园' },
        { id: 2, name: '万轮科创园' },
        { id: 3, name: '杭州湾信息港' },
        { id: 4, name: '尚达药谷中心' },
        { id: 5, name: '颐高创业园' },
        { id: 6, name: '天和国际产业园' },
        { id: 7, name: '富春湾科创园' },
        { id: 8, name: '银湖科创中心' }
      ],
      loading: false,
      list: [],
      total: 0,
      query: {
        pageNum: 1,
        pageSize: 20,
        year: currentYear,
        parkName: '',
        parkType: ''
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        this.list = this.getMockData()
        this.total = 134
      } catch (e) {
        console.error('获取园区评价列表失败', e)
      } finally {
        this.loading = false
      }
    },

    getMockData() {
      return [
        {
          parkName: '万轮科创园',
          muRevenue: 122.11,
          muTax: 18.52,
          industryDev: 4.5,
          enterpriseCult: 4.5,
          techInnov: 4.5,
          serviceAbility: 4.5,
          benefitOutput: 4.5,
          safetyProd: 4.5,
          other: 4.5,
          totalScore: 31.5
        },
        {
          parkName: '传化国际科创园',
          muRevenue: 138.42,
          muTax: 21.33,
          industryDev: 4.8,
          enterpriseCult: 4.8,
          techInnov: 4.8,
          serviceAbility: 4.8,
          benefitOutput: 4.8,
          safetyProd: 4.8,
          other: 4.8,
          totalScore: 33.6
        },
        {
          parkName: '尚达药谷中心',
          muRevenue: 96.88,
          muTax: 10.20,
          industryDev: 4.4,
          enterpriseCult: 4.4,
          techInnov: 4.4,
          serviceAbility: 4.4,
          benefitOutput: 4.4,
          safetyProd: 4.4,
          other: 4.4,
          totalScore: 30.8
        },
        {
          parkName: '颐高创业园',
          muRevenue: 122.11,
          muTax: 8.96,
          industryDev: 3.6,
          enterpriseCult: 3.6,
          techInnov: 3.6,
          serviceAbility: 3.6,
          benefitOutput: 3.6,
          safetyProd: 3.6,
          other: 3.6,
          totalScore: 32.2
        },
        {
          parkName: '天和国际产业园',
          muRevenue: 108.42,
          muTax: 18.52,
          industryDev: 4.0,
          enterpriseCult: 4.0,
          techInnov: 4.0,
          serviceAbility: 4.0,
          benefitOutput: 4.0,
          safetyProd: 4.0,
          other: 4.0,
          totalScore: 28.0
        },
        {
          parkName: '富春湾科创园',
          muRevenue: 96.88,
          muTax: 11.33,
          industryDev: 4.5,
          enterpriseCult: 4.5,
          techInnov: 4.5,
          serviceAbility: 4.5,
          benefitOutput: 4.5,
          safetyProd: 4.5,
          other: 4.5,
          totalScore: 31.5
        },
        {
          parkName: '银湖科创中心',
          muRevenue: 122.11,
          muTax: 10.20,
          industryDev: 4.8,
          enterpriseCult: 4.8,
          techInnov: 4.8,
          serviceAbility: 4.8,
          benefitOutput: 4.8,
          safetyProd: 4.8,
          other: 4.8,
          totalScore: 33.6
        },
        {
          parkName: '杭州湾信息港',
          muRevenue: 138.42,
          muTax: 8.96,
          industryDev: 4.4,
          enterpriseCult: 4.4,
          techInnov: 4.4,
          serviceAbility: 4.4,
          benefitOutput: 4.4,
          safetyProd: 4.4,
          other: 4.4,
          totalScore: 30.8
        },
        {
          parkName: '杭州生物医药（一期）',
          muRevenue: 96.88,
          muTax: 18.52,
          industryDev: 3.6,
          enterpriseCult: 3.6,
          techInnov: 3.6,
          serviceAbility: 3.6,
          benefitOutput: 3.6,
          safetyProd: 3.6,
          other: 3.6,
          totalScore: 32.2
        },
        {
          parkName: '星河智谷产业园',
          muRevenue: 122.11,
          muTax: 11.33,
          industryDev: 4.0,
          enterpriseCult: 4.0,
          techInnov: 4.0,
          serviceAbility: 4.0,
          benefitOutput: 4.0,
          safetyProd: 4.0,
          other: 4.0,
          totalScore: 28.0
        }
      ]
    },

    formatNumber(value) {
      if (value === null || value === undefined) return '-'
      return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },

    handlePageChange(page) {
      this.query.pageNum = page
      this.fetchList()
    },
    handleSizeChange(size) {
      this.query.pageSize = size
      this.query.pageNum = 1
      this.fetchList()
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.top-tip {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #e8f4fd;
  border-radius: 4px;
  margin-bottom: 16px;
  color: #666;
  font-size: 14px;
}

.top-tip i {
  margin-right: 8px;
  color: #409EFF;
}

.filter-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.filter-container .el-select {
  border-radius: 4px;
}

.query-btn {
  width: 100px;
  border-radius: 4px;
  font-weight: 500;
}

.export-btn {
  width: 100px;
  border-radius: 4px;
  font-weight: 500;
  background: #67c23a;
  border-color: #67c23a;
}

.export-btn:hover {
  background: #85ce61;
  border-color: #85ce61;
}

.table-container {
  overflow-x: auto;
}

.el-table {
  font-size: 13px;
}

.el-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>