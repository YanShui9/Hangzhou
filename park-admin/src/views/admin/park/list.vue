<template>
  <div class="park-list-container page-list-flex">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">园区列表</h2>
    </div>

    <!-- 搜索过滤区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input
          v-model="queryParams.parkName"
          placeholder="园区名称"
          clearable
          size="small"
          class="filter-item"
          style="width: 160px"
          @keyup.enter.native="handleQuery"
        />
        <el-select
          v-model="queryParams.districtName"
          placeholder="所属区域"
          clearable
          size="small"
          class="filter-item"
          style="width: 130px"
        >
          <el-option
            v-for="item in districtOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select
          v-model="queryParams.parkType"
          placeholder="园区类型"
          clearable
          size="small"
          class="filter-item"
          style="width: 130px"
        >
          <el-option label="制造类" value="制造类" />
          <el-option label="服务类" value="服务类" />
        </el-select>
        <el-select
          v-model="queryParams.starLevel"
          placeholder="星级认定"
          clearable
          size="small"
          class="filter-item"
          style="width: 130px"
        >
          <el-option label="三星级" :value="3" />
          <el-option label="四星级" :value="4" />
          <el-option label="五星级" :value="5" />
        </el-select>
        <el-select
          v-model="queryParams.year"
          placeholder="年度"
          clearable
          size="small"
          class="filter-item"
          style="width: 130px"
        >
          <el-option
            v-for="item in yearOptions"
            :key="item"
            :label="item + '年度'"
            :value="item"
          />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </div>
      <div class="filter-right">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增园区</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
      <el-table
        v-loading="loading"
        :data="parkList"
        border
        stripe
        size="mini"
        height="100%"
        class="park-table"
      >
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span class="park-name-link" @click="handleViewDetail(row)">{{ row.parkName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="parkCode" label="园区代码" width="100" align="center" />
      <el-table-column prop="districtName" label="所属区域" width="80" align="center" />
      <el-table-column prop="recognition" label="园区认定" width="80" align="center">
        <template slot-scope="{ row }">
          <el-tag
            v-if="row.recognition"
            :type="row.recognition === '已认定' ? 'success' : 'info'"
            size="mini"
            effect="plain"
          >
            {{ row.recognition }}
          </el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column label="星级评定" width="80" align="center">
        <template slot-scope="{ row }">
          <span v-if="row.starLevel" class="star-text">
            <i class="el-icon-star-on"></i>
            <span>{{ getStarText(row.starLevel) }}</span>
          </span>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column label="园区状态" width="90" align="center">
        <template slot-scope="{ row }">
          <span v-if="row.parkStatus" class="status-text" :class="'status-' + getStatusKey(row.parkStatus)">
            <span class="status-dot"></span>
            {{ row.parkStatus }}
          </span>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column label="园区类型" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag
            :type="row.parkType === '制造类' ? '' : 'success'"
            size="mini"
            effect="plain"
          >
            {{ row.parkType || '--' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="devMode" label="开发模式" width="100" align="center">
        <template slot-scope="{ row }">{{ row.devMode || '--' }}</template>
      </el-table-column>
      <el-table-column prop="landSource" label="土地来源" width="80" align="center">
        <template slot-scope="{ row }">{{ row.landSource || '--' }}</template>
      </el-table-column>
      <el-table-column prop="landNature" label="土地性质" width="80" align="center">
        <template slot-scope="{ row }">{{ row.landNature || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" align="center" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" class="delete-link" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-bar">
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        small
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="760px"
      append-to-body
      :close-on-click-modal="false"
      class="park-dialog"
    >
      <el-form
        ref="parkForm"
        :model="parkForm"
        :rules="parkRules"
        label-width="100px"
        class="park-form"
      >
        <div class="form-section">
          <div class="section-title">基础信息</div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="园区名称" prop="parkName">
                <el-input v-model="parkForm.parkName" placeholder="请输入园区名称" size="small" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="园区代码" prop="parkCode">
                <el-input v-model="parkForm.parkCode" placeholder="如 DS2026001" size="small" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="园区类型" prop="parkType">
                <el-select v-model="parkForm.parkType" placeholder="请选择园区类型" size="small" style="width: 100%">
                  <el-option label="制造类" value="制造类" />
                  <el-option label="服务类" value="服务类" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属区域" prop="districtName">
                <el-select v-model="parkForm.districtName" placeholder="请选择区县" size="small" style="width: 100%">
                  <el-option
                    v-for="item in districtOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <div class="section-title">园区状态</div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="园区认定">
                <el-select v-model="parkForm.recognition" placeholder="请选择" size="small" style="width: 100%">
                  <el-option label="已认定" value="已认定" />
                  <el-option label="未认定" value="未认定" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="星级评定">
                <el-select v-model="parkForm.starLevel" placeholder="请选择" size="small" style="width: 100%">
                  <el-option label="三星级" :value="3" />
                  <el-option label="四星级" :value="4" />
                  <el-option label="五星级" :value="5" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="园区状态">
                <el-select v-model="parkForm.parkStatus" placeholder="请选择状态" size="small" style="width: 100%">
                  <el-option label="已投运" value="已投运" />
                  <el-option label="建设中" value="建设中" />
                  <el-option label="规划中" value="规划中" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开发模式">
                <el-select v-model="parkForm.devMode" placeholder="请选择" size="small" style="width: 100%">
                  <el-option label="政府主导" value="政府主导" />
                  <el-option label="企业自建" value="企业自建" />
                  <el-option label="政企合作" value="政企合作" />
                  <el-option label="市场运营" value="市场运营" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <div class="section-title">土地信息</div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="土地来源">
                <el-select v-model="parkForm.landSource" placeholder="请选择" size="small" style="width: 100%">
                  <el-option label="划拨" value="划拨" />
                  <el-option label="出让" value="出让" />
                  <el-option label="租赁" value="租赁" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="土地性质">
                <el-select v-model="parkForm.landNature" placeholder="请选择" size="small" style="width: 100%">
                  <el-option label="工业用地" value="工业用地" />
                  <el-option label="商业用地" value="商业用地" />
                  <el-option label="商务用地" value="商务用地" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="已建面积（亩）">
                <el-input-number
                  v-model="parkForm.buildArea"
                  :min="0"
                  :precision="2"
                  size="small"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="用地面积（亩）">
                <el-input-number
                  v-model="parkForm.landArea"
                  :min="0"
                  :precision="2"
                  size="small"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="园区地址">
                <el-input v-model="parkForm.address" placeholder="请输入园区地址" size="small" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <div class="section-title">园区简介</div>
          <el-form-item label="简介">
            <el-input
              v-model="parkForm.introduction"
              type="textarea"
              :rows="3"
              placeholder="请输入园区简介"
              size="small"
            />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      title="园区详情"
      :visible.sync="detailVisible"
      width="800px"
      append-to-body
      class="park-dialog"
    >
      <div v-if="currentPark" class="park-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="园区名称">{{ currentPark.parkName }}</el-descriptions-item>
          <el-descriptions-item label="园区代码">{{ currentPark.parkCode }}</el-descriptions-item>
          <el-descriptions-item label="所属区域">{{ currentPark.districtName }}</el-descriptions-item>
          <el-descriptions-item label="园区类型">{{ currentPark.parkType || '--' }}</el-descriptions-item>
          <el-descriptions-item label="园区认定">{{ currentPark.recognition || '--' }}</el-descriptions-item>
          <el-descriptions-item label="星级评定">
            <span v-if="currentPark.starLevel">
              <i class="el-icon-star-on" style="color: #E6A23C"></i>{{ getStarText(currentPark.starLevel) }}
            </span>
            <span v-else class="text-muted">未评定</span>
          </el-descriptions-item>
          <el-descriptions-item label="园区状态">{{ currentPark.parkStatus || '--' }}</el-descriptions-item>
          <el-descriptions-item label="开发模式">{{ currentPark.devMode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="土地来源">{{ currentPark.landSource || '--' }}</el-descriptions-item>
          <el-descriptions-item label="土地性质">{{ currentPark.landNature || '--' }}</el-descriptions-item>
          <el-descriptions-item label="已建面积（亩）">
            {{ currentPark.buildArea ? currentPark.buildArea.toFixed(2) : '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="用地面积（亩）">
            {{ currentPark.landArea ? currentPark.landArea.toFixed(2) : '--' }}
          </el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ currentPark.address || '--' }}</el-descriptions-item>
          <el-descriptions-item label="园区简介" :span="2">{{ currentPark.introduction || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getParkList, getParkDetail, savePark, updatePark, deletePark } from '@/api/park'

export default {
  name: 'AdminParkList',
  data() {
    return {
      // 查询参数
      queryParams: {
        parkName: '',
        districtName: '',
        parkType: '',
        starLevel: null,
        year: null,
        pageNum: 1,
        pageSize: 20
      },
      // 区县选项
      districtOptions: [
        '上城区', '下城区', '西湖区', '滨江区', '萧山区',
        '余杭区', '富阳区', '临安区', '临平区', '钱塘区',
        '桐庐县', '淳安县', '建德市'
      ],
      // 年度选项
      yearOptions: [2025, 2024, 2023, 2022],
      // 表格数据
      parkList: [],
      total: 0,
      loading: false,
      // 对话框
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      // 详情对话框
      detailVisible: false,
      currentPark: null,
      // 表单
      parkForm: {
        id: null,
        parkName: '',
        parkCode: '',
        parkType: '',
        districtId: null,
        districtName: '',
        address: '',
        recognition: '',
        starLevel: null,
        parkStatus: '',
        devMode: '',
        landSource: '',
        landNature: '',
        buildArea: null,
        landArea: null,
        introduction: ''
      },
      // 表单校验规则
      parkRules: {
        parkName: [
          { required: true, message: '请输入园区名称', trigger: 'blur' }
        ],
        districtName: [
          { required: true, message: '请选择所属区域', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 获取园区列表 */
    getList() {
      this.loading = true
      getParkList(this.queryParams).then(res => {
        this.parkList = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },
    /** 查询按钮 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置（同查询，因为查询按钮本身就是重新查询） */
    resetQuery() {
      this.queryParams = {
        parkName: '',
        districtName: '',
        parkType: '',
        starLevel: null,
        year: null,
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    /** 新增园区 - 跳转到新增园区页面 */
    handleAdd() {
      this.$router.push('/admin/park/add')
    },
    /** 编辑（从详情进入时，点击编辑可进入编辑模式） */
    handleEdit(row) {
      this.dialogTitle = '编辑园区'
      this.dialogVisible = true
      getParkDetail(row.id).then(res => {
        this.parkForm = { ...res.data }
      })
      this.$nextTick(() => {
        this.$refs.parkForm && this.$refs.parkForm.clearValidate()
      })
    },
    /** 查看详情 - 跳转到园区详情页 */
    handleViewDetail(row) {
      if (!row || !row.id) {
        this.$message.warning('园区信息不完整')
        return
      }
      this.$router.push(`/admin/park/detail/${row.id}`)
    },
    /** 删除园区 */
    handleDelete(row) {
      this.$confirm(`确认删除园区 "${row.parkName}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePark(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    /** 提交表单 */
    submitForm() {
      this.$refs.parkForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const request = this.parkForm.id ? updatePark : savePark
          request(this.parkForm).then(() => {
            this.$message.success(this.parkForm.id ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.getList()
          }).finally(() => {
            this.submitLoading = false
          })
        }
      })
    },
    /** 分页大小改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    /** 页码改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    /** 获取星级文本 */
    getStarText(level) {
      const starMap = { 3: '三星级', 4: '四星级', 5: '五星级' }
      return starMap[level] || level + '星'
    },
    /** 获取状态样式 key */
    getStatusKey(status) {
      if (!status) return 'default'
      if (status.includes('已投运') || status.includes('运营')) return 'success'
      if (status.includes('建设') || status.includes('施工')) return 'info'
      if (status.includes('规划')) return 'warning'
      return 'default'
    }
  }
}
</script>

<style scoped>
.park-list-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  height: 100%;
  overflow: hidden;
}

/* 页面标题 */
.page-header {
  margin-bottom: 14px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 搜索过滤区 - 紧凑横向布局 */
.filter-bar {
  background: #FFFFFF;
  padding: 14px 16px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.filter-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-item {
  margin-right: 0 !important;
}

/* 数据表格 */
.park-table {
  background: #FFFFFF;
  border-radius: 4px;
}

.park-table >>> .el-table__header th {
  background: #F5F7FA !important;
  color: #303133;
  font-weight: 600;
  font-size: 13px;
}

.park-table >>> .el-table__body td {
  font-size: 13px;
  color: #606266;
}

.park-table >>> .el-table__row--striped td {
  background: #FAFAFA;
}

.park-table >>> .el-table__row:hover > td {
  background: #F0F6FF !important;
}

/* 园区名称链接样式 */
.park-name-link {
  color: #409EFF;
  cursor: pointer;
  font-weight: 500;
}

.park-name-link:hover {
  text-decoration: underline;
}

/* 星级显示 */
.star-text {
  color: #E6A23C;
  font-size: 13px;
}

.star-text i {
  color: #E6A23C;
  margin-right: 2px;
  font-size: 12px;
}

/* 状态显示 - 带圆点 */
.status-text {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
}

.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  background: #909399;
}

.status-success {
  color: #67C23A;
}
.status-success .status-dot {
  background: #67C23A;
  box-shadow: 0 0 4px rgba(103, 194, 58, 0.5);
}

.status-warning {
  color: #E6A23C;
}
.status-warning .status-dot {
  background: #E6A23C;
  box-shadow: 0 0 4px rgba(230, 162, 60, 0.5);
}

.status-info {
  color: #409EFF;
}
.status-info .status-dot {
  background: #409EFF;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.5);
}

.status-default {
  color: #909399;
}

/* 删除链接 */
.delete-link {
  color: #F56C6C !important;
  font-size: 13px !important;
}

.delete-link:hover {
  color: #F56C6C !important;
  text-decoration: underline;
}

.text-muted {
  color: #C0C4CC;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  padding: 12px 0 4px;
  background: #FFFFFF;
  margin-top: 0;
}

/* 对话框 */
.park-dialog >>> .el-dialog__header {
  border-bottom: 1px solid #E4E7ED;
  padding: 14px 20px;
}

.park-dialog >>> .el-dialog__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.park-dialog >>> .el-dialog__body {
  padding: 16px 20px;
  max-height: 65vh;
  overflow-y: auto;
}

.park-dialog >>> .el-dialog__footer {
  border-top: 1px solid #E4E7ED;
  padding: 10px 20px;
}

/* 表单分组 */
.form-section {
  margin-bottom: 16px;
  padding-bottom: 4px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  padding: 6px 10px;
  margin-bottom: 12px;
  background: #F5F7FA;
  border-left: 3px solid #409EFF;
  border-radius: 2px;
}

.park-form >>> .el-form-item {
  margin-bottom: 14px;
}

.park-form >>> .el-form-item__label {
  font-size: 13px;
  color: #606266;
}

/* 详情页 */
.park-detail >>> .el-descriptions__label {
  font-weight: 500;
  background: #FAFAFA;
  color: #606266;
  font-size: 13px;
}

.park-detail >>> .el-descriptions__content {
  color: #303133;
  font-size: 13px;
}
</style>
