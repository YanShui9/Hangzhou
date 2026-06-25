<template>
  <div class="app-container">
    <el-card>
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 140px;">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
        </el-select>
        <el-input v-model="query.districtName" placeholder="园区名称（模糊搜索）" style="width: 220px;" />
        <el-select v-model="query.parkType" placeholder="全部类型" style="width: 140px;">
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
          <el-table-column prop="district" label="所属区域" width="120" align="center" fixed />
          <el-table-column prop="parkType" label="园区类型" width="100" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.parkType === '1' ? '制造类' : '服务类' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalEnterprises" label="参评企业总数（家）" width="140" align="center" />
          <!-- 企业培育指标 -->
          <el-table-column prop="aboveScale" label="规模以上（家）" width="120" align="center" />
          <el-table-column prop="highTech" label="高新技术企业（家）" width="140" align="center" />
          <el-table-column prop="techSme" label="科技型中小企业（家）" width="140" align="center" />
          <el-table-column prop="hiddenChampion" label="隐形冠军企业（家）" width="140" align="center" />
          <el-table-column prop="provincialSrti" label="省专精特新中小企业（家）" width="160" align="center" />
          <el-table-column prop="nationalSrti" label="国家专精特新小巨人（家）" width="160" align="center" />
          <el-table-column prop="innovativeSme" label="创新型中小企业（家）" width="140" align="center" />
          <!-- 科技创新指标 -->
          <el-table-column prop="patentTotal" label="专利总数（件）" width="120" align="center" />
          <el-table-column prop="patentInvention" label="发明专利（件）" width="120" align="center" />
          <el-table-column prop="patentUtility" label="实用新型专利（件）" width="140" align="center" />
          <el-table-column prop="patentDesign" label="外观设计专利（件）" width="140" align="center" />
          <el-table-column prop="rndInputRatio" label="研发投入占比（%）" width="140" align="center" />
          <el-table-column prop="newProductRevenueRatio" label="新产品销售收入占比（%）" width="160" align="center" />
          <!-- 入驻人才指标 -->
          <el-table-column prop="employeeCount" label="从业人员（人）" width="120" align="center" />
          <el-table-column prop="nationalTalent" label="国千人才（人）" width="120" align="center" />
          <el-table-column prop="provincialTalent" label="省千人才（人）" width="120" align="center" />
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
import { getEnterpriseList } from '@/api/enterprise'
export default {
  name: 'DistrictResultEnterprise',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      yearOptions: [currentYear, currentYear - 1, currentYear - 2],
      loading: false,
      list: [],
      total: 0,
      allData: [], // 存储所有数据用于筛选
      query: {
        pageNum: 1,
        pageSize: 20,
        year: currentYear,
        districtName: '',
        parkType: ''
      },
      // 当前用户所属区域（西湖区管理员）
      currentDistrict: '西湖区'
    }
  },
  created() {
    this.fetchList(false)
  },
  methods: {
    async fetchList(showSuccessMsg = true) {
      this.loading = true
      try {
        // 首先尝试从后端API获取数据
        const response = await getEnterpriseList({
          year: this.query.year,
          districtName: this.query.districtName,
          parkType: this.query.parkType,
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize
        })
        
        if (response.code === 200 && response.data) {
          // 后端返回成功，使用真实数据
          this.list = response.data.list || response.data
          this.total = response.data.total || this.list.length
          // 存储全量数据用于导出
          this.allData = this.list
          
          if (showSuccessMsg) {
            this.$message.success('查询成功')
          }
        }
      } catch (error) {
        console.error('从后端获取企业指标数据失败', error)
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },

    handleExport() {
      // 获取所有符合条件的数据（已按当前区域过滤）
      let exportData = this.allData
      
      // 根据当前筛选条件过滤数据
      if (this.query.districtName) {
        exportData = exportData.filter(item => 
          item.parkName.includes(this.query.districtName)
        )
      }
      if (this.query.parkType) {
        exportData = exportData.filter(item => 
          item.parkType === this.query.parkType
        )
      }
      
      // 构建CSV内容
      const headers = [
        '园区名称', '所属区域', '园区类型', '参评企业总数（家）',
        '规模以上（家）', '高新技术企业（家）', '科技型中小企业（家）',
        '隐形冠军企业（家）', '省专精特新中小企业（家）', '国家专精特新小巨人（家）',
        '创新型中小企业（家）', '专利总数（件）', '发明专利（件）',
        '实用新型专利（件）', '外观设计专利（件）', '研发投入占比（%）',
        '新产品销售收入占比（%）', '从业人员（人）', '国千人才（人）', '省千人才（人）'
      ]
      
      const rows = exportData.map(item => [
        item.parkName,
        item.district,
        item.parkType === '1' ? '制造类' : '服务类',
        item.totalEnterprises,
        item.aboveScale,
        item.highTech,
        item.techSme,
        item.hiddenChampion,
        item.provincialSrti,
        item.nationalSrti,
        item.innovativeSme,
        item.patentTotal,
        item.patentInvention,
        item.patentUtility,
        item.patentDesign,
        item.rndInputRatio,
        item.newProductRevenueRatio,
        item.employeeCount,
        item.nationalTalent,
        item.provincialTalent
      ])
      
      // 添加BOM头以支持中文
      const bom = '\uFEFF'
      const csvContent = bom + [headers.join(','), ...rows.map(row => row.join(','))].join('\n')
      
      // 创建下载链接
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `企业指标_${this.query.year}年度_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.csv`)
      link.style.visibility = 'hidden'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      this.$message.success('导出成功')
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

.el-pagination .btn-prev,
.el-pagination .btn-next {
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.el-pagination .btn-prev:disabled,
.el-pagination .btn-next:disabled {
  color: #c0c4cc;
}

.el-pagination .el-pager li {
  margin: 0 4px;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  border-radius: 4px;
}

.el-pagination .el-pager li.active {
  background: #409EFF;
  color: #fff;
}
</style>