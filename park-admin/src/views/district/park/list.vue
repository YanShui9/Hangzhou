<template>
  <div class="park-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="园区名称">
          <el-input
            v-model="queryParams.parkName"
            placeholder="请输入园区名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="园区类型">
          <el-select v-model="queryParams.parkType" placeholder="请选择类型" clearable>
            <el-option label="制造类" :value="1" />
            <el-option label="服务类" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="parkList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="园区类型" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.parkType === 1 ? '' : 'success'">
              {{ row.parkType === 1 ? '制造类' : '服务类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="已建面积（亩）" width="130" align="center">
          <template slot-scope="{ row }">
            {{ row.buildArea ? row.buildArea.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" align="center" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" align="center" />
        <el-table-column label="星级" width="100" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.starLevel">{{ row.starLevel }}星</span>
            <span v-else class="text-muted">未评定</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-view" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
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
    </el-card>
  </div>
</template>

<script>
import { getParkList } from '@/api/park'

/**
 * 区县端园区列表页面
 * @author park-team
 */
export default {
  name: 'DistrictParkList',
  data() {
    return {
      /** 查询参数 */
      queryParams: {
        parkName: '',
        parkType: null,
        pageNum: 1,
        pageSize: 10
      },
      /** 园区列表 */
      parkList: [],
      /** 总记录数 */
      total: 0,
      /** 加载状态 */
      loading: false
    }
  },
  /**
   * 页面创建时加载数据
   */
  created() {
    this.getList()
  },
  methods: {
    /**
     * 获取园区列表
     */
    getList() {
      this.loading = true
      getParkList(this.queryParams).then(res => {
        this.parkList = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
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
        parkName: '',
        parkType: null,
        pageNum: 1,
        pageSize: 10
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
     * 查看园区详情
     * @param {Object} row - 园区数据
     */
    handleView(row) {
      this.$router.push(`/district/park/detail/${row.id}`)
    }
  }
}
</script>

<style scoped>
.park-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
