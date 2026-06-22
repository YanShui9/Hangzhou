<template>
  <div class="enterprise-result-container page-list-flex">
    <div class="breadcrumb-bar">
      <span class="breadcrumb-link">评价结果</span>
      <i class="el-icon-arrow-right breadcrumb-sep"></i>
      <span class="breadcrumb-current">企业指标</span>
    </div>

    <!-- 搜索筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="queryParams.year" size="small" style="width: 150px">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
        </el-select>
        <el-select v-model="queryParams.parkName" placeholder="园区名称（模糊搜索）" size="small" clearable filterable style="width: 200px">
          <el-option v-for="p in parkOptions" :key="p" :label="p" :value="p" />
        </el-select>
        <el-select v-model="queryParams.region" placeholder="全部区域" size="small" clearable style="width: 150px">
          <el-option v-for="r in regionOptions" :key="r" :label="r" :value="r" />
        </el-select>
        <el-select v-model="queryParams.type" placeholder="全部类型" size="small" clearable style="width: 150px">
          <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
        </el-select>
        <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
        <el-button size="small" @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button size="small" @click="handleExport">导出</el-button>
      </div>
    </div>

    <!-- 表格标题 -->
    <div class="table-title">企业荣誉数量统计汇总表</div>
    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
      <el-table v-loading="loading" :data="list" border stripe size="mini" class="result-table">
        <el-table-column type="index" label="序号" width="70" align="center" fixed="left" />
        <el-table-column prop="parkName" label="园区名称" width="160" align="center" fixed="left" show-overflow-tooltip />
        <el-table-column prop="region" label="所属区域" width="120" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="120" align="center" />
        <el-table-column prop="totalEnterprises" label="参评企业总数" width="120" align="center" />

        <!-- 企业培育分组 -->
        <el-table-column label="企业培育" align="center">
          <el-table-column prop="existingAboveScale" label="存量规上" width="90" align="center" />
          <el-table-column prop="newAboveScale" label="新增规上" width="90" align="center" />
          <el-table-column prop="retiredAboveScale" label="退规" width="90" align="center" />
          <el-table-column prop="newSpecialtyGiant" label="新增专精特新小巨人" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newProvincialHiddenChampion" label="新增省级隐形冠军" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newSpecialtySME" label="新增专精中小企业" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newSingleChampion" label="新增单项冠军" width="120" align="center" show-overflow-tooltip />
          <el-table-column prop="newIPO" label="新增上市" width="90" align="center" />
          <el-table-column prop="newNationalHighTech" label="新增国高" width="90" align="center" />
          <el-table-column prop="innovativeSME" label="创新型中小企业" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newProvincialTechSmall" label="新增省科小" width="110" align="center" />
          <el-table-column prop="earlyInvestInnovation" label="投早投小创新" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newFirstEquipment" label="新增首台（套）装备" width="140" align="center" show-overflow-tooltip />
          <el-table-column prop="firstVersion" label="首次次" width="90" align="center" />
          <el-table-column prop="firstBatch" label="首批次" width="90" align="center" />
          <el-table-column prop="provincialExcellentIndustrial" label="省级优秀工业新品" width="150" align="center" show-overflow-tooltip />
          <el-table-column prop="zhejiangMadeQuality" label="浙江制造精品" width="130" align="center" show-overflow-tooltip />
          <el-table-column prop="newNationalRDAgency" label="新增国家级研发机构" width="150" align="center" show-overflow-tooltip />
          <el-table-column prop="newProvincialRDAgency" label="新增省级研发机构" width="150" align="center" show-overflow-tooltip />
          <el-table-column prop="newMunicipalRDAgency" label="新增市级研发机构" width="150" align="center" show-overflow-tooltip />
        </el-table-column>

        <!-- 科技创新分组 -->
        <el-table-column label="科技创新" align="center">
          <el-table-column prop="publicServicePlatform" label="务平台（科研创新/检验检测等公共服）" width="180" align="center" show-overflow-tooltip />
          <el-table-column prop="enterpriseIncubator" label="企业孵化检验检测等公共服" width="180" align="center" show-overflow-tooltip />
          <el-table-column prop="talentAClass" label="A类人才" width="90" align="center" />
          <el-table-column prop="talentBClass" label="B类人才" width="90" align="center" />
          <el-table-column prop="talentCClass" label="C类人才" width="90" align="center" />
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar">
        <span class="total-text">共 {{ total }}条</span>
        <el-select v-model="queryParams.pageSize" size="small" style="width: 110px" @change="handleSizeChange">
          <el-option v-for="s in [20, 50, 100]" :key="s" :label="s + '条/页'" :value="s" />
        </el-select>
        <div class="page-nav">
          <i class="el-icon-arrow-left" :class="['nav-btn', queryParams.pageNum === 1 ? 'disabled' : '']" @click="prevPage"></i>
          <span
            v-for="p in pageNumbers"
            :key="p"
            :class="['page-num', p === queryParams.pageNum ? 'active' : '']"
            @click="gotoPage(p)"
          >{{ p }}</span>
          <i class="el-icon-arrow-right" :class="['nav-btn', queryParams.pageNum === totalPages ? 'disabled' : '']" @click="nextPage"></i>
        </div>
        <div class="jumper">
          <span>前往</span>
          <el-input v-model.number="jumpPage" size="small" style="width: 60px; margin: 0 6px" @keyup.enter.native="handleJump" />
          <span>页</span>
        </div>
      </div>
  </div>
</template>

<script>
import { getEnterpriseHonorSummary } from '@/api/enterprise'

export default {
  name: 'AdminResultEnterprise',
  data() {
    return {
      queryParams: {
        year: 2026,
        parkName: '',
        region: '',
        type: '',
        pageNum: 1,
        pageSize: 20
      },
      yearOptions: [2026, 2025, 2024, 2023, 2022],
      parkOptions: ['传化国际科创园', '万轮科技园', '和达药谷中心', '颐高创业园', '天明国际产业园', '乐富海邦园', '银海科创中心', '杭州湾信息港', '钱湾生物港（一期）', '菜鸟智谷产业园'],
      regionOptions: ['滨江区', '萧山区', '余杭区', '西湖区', '上城区', '拱墅区'],
      typeOptions: ['服务类', '制造类', '数字经济类', '生物医药类', '新材料类'],
      list: [],
      total: 0,
      loading: false,
      jumpPage: 1
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.queryParams.pageSize) || 1
    },
    pageNumbers() {
      const total = this.totalPages
      const current = this.queryParams.pageNum
      const pages = []
      const start = Math.max(1, Math.min(current - 2, total - 5))
      const end = Math.min(total, start + 5)
      for (let i = start; i <= end; i++) pages.push(i)
      return pages
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getEnterpriseHonorSummary(this.queryParams)
        .then(res => {
          const records = res.data && res.data.records ? res.data.records : null
          this.list = records && records.length ? records : this.buildMockRows()
          this.total = (res.data && res.data.total) || 104
        })
        .catch(() => {
          this.list = this.buildMockRows()
          this.total = 104
        })
        .finally(() => {
          this.loading = false
          this.jumpPage = this.queryParams.pageNum
        })
    },
    buildMockRows() {
      const parkNames = ['传化国际科创园', '万轮科技园', '和达药谷中心', '颐高创业园', '天明国际产业园', '乐富海邦园', '银海科创中心', '杭州湾信息港', '钱湾生物港（一期）']
      const regions = ['滨江区', '萧山区', '余杭区', '西湖区', '上城区']
      const parkTypes = ['服务类', '制造类', '数字经济类', '生物医药类']
      const rows = []
      for (let i = 0; i < this.queryParams.pageSize; i++) {
        const idx = i % parkNames.length
        rows.push({
          id: i + 1 + (this.queryParams.pageNum - 1) * this.queryParams.pageSize,
          parkName: parkNames[idx],
          region: regions[idx % regions.length],
          parkType: parkTypes[idx % parkTypes.length],
          totalEnterprises: 50,
          existingAboveScale: 2,
          newAboveScale: 2,
          retiredAboveScale: 2,
          newSpecialtyGiant: 2,
          newProvincialHiddenChampion: 2,
          newSpecialtySME: 2,
          newSingleChampion: 2,
          newIPO: 2,
          newNationalHighTech: 2,
          innovativeSME: 2,
          newProvincialTechSmall: 2,
          earlyInvestInnovation: 2,
          newFirstEquipment: 2,
          firstVersion: 2,
          firstBatch: 2,
          provincialExcellentIndustrial: 2,
          zhejiangMadeQuality: 2,
          newNationalRDAgency: 2,
          newProvincialRDAgency: 2,
          newMunicipalRDAgency: 2,
          publicServicePlatform: 2,
          enterpriseIncubator: 2,
          talentAClass: 2,
          talentBClass: 2,
          talentCClass: 2
        })
      }
      return rows
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { year: 2026, parkName: '', region: '', type: '', pageNum: 1, pageSize: 20 }
      this.getList()
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },
    prevPage() {
      if (this.queryParams.pageNum > 1) {
        this.queryParams.pageNum--
        this.getList()
      }
    },
    nextPage() {
      if (this.queryParams.pageNum < this.totalPages) {
        this.queryParams.pageNum++
        this.getList()
      }
    },
    gotoPage(p) {
      this.queryParams.pageNum = p
      this.getList()
    },
    handleJump() {
      const p = parseInt(this.jumpPage)
      if (p >= 1 && p <= this.totalPages) {
        this.queryParams.pageNum = p
        this.getList()
      }
    }
  }
}
</script>

<style scoped>
.enterprise-result-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  height: 100%;
  overflow: hidden;
}

.breadcrumb-bar {
  background: #FFFFFF;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6B7280;
  flex-shrink: 0;
}

.breadcrumb-link {
  cursor: pointer;
  color: #6B7280;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #C0C4CC;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 500;
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
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.table-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  padding: 8px 0 12px 0;
  flex-shrink: 0;
}

.result-table >>> .el-table__header th {
  background: #FFFFFF !important;
  color: #303133;
  font-weight: 600;
  font-size: 13px;
}

.result-table >>> .el-table__header .el-table__cell.is-leaf {
  background: #FFFFFF !important;
}

.result-table >>> .el-table__body td {
  font-size: 13px;
  color: #606266;
}

.result-table >>> .el-table__row--striped td {
  background: #FAFAFA;
}

.result-table >>> .el-table__row:hover > td {
  background: #F0F6FF !important;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 16px 0 4px 0;
  gap: 12px;
  font-size: 13px;
  color: #606266;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

.page-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-btn {
  cursor: pointer;
  padding: 6px 8px;
  color: #606266;
}

.nav-btn.disabled {
  color: #C0C4CC;
  cursor: not-allowed;
}

.page-num {
  min-width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  cursor: pointer;
  color: #606266;
  border-radius: 3px;
  font-size: 13px;
}

.page-num.active {
  background: #1E40AF;
  color: #FFFFFF;
  font-weight: 500;
}

.jumper {
  display: flex;
  align-items: center;
}
</style>