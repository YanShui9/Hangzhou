<template>
  <div class="app-container">
    <!-- 顶部提示条 -->
    <div class="top-tip">
      <i class="el-icon-info"></i>
      <span>导出功能开发中</span>
    </div>

    <el-card>
      <div slot="header" class="clearfix">
        <span>企业指标</span>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 140px;" clearable>
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
        </el-select>
        <el-select v-model="query.districtName" placeholder="园区名称（模糊搜索）" style="width: 220px;" clearable>
          <el-option v-for="park in parkOptions" :key="park.id" :label="park.name" :value="park.name" />
        </el-select>
        <el-select v-model="query.district" placeholder="所属区域" style="width: 140px;" clearable>
          <el-option v-for="d in districtOptions" :key="d" :label="d" :value="d" />
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
export default {
  name: 'DistrictResultEnterprise',
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
        { id: 8, name: '银湖科创中心' },
        { id: 9, name: '尚达创业中心' },
        { id: 10, name: '富春江科创园' }
      ],
      districtOptions: ['滨江区', '萧山区', '余杭区', '上城区', '下城区', '西湖区', '拱墅区', '江干区', '富阳区'],
      loading: false,
      list: [],
      total: 0,
      allData: [], // 存储所有数据用于筛选
      query: {
        pageNum: 1,
        pageSize: 20,
        year: currentYear,
        districtName: '',
        district: '',
        parkType: ''
      }
    }
  },
  created() {
    this.fetchList(false)
  },
  methods: {
    async fetchList(showSuccessMsg = true) {
      this.loading = true
      try {
        // 获取所有数据
        const allData = this.getMockData()
        this.allData = allData
        
        // 根据筛选条件过滤数据
        let filteredData = allData
        
        // 园区名称筛选（模糊匹配）
        if (this.query.districtName) {
          filteredData = filteredData.filter(item => 
            item.parkName.includes(this.query.districtName)
          )
        }
        
        // 所属区域筛选
        if (this.query.district) {
          filteredData = filteredData.filter(item => 
            item.district === this.query.district
          )
        }
        
        // 园区类型筛选
        if (this.query.parkType) {
          filteredData = filteredData.filter(item => 
            item.parkType === this.query.parkType
          )
        }
        
        // 年份筛选（当前年份默认选中）
        // 这里可以根据实际需求添加年份筛选逻辑
        
        // 分页处理
        this.total = filteredData.length
        const start = (this.query.pageNum - 1) * this.query.pageSize
        const end = start + this.query.pageSize
        this.list = filteredData.slice(start, end)
        
        // 只有用户主动查询时才显示成功提示
        if (showSuccessMsg) {
          this.$message.success('查询成功')
        }
      } catch (e) {
        console.error('获取企业指标列表失败', e)
        this.$message.error('查询失败')
      } finally {
        this.loading = false
      }
    },

    getMockData() {
      return [
        {
          parkName: '传化国际科创园',
          district: '滨江区',
          parkType: '2',
          totalEnterprises: 59,
          aboveScale: 5,
          highTech: 12,
          techSme: 28,
          hiddenChampion: 3,
          provincialSrti: 8,
          nationalSrti: 2,
          innovativeSme: 22,
          patentTotal: 156,
          patentInvention: 32,
          patentUtility: 88,
          patentDesign: 36,
          rndInputRatio: 5.2,
          newProductRevenueRatio: 35.6,
          employeeCount: 1256,
          nationalTalent: 8,
          provincialTalent: 15
        },
        {
          parkName: '万轮科创园',
          district: '滨江区',
          parkType: '2',
          totalEnterprises: 52,
          aboveScale: 4,
          highTech: 10,
          techSme: 24,
          hiddenChampion: 2,
          provincialSrti: 6,
          nationalSrti: 1,
          innovativeSme: 18,
          patentTotal: 132,
          patentInvention: 28,
          patentUtility: 72,
          patentDesign: 32,
          rndInputRatio: 4.8,
          newProductRevenueRatio: 32.4,
          employeeCount: 1086,
          nationalTalent: 6,
          provincialTalent: 12
        },
        {
          parkName: '杭州湾信息港',
          district: '萧山区',
          parkType: '2',
          totalEnterprises: 68,
          aboveScale: 6,
          highTech: 15,
          techSme: 32,
          hiddenChampion: 4,
          provincialSrti: 10,
          nationalSrti: 3,
          innovativeSme: 26,
          patentTotal: 186,
          patentInvention: 42,
          patentUtility: 102,
          patentDesign: 42,
          rndInputRatio: 5.8,
          newProductRevenueRatio: 38.2,
          employeeCount: 1520,
          nationalTalent: 10,
          provincialTalent: 18
        },
        {
          parkName: '尚达药谷中心',
          district: '余杭区',
          parkType: '1',
          totalEnterprises: 45,
          aboveScale: 8,
          highTech: 18,
          techSme: 22,
          hiddenChampion: 5,
          provincialSrti: 12,
          nationalSrti: 4,
          innovativeSme: 15,
          patentTotal: 208,
          patentInvention: 56,
          patentUtility: 112,
          patentDesign: 40,
          rndInputRatio: 8.5,
          newProductRevenueRatio: 45.8,
          employeeCount: 986,
          nationalTalent: 12,
          provincialTalent: 22
        },
        {
          parkName: '颐高创业园',
          district: '上城区',
          parkType: '2',
          totalEnterprises: 55,
          aboveScale: 4,
          highTech: 11,
          techSme: 26,
          hiddenChampion: 2,
          provincialSrti: 7,
          nationalSrti: 2,
          innovativeSme: 20,
          patentTotal: 142,
          patentInvention: 30,
          patentUtility: 80,
          patentDesign: 32,
          rndInputRatio: 4.5,
          newProductRevenueRatio: 30.8,
          employeeCount: 1120,
          nationalTalent: 7,
          provincialTalent: 14
        },
        {
          parkName: '天和国际产业园',
          district: '萧山区',
          parkType: '1',
          totalEnterprises: 48,
          aboveScale: 10,
          highTech: 16,
          techSme: 24,
          hiddenChampion: 6,
          provincialSrti: 11,
          nationalSrti: 3,
          innovativeSme: 18,
          patentTotal: 195,
          patentInvention: 48,
          patentUtility: 108,
          patentDesign: 39,
          rndInputRatio: 7.2,
          newProductRevenueRatio: 42.5,
          employeeCount: 1350,
          nationalTalent: 9,
          provincialTalent: 19
        },
        {
          parkName: '富春湾科创园',
          district: '富阳区',
          parkType: '1',
          totalEnterprises: 42,
          aboveScale: 6,
          highTech: 12,
          techSme: 20,
          hiddenChampion: 3,
          provincialSrti: 8,
          nationalSrti: 2,
          innovativeSme: 16,
          patentTotal: 168,
          patentInvention: 38,
          patentUtility: 92,
          patentDesign: 38,
          rndInputRatio: 6.5,
          newProductRevenueRatio: 38.6,
          employeeCount: 896,
          nationalTalent: 6,
          provincialTalent: 13
        },
        {
          parkName: '银湖科创中心',
          district: '富阳区',
          parkType: '2',
          totalEnterprises: 38,
          aboveScale: 3,
          highTech: 9,
          techSme: 18,
          hiddenChampion: 2,
          provincialSrti: 5,
          nationalSrti: 1,
          innovativeSme: 14,
          patentTotal: 118,
          patentInvention: 24,
          patentUtility: 66,
          patentDesign: 28,
          rndInputRatio: 4.2,
          newProductRevenueRatio: 28.5,
          employeeCount: 756,
          nationalTalent: 4,
          provincialTalent: 10
        },
        {
          parkName: '尚达创业中心',
          district: '余杭区',
          parkType: '1',
          totalEnterprises: 45,
          aboveScale: 8,
          highTech: 18,
          techSme: 22,
          hiddenChampion: 5,
          provincialSrti: 12,
          nationalSrti: 4,
          innovativeSme: 15,
          patentTotal: 208,
          patentInvention: 56,
          patentUtility: 112,
          patentDesign: 40,
          rndInputRatio: 8.5,
          newProductRevenueRatio: 45.8,
          employeeCount: 986,
          nationalTalent: 12,
          provincialTalent: 22
        },
        {
          parkName: '富春江科创园',
          district: '富阳区',
          parkType: '1',
          totalEnterprises: 42,
          aboveScale: 6,
          highTech: 12,
          techSme: 20,
          hiddenChampion: 3,
          provincialSrti: 8,
          nationalSrti: 2,
          innovativeSme: 16,
          patentTotal: 168,
          patentInvention: 38,
          patentUtility: 92,
          patentDesign: 38,
          rndInputRatio: 6.5,
          newProductRevenueRatio: 38.6,
          employeeCount: 896,
          nationalTalent: 6,
          provincialTalent: 13
        }
      ]
    },

    handleExport() {
      // 获取所有符合条件的数据
      let exportData = this.allData
      
      // 根据当前筛选条件过滤数据
      if (this.query.districtName) {
        exportData = exportData.filter(item => 
          item.parkName.includes(this.query.districtName)
        )
      }
      if (this.query.district) {
        exportData = exportData.filter(item => 
          item.district === this.query.district
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