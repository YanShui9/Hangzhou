<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="filter-container">
      <el-input
        v-model="queryParams.enterpriseName"
        placeholder="企业名称"
        style="width: 200px;"
        class="filter-item"
        clearable
        @keyup.enter.native="handleQuery"
      />
      <el-select
        v-model="queryParams.parkId"
        placeholder="所属园区"
        style="width: 200px;"
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
      <el-select
        v-model="queryParams.industryName"
        placeholder="所属行业"
        style="width: 200px;"
        class="filter-item"
        clearable
      >
        <el-option
          v-for="item in industryOptions"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleQuery"
      >
        搜索
      </el-button>
      <el-button
        class="filter-item"
        icon="el-icon-refresh"
        @click="resetQuery"
      >
        重置
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="enterpriseList"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
      <el-table-column label="所属园区" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">
          {{ getParkName(row.parkId) }}
        </template>
      </el-table-column>
      <el-table-column label="行业" prop="industryName" min-width="120" show-overflow-tooltip />
      <el-table-column label="法定代表人" prop="legalPerson" min-width="100" />
      <el-table-column label="联系人" prop="contactName" min-width="100" />
      <el-table-column label="联系电话" prop="contactPhone" min-width="130" />
      <el-table-column label="是否参评" min-width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.isParticipate === 1 ? 'success' : 'info'">
            {{ row.isParticipate === 1 ? '参评' : '不参评' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="经营状态" prop="status" min-width="80" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === '在营' ? 'success' : 'danger'">
            {{ row.status || '-' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getEnterpriseList } from '@/api/enterprise'
import { getParkList } from '@/api/park'

/**
 * 区县端入驻企业列表页面
 * @author park-team
 */
export default {
  name: 'DistrictEnterpriseList',
  data() {
    return {
      /** 查询参数 */
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        enterpriseName: '',
        parkId: undefined,
        industryName: ''
      },
      /** 企业列表 */
      enterpriseList: [],
      /** 总记录数 */
      total: 0,
      /** 加载状态 */
      loading: false,
      /** 园区选项 */
      parkOptions: [],
      /** 行业选项 */
      industryOptions: [
        '软件和信息技术服务业',
        '计算机、通信和其他电子设备制造业',
        '医药制造业',
        '生态保护和环境治理业',
        '非金属矿物制品业',
        '其他'
      ]
    }
  },
  /**
   * 页面创建时加载数据
   */
  created() {
    this.getList()
    this.getParkOptions()
  },
  methods: {
    /**
     * 获取企业列表
     */
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
    /**
     * 获取园区选项
     */
    getParkOptions() {
      getParkList({ pageNum: 1, pageSize: 100 }).then(res => {
        this.parkOptions = res.data.records || []
      })
    },
    /**
     * 搜索
     */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /**
     * 重置搜索条件
     */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        enterpriseName: '',
        parkId: undefined,
        industryName: ''
      }
      this.getList()
    },
    /**
     * 每页条数改变
     * @param {Number} val - 每页条数
     */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    /**
     * 当前页改变
     * @param {Number} val - 当前页码
     */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    /**
     * 根据园区ID获取园区名称
     * @param {Number} parkId - 园区ID
     * @returns {String} 园区名称
     */
    getParkName(parkId) {
      const park = this.parkOptions.find(item => item.id === parkId)
      return park ? park.parkName : '-'
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 15px;
}

.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}

.pagination-container {
  padding: 15px 0;
  text-align: right;
}
</style>
