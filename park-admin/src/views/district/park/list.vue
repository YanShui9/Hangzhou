<template>
  <div class="park-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
        <el-form :model="queryParams" inline>
          <el-form-item>
            <el-input
              v-model="queryParams.parkName"
              placeholder="园区名称"
              clearable
              @keyup.enter.native="handleQuery"
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryParams.parkType" placeholder="园区类型" clearable style="width: 160px;">
              <el-option label="生产性制造类" value="生产性制造类" />
              <el-option label="生产性服务类" value="生产性服务类" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryParams.starLevel" placeholder="星级认定" clearable style="width: 160px;">
              <el-option label="全部星级" value="" />
              <el-option label="五星级" :value="5" />
              <el-option label="四星级" :value="4" />
              <el-option label="三星级" :value="3" />
              <el-option label="二星级" :value="2" />
              <el-option label="一星级" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="success" icon="el-icon-plus" @click="handleAdd">新增园区</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="parkList"
          border
          style="width: 100%"
          max-height="500"
        >
          <el-table-column label="序号" width="60" align="center" type="index" />
          <el-table-column prop="parkName" label="园区名称" min-width="120">
            <template slot-scope="{ row }">
              <span class="park-name-link" @click="handleView(row)">{{ row.parkName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="parkCode" label="园区代码" width="120" align="center" />
          <el-table-column prop="districtName" label="所属区域" width="100" align="center" />
          <el-table-column label="园区认定" width="140" align="center">
            <template slot-scope="{ row }">
              <el-tag v-if="row.recognition && row.recognition !== '-'" :type="row.recognition.includes('国家') ? 'danger' : row.recognition.includes('省') ? 'success' : 'warning'" size="small">
                {{ row.recognition }}
              </el-tag>
              <span v-else class="text-muted">未认定</span>
            </template>
          </el-table-column>
          <el-table-column label="星级评定" width="120" align="center">
            <template slot-scope="{ row }">
              <span v-if="row.starLevel && row.starLevel > 0" style="color: #E6A23C;">
                <i v-for="n in row.starLevel" :key="n" class="el-icon-star-on" style="font-size: 14px;"></i>
              </span>
              <span v-else class="text-muted">未评定</span>
            </template>
          </el-table-column>
          <el-table-column label="园区状态" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="getStatusType(row.parkStatus)" size="small">
                {{ row.parkStatus || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="园区类型" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="row.parkType === '生产性制造类' ? 'primary' : 'success'" size="small">
                {{ row.parkType || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="devMode" label="开发模式" width="120" align="center" />
          <el-table-column prop="landSource" label="土地来源" width="100" align="center" />
          <el-table-column prop="landNature" label="土地性质" width="100" align="center" />
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="{ row }">
              <el-button type="text" icon="el-icon-edit" @click="handleEdit(row)" size="small">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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

    <!-- 删除确认对话框 -->
    <el-dialog title="删除确认" :visible.sync="deleteDialogVisible" width="400px">
      <p>确定要删除园区 <span style="color: #f56c6c; font-weight: bold">{{ deleteParkName }}</span> 吗？</p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getParkList, deletePark } from '@/api/park'

export default {
  name: 'DistrictParkList',
  data() {
    return {
      queryParams: {
        parkName: '',
        parkType: '',
        starLevel: null,
        pageNum: 1,
        pageSize: 20
      },
      parkList: [],
      total: 0,
      loading: false,
      deleteDialogVisible: false,
      deleteParkId: null,
      deleteParkName: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.queryParams.parkName) params.parkName = this.queryParams.parkName
      if (this.queryParams.parkType) params.parkType = this.queryParams.parkType
      if (this.queryParams.starLevel) params.starLevel = this.queryParams.starLevel

      getParkList(params).then(res => {
        this.parkList = res.data.records || []
        this.total = res.data.total || 0
      }).catch(() => {
        this.parkList = []
        this.total = 0
        this.$message.error('获取园区列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        parkName: '',
        parkType: '',
        starLevel: null,
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    handleView(row) {
      this.$router.push(`/district/park/detail/${row.id}`)
    },
    handleAdd() {
      this.$router.push('/district/park/add')
    },
    handleEdit(row) {
      this.$router.push(`/district/park/edit/${row.id}`)
    },
    handleDelete(row) {
      this.deleteParkId = row.id
      this.deleteParkName = row.parkName
      this.deleteDialogVisible = true
    },
    confirmDelete() {
      deletePark(this.deleteParkId).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
      })
    },
    getStatusType(status) {
      const map = {
        '已投运': 'success',
        '在建': 'warning',
        '规划中': 'info'
      }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped>
.park-list-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.search-card {
  margin-bottom: 16px;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-container {
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}

.park-name-link {
  color: #409EFF;
  cursor: pointer;
  text-decoration: underline;
}

.park-name-link:hover {
  color: #66b1ff;
}

.dialog-footer {
  text-align: right;
}

.el-table .el-tag {
  margin: 0;
}
</style>
