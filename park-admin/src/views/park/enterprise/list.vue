<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="queryParams.keyword"
        placeholder="企业名称/统一信用代码"
        style="width: 240px;"
        class="filter-item"
        clearable
        @keyup.enter.native="handleQuery"
      />
      <el-select
        v-model="queryParams.honor"
        placeholder="企业荣誉"
        style="width: 160px;"
        class="filter-item"
        clearable
      >
        <el-option
          v-for="item in honorOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <div class="date-range">
        <el-date-picker
          v-model="queryParams.entryStartDate"
          type="date"
          placeholder="入驻开始日期"
          style="width: 160px;"
          class="filter-item"
          value-format="yyyy-MM-dd"
          clearable
        />
        <span class="date-separator">至</span>
        <el-date-picker
          v-model="queryParams.entryEndDate"
          type="date"
          placeholder="入驻截止日期"
          style="width: 160px;"
          class="filter-item"
          value-format="yyyy-MM-dd"
          clearable
        />
      </div>
      <div class="filter-right">
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-search"
          @click="handleQuery"
        >
          查询
        </el-button>
        <el-button
          class="filter-item"
          icon="el-icon-refresh"
          @click="resetQuery"
        >
          重置
        </el-button>
        <el-button
          class="filter-item"
          icon="el-icon-download"
          @click="handleExport"
          :loading="exportLoading"
        >
          导出
        </el-button>
      </div>
    </div>

    <div class="table-scroll-container">
      <el-table
      v-loading="loading"
      :data="enterpriseList"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="企业名称" prop="enterpriseName" min-width="150" show-overflow-tooltip />
      <el-table-column label="统一信用代码" prop="creditCode" min-width="180" show-overflow-tooltip />
      <el-table-column label="所属区域" prop="districtName" min-width="100" />
      <el-table-column label="所属园区" prop="parkName" min-width="120" />
      <el-table-column label="企业荣誉" min-width="150">
        <template slot-scope="{ row }">
          <el-tag
            v-for="honor in getHonorTags(row.honor)"
            :key="honor"
            :type="getHonorTagType(honor)"
            size="mini"
            class="honor-tag"
          >
            {{ honor }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="企业状态" min-width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="getEnterpriseStatusType(row.status)">
            {{ row.status || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="登记状态" min-width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="getRegisterStatusType(row.registerStatus)">
            {{ row.registerStatus || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="法定代表人" prop="legalPerson" min-width="100" />
      <el-table-column label="联系人" prop="contactName" min-width="100" />
      <el-table-column label="联系电话" min-width="130">
        <template slot-scope="{ row }">
          {{ maskPhone(row.contactPhone) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleViewDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <span class="total-text">共{{ total }}条</span>
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    </div>
  </div>
</template>

<script>
import { getEnterpriseList } from '@/api/enterprise'
import { mapGetters } from 'vuex'
import XLSX from 'xlsx'

export default {
  name: 'ParkEnterpriseList',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        keyword: '',
        honor: '',
        entryStartDate: '',
        entryEndDate: ''
      },
      enterpriseList: [],
      total: 0,
      loading: false,
      exportLoading: false,
      parkOptions: [],
      districtOptions: ['上城区', '下城区', '西湖区', '江干区', '拱墅区', '滨江区', '萧山区', '余杭区', '富阳区', '临安区', '桐庐县', '建德市', '淳安县'],
      honorOptions: [
        { value: 'national_high', label: '国高' },
        { value: 'provincial_high', label: '省专' },
        { value: 'high_tech', label: '高新技术企业' },
        { value: 'tech_enterprise', label: '科技型中小企业' },
        { value: 'national_small_giant', label: '小巨人' },
        { value: 'provincial_small_giant', label: '省专小巨人' },
        { value: 'innovation', label: '创新型' },
        { value: 'hidden_champion', label: '隐形冠军' },
        { value: 'single_champion', label: '单项冠军' }
      ],
      registerStatusOptions: [
        { value: '存续/在业', label: '存续/在业' },
        { value: '开业', label: '开业' },
        { value: '迁出', label: '迁出' },
        { value: '注销', label: '注销' },
        { value: '吊销', label: '吊销' },
        { value: '撤销', label: '撤销' },
        { value: '停业', label: '停业' },
        { value: '歇业', label: '歇业' },
        { value: '除名', label: '除名' },
        { value: '责令关闭', label: '责令关闭' }
      ]
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.getList()
    this.loadParkOptions()
  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        parkId: this.userInfo.parkId
      }
      getEnterpriseList(params).then(response => {
        const { data } = response
        this.enterpriseList = data.records || []
        this.total = data.total || 0
      }).catch(() => {
        this.enterpriseList = []
        this.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    loadParkOptions() {
      this.parkOptions = [
        { id: '', name: '全部园区' },
        { id: this.userInfo.parkId, name: this.userInfo.parkName || '当前园区' }
      ]
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        keyword: '',
        honor: '',
        entryStartDate: '',
        entryEndDate: ''
      }
      this.getList()
    },
    handleExport() {
      this.$confirm('确认导出当前筛选条件下的企业数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.exportLoading = true
        const params = {
          ...this.queryParams,
          parkId: this.userInfo.parkId,
          pageNum: 1,
          pageSize: 99999
        }
        getEnterpriseList(params).then(response => {
          const data = response.data
          const records = data.records || []
          if (records.length === 0) {
            this.$message.warning('没有数据可导出')
            return
          }
          this.exportToExcel(records)
          this.$message.success('导出成功')
        }).catch(() => {
          this.$message.error('导出失败')
        }).finally(() => {
          this.exportLoading = false
        })
      }).catch(() => {})
    },
    exportToExcel(data) {
      const headers = [
        { key: 'enterpriseName', label: '企业名称' },
        { key: 'creditCode', label: '统一信用代码' },
        { key: 'districtName', label: '所属区域' },
        { key: 'parkName', label: '所属园区' },
        { key: 'honor', label: '企业荣誉' },
        { key: 'status', label: '企业状态' },
        { key: 'registerStatus', label: '登记状态' },
        { key: 'legalPerson', label: '法定代表人' },
        { key: 'contactName', label: '联系人' },
        { key: 'contactPhone', label: '联系电话' },
        { key: 'remark', label: '备注' }
      ]
      const rows = data.map(row => {
        const rowData = {}
        headers.forEach(header => {
          if (header.key === 'honor') {
            rowData[header.label] = this.getHonorTags(row.honor).join('; ') || '-'
          } else if (header.key === 'contactPhone') {
            rowData[header.label] = row.contactPhone || '-'
          } else {
            rowData[header.label] = row[header.key] || '-'
          }
        })
        return rowData
      })
      const worksheet = XLSX.utils.json_to_sheet(rows)
      const workbook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(workbook, worksheet, '入驻企业列表')
      const filename = `入驻企业列表_${new Date().toISOString().slice(0, 10)}.xlsx`
      XLSX.writeFile(workbook, filename)
    },
    handleViewDetail(row) {
      this.$router.push({ name: 'ParkEnterpriseDetail', params: { id: row.id } })
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    getHonorTags(honorStr) {
      if (!honorStr) return []
      const honorMap = {
        'national_high': '国高',
        'provincial_high': '省专',
        'high_tech': '高新技术企业',
        'tech_enterprise': '科技型中小企业',
        'national_small_giant': '小巨人',
        'provincial_small_giant': '省专小巨人',
        'innovation': '创新型',
        'hidden_champion': '隐形冠军',
        'single_champion': '单项冠军'
      }
      return honorStr.split(',').map(h => honorMap[h] || h)
    },
    getHonorTagType(honor) {
      if (honor.includes('国高')) return 'danger'
      if (honor.includes('省专')) return 'warning'
      if (honor.includes('高新技术企业')) return 'success'
      if (honor.includes('科技型中小企业')) return 'primary'
      if (honor.includes('小巨人')) return 'danger'
      if (honor.includes('创新型')) return 'info'
      if (honor.includes('隐形冠军')) return 'success'
      if (honor.includes('单项冠军')) return 'warning'
      return 'info'
    },
    getEnterpriseStatusType(status) {
      if (!status) return 'info'
      if (status.includes('不参评')) return 'danger'
      if (status.includes('参评')) return 'success'
      return 'info'
    },
    getRegisterStatusType(status) {
      if (!status) return 'info'
      if (status.includes('存续') || status.includes('开业') || status.includes('在业')) return 'success'
      if (status.includes('迁出')) return 'info'
      if (status.includes('注销') || status.includes('吊销') || status.includes('撤销')) return 'danger'
      return 'info'
    },
    maskPhone(phone) {
      if (!phone) return '-'
      if (phone.length === 11) {
        return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3')
      }
      return phone
    }
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: #f5f7fa;
  height: calc(100vh - 84px);
  overflow: hidden;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.table-scroll-container {
  flex: 1;
  overflow-y: auto;
}

.filter-left {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-separator {
  color: #909399;
  font-size: 14px;
}

.filter-right {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.filter-item {
  margin-bottom: 8px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 16px;
}

.total-text {
  color: #606266;
  font-size: 14px;
}

.honor-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

@media screen and (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }
  .filter-left, .filter-right {
    justify-content: flex-start;
  }
}

@media screen and (max-width: 900px) {
  .filter-left {
    flex-wrap: wrap;
  }
}
</style>