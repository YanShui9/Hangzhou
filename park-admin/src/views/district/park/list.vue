<template>
  <div class="park-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
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
          <el-form-item label="园区状态">
            <el-select v-model="queryParams.parkStatus" placeholder="请选择状态" clearable>
              <el-option label="规划中" :value="1" />
              <el-option label="建设中" :value="2" />
              <el-option label="已投运" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属区域">
            <el-select v-model="queryParams.district" placeholder="请选择区域" clearable>
              <el-option label="上城区" value="上城区" />
              <el-option label="下城区" value="下城区" />
              <el-option label="西湖区" value="西湖区" />
              <el-option label="拱墅区" value="拱墅区" />
              <el-option label="江干区" value="江干区" />
              <el-option label="滨江区" value="滨江区" />
              <el-option label="萧山区" value="萧山区" />
              <el-option label="余杭区" value="余杭区" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增园区</el-button>
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
          <el-table-column prop="district" label="所属区域" width="100" align="center" />
          <el-table-column label="园区认定" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="row.isCertified === '1' ? 'success' : 'warning'">
                {{ row.isCertified === '1' ? '已认定' : '未认定' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="星级评定" width="100" align="center">
            <template slot-scope="{ row }">
              <span v-if="row.starLevel && row.starLevel !== '-'">{{ row.starLevel }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="园区状态" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="getStatusType(row.parkStatus)" :effect="row.parkStatus === '2' ? 'dark' : 'plain'">
                <i :class="getStatusIcon(row.parkStatus)" :style="getStatusIconStyle(row.parkStatus)"></i>
                {{ getStatusText(row.parkStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="园区类型" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="row.parkType === '1' ? 'primary' : 'success'">
                {{ row.parkType === '1' ? '制造类' : '服务类' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="developmentMode" label="开发模式" width="120" align="center" />
          <el-table-column prop="landSource" label="土地来源" width="100" align="center" />
          <el-table-column prop="landNature" label="土地性质" width="100" align="center" />
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="{ row }">
              <el-button type="text" icon="el-icon-edit" @click="handleEdit(row)" size="small">编辑</el-button>
              <el-button type="text" icon="el-icon-delete" @click="handleDelete(row)" size="small" style="color: #f56c6c">删除</el-button>
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
        parkType: null,
        parkStatus: null,
        district: '',
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
      getParkList(this.queryParams).then(res => {
        this.parkList = res.data.records || this.getMockData()
        this.total = res.data.total || 104
      }).catch(() => {
        this.parkList = this.getMockData()
        this.total = 104
      }).finally(() => {
        this.loading = false
      })
    },
    getMockData() {
      const data = []
      const districts = ['滨江区', '萧山区', '钱塘区', '西湖区', '余杭区']
      const statuses = ['3', '1', '2']
      const types = ['2', '1']
      const modes = ['政府主导', '企业自建', '政企合作', '政府引导', '市场运营']
      const landSources = ['划拨', '出让', '租赁']
      const landNatures = ['工业用地', '商业用地', '商务用地']
      const stars = ['五星级', '四星级', '三星级', '-']
      const names = [
        '万轮科技园', '传化国际科创园', '和达药谷中心', '颐高创业园',
        '天明国际产业园', '乐富海邦园', '银海科创中心', '杭州湾信息港',
        '钱塘生物港（一期）', '菜鸟智谷产业园'
      ]

      for (let i = 1; i <= 17; i++) {
        const idx = (i - 1) % names.length
        data.push({
          id: i,
          parkName: names[idx] + (i > names.length ? i : ''),
          parkCode: 'DS2026' + String(i).padStart(3, '0'),
          district: districts[Math.floor(Math.random() * districts.length)],
          isCertified: Math.random() > 0.3 ? '1' : '0',
          starLevel: stars[Math.floor(Math.random() * stars.length)],
          parkStatus: statuses[Math.floor(Math.random() * statuses.length)],
          parkType: types[Math.floor(Math.random() * types.length)],
          developmentMode: modes[Math.floor(Math.random() * modes.length)],
          landSource: landSources[Math.floor(Math.random() * landSources.length)],
          landNature: landNatures[Math.floor(Math.random() * landNatures.length)]
        })
      }
      return data
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        parkName: '',
        parkType: null,
        parkStatus: null,
        district: '',
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
      this.$router.push(`/district/park/detail/${row.id}?edit=1`)
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
    getStatusText(status) {
      const map = {
        '1': '规划中',
        '2': '建设中',
        '3': '已投运'
      }
      return map[status] || '-'
    },
    getStatusType(status) {
      const map = {
        '1': 'warning',
        '2': 'primary',
        '3': 'success'
      }
      return map[status] || 'info'
    },
    getStatusIcon(status) {
      const map = {
        '1': 'el-icon-warning',
        '2': 'el-icon-info',
        '3': 'el-icon-check'
      }
      return map[status] || 'el-icon-circle'
    },
    getStatusIconStyle(status) {
      const map = {
        '1': 'color: #E6A23C; margin-right: 4px;',
        '2': 'color: #409EFF; margin-right: 4px;',
        '3': 'color: #67C23A; margin-right: 4px;'
      }
      return map[status] || 'margin-right: 4px;'
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
