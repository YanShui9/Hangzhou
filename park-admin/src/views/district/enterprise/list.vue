<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="filter-container">
      <el-input
        v-model="queryParams.enterpriseName"
        placeholder="企业名称/信用代码"
        style="width: 200px;"
        class="filter-item"
        clearable
        @keyup.enter.native="handleQuery"
      />
      <el-select
        v-model="queryParams.enterpriseHonor"
        placeholder="企业荣誉"
        style="width: 150px;"
        class="filter-item"
        clearable
      >
        <el-option
          v-for="item in honorOptions"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
      <el-select
        v-model="queryParams.parkId"
        placeholder="所属园区"
        style="width: 150px;"
        class="filter-item"
        clearable
      >
        <el-option
          v-for="item in parkOptions"
          :key="item.id"
          :label="item.parkName"
          :value="item.id"
        />
      </el-select>
      <el-button
        class="filter-item query-btn"
        type="primary"
        @click="handleQuery"
      >
        查询
      </el-button>
      <el-button
        class="filter-item"
        @click="resetQuery"
      >
        重置
      </el-button>
    </div>

    <!-- 表格容器 -->
    <div class="table-wrapper">
      <div class="table-header-wrapper">
        <table class="header-table">
          <thead>
            <tr>
              <th width="50">序号</th>
              <th width="180">企业名称</th>
              <th width="180">统一信用代码</th>
              <th width="100">所属区域</th>
              <th width="150">所属园区</th>
              <th width="100">企业荣誉</th>
              <th width="100">登记状态</th>
              <th width="100">法定代表人</th>
              <th width="100">联系人</th>
              <th width="120">联系电话</th>
              <th width="100">操作</th>
            </tr>
          </thead>
        </table>
      </div>
      <div class="table-body-wrapper" @scroll="handleScroll">
        <table class="body-table">
          <tbody>
            <tr 
              v-for="(row, index) in enterpriseList" 
              :key="row.id"
              :class="{ 'even-row': index % 2 === 0, 'odd-row': index % 2 !== 0 }"
            >
              <td width="50" align="center">{{ (queryParams.pageNum - 1) * queryParams.pageSize + index + 1 }}</td>
              <td width="180">
                <span 
                  class="tooltip-trigger"
                  :data-tooltip="row.enterpriseName"
                  @mouseenter="showTooltip($event, row.enterpriseName)"
                  @mouseleave="hideTooltip"
                >{{ truncateText(row.enterpriseName, 15) }}</span>
              </td>
              <td width="180">
                <span 
                  class="tooltip-trigger"
                  :data-tooltip="row.creditCode"
                  @mouseenter="showTooltip($event, row.creditCode)"
                  @mouseleave="hideTooltip"
                >{{ truncateText(row.creditCode, 18) }}</span>
              </td>
              <td width="100" align="center">{{ row.districtName || '-' }}</td>
              <td width="150">
                <span 
                  class="tooltip-trigger"
                  :data-tooltip="getParkName(row.parkId)"
                  @mouseenter="showTooltip($event, getParkName(row.parkId))"
                  @mouseleave="hideTooltip"
                >{{ truncateText(getParkName(row.parkId), 12) }}</span>
              </td>
              <td width="100" align="center">{{ row.honor || '-' }}</td>
              <td width="100" align="center">{{ row.status || '-' }}</td>
              <td width="100" align="center">{{ row.legalPerson || '-' }}</td>
              <td width="100" align="center">{{ row.contactName || '-' }}</td>
              <td width="120" align="center">{{ row.contactPhone || '-' }}</td>
              <td width="100" align="center">
                <a href="javascript:void(0)" class="view-detail" @click="handleView(row)">查看详情</a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <span class="total-text">共 {{ total }} 条</span>
      <el-select
        v-model="queryParams.pageSize"
        style="width: 80px;"
        class="page-size-select"
        @change="handleSizeChange"
      >
        <el-option :label="'10条/页'" :value="10" />
        <el-option :label="'20条/页'" :value="20" />
        <el-option :label="'50条/页'" :value="50" />
        <el-option :label="'100条/页'" :value="100" />
      </el-select>
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="prev, pager, next, jumper"
        :disabled="loading"
        @current-change="handleCurrentChange"
      />
      <span class="go-page">前往 <input type="number" v-model.number="jumpPage" class="jump-input" /> 页</span>
      <el-button size="mini" @click="handleJump">确定</el-button>
    </div>

    <!-- Tooltip -->
    <div 
      v-if="tooltipVisible" 
      class="custom-tooltip"
      :style="{ left: tooltipX + 'px', top: tooltipY + 'px' }"
    >
      {{ tooltipText }}
    </div>
  </div>
</template>

<script>
import { getEnterpriseList } from '@/api/enterprise'
import { getParkList } from '@/api/park'

export default {
  name: 'DistrictEnterpriseList',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        enterpriseName: '',
        enterpriseHonor: '',
        parkId: undefined
      },
      enterpriseList: [],
      total: 0,
      loading: false,
      parkOptions: [],
      honorOptions: [
        '高新技术企业',
        '科技型中小企业',
        '省级研发中心',
        '市级研发中心'
      ],
      tooltipVisible: false,
      tooltipText: '',
      tooltipX: 0,
      tooltipY: 0,
      jumpPage: 1
    }
  },
  created() {
    this.getList()
    this.getParkOptions()
  },
  methods: {
    getList() {
      this.loading = true
      getEnterpriseList(this.queryParams).then(response => {
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
    getParkOptions() {
      getParkList({ pageNum: 1, pageSize: 100 }).then(res => {
        this.parkOptions = res.data.records || []
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        enterpriseName: '',
        enterpriseHonor: '',
        parkId: undefined
      }
      this.getList()
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
      if (this.jumpPage >= 1 && this.jumpPage <= Math.ceil(this.total / this.queryParams.pageSize)) {
        this.queryParams.pageNum = this.jumpPage
        this.getList()
      }
    },
    getParkName(parkId) {
      const park = this.parkOptions.find(item => item.id === parkId)
      return park ? park.parkName : '-'
    },
    handleView(row) {
      this.$router.push(`/district/enterprise/detail/${row.id}`)
    },
    truncateText(text, maxLength) {
      if (!text) return '-'
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
    },
    showTooltip(event, text) {
      if (!text || text.length <= 15) return
      
      this.tooltipText = text
      const rect = event.target.getBoundingClientRect()
      this.tooltipX = rect.left + rect.width / 2
      this.tooltipY = rect.top - 10
      this.tooltipVisible = true
    },
    hideTooltip() {
      this.tooltipVisible = false
    },
    handleScroll() {
      // 滚动处理
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 15px;
  background: #f5f7fa;
  min-height: 100vh;
}

.filter-container {
  background: #fff;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.filter-item {
  margin-right: 15px;
  margin-bottom: 10px;
}

.query-btn {
  background: #2385bb;
  border-color: #2385bb;
}

.query-btn:hover {
  background: #1a6a96;
  border-color: #1a6a96;
}

.table-wrapper {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.table-header-wrapper {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
}

.header-table {
  width: 100%;
  border-collapse: collapse;
}

.header-table th {
  padding: 12px 8px;
  background: #fafafa;
  border-bottom: 2px solid #e4e7ed;
  border-right: 1px solid #e4e7ed;
  font-weight: 600;
  color: #606266;
  text-align: left;
  font-size: 13px;
}

.header-table th:last-child {
  border-right: none;
}

.table-body-wrapper {
  max-height: 500px;
  overflow-y: auto;
}

.body-table {
  width: 100%;
  border-collapse: collapse;
}

.body-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #e4e7ed;
  border-right: 1px solid #e4e7ed;
  color: #606266;
  font-size: 13px;
}

.body-table td:last-child {
  border-right: none;
}

.body-table .even-row {
  background: #fff;
}

.body-table .odd-row {
  background: #fafafa;
}

.body-table tr:hover {
  background: #f5f7fa;
}

.tooltip-trigger {
  cursor: default;
  color: #606266;
}

.tooltip-trigger:hover {
  color: #409eff;
}

.view-detail {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
}

.view-detail:hover {
  text-decoration: underline;
}

.custom-tooltip {
  position: fixed;
  background: #1f2329;
  color: #fff;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 9999;
  transform: translate(-50%, -100%);
  pointer-events: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.custom-tooltip::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid #1f2329;
}

.pagination-container {
  background: #fff;
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 10px;
}

.total-text {
  color: #606266;
  font-size: 13px;
}

.page-size-select {
  font-size: 13px;
}

.jump-input {
  width: 50px;
  height: 28px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 0 8px;
  font-size: 13px;
}

.go-page {
  font-size: 13px;
  color: #606266;
}
</style>
