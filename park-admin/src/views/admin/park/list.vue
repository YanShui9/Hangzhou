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
        <el-form-item label="所属区县">
          <el-select v-model="queryParams.districtName" placeholder="请选择区县" clearable>
            <el-option
              v-for="item in districtOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
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

    <!-- 操作栏 -->
    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增园区</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="parkList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="districtName" label="所属区县" width="120" align="center" />
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
        <el-table-column label="用地面积（亩）" width="130" align="center">
          <template slot-scope="{ row }">
            {{ row.landArea ? row.landArea.toFixed(2) : '-' }}
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
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="parkForm"
        :model="parkForm"
        :rules="parkRules"
        label-width="120px"
      >
        <el-form-item label="园区名称" prop="parkName">
          <el-input v-model="parkForm.parkName" placeholder="请输入园区名称" />
        </el-form-item>
        <el-form-item label="园区类型" prop="parkType">
          <el-select v-model="parkForm.parkType" placeholder="请选择园区类型" style="width: 100%;">
            <el-option label="制造类" :value="1" />
            <el-option label="服务类" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区县" prop="districtName">
          <el-select v-model="parkForm.districtName" placeholder="请选择区县" style="width: 100%;">
            <el-option
              v-for="item in districtOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="parkForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="已建面积（亩）" prop="buildArea">
              <el-input-number
                v-model="parkForm.buildArea"
                :min="0"
                :precision="2"
                :step="100"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用地面积（亩）" prop="landArea">
              <el-input-number
                v-model="parkForm.landArea"
                :min="0"
                :precision="2"
                :step="100"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="parkForm.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="parkForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="园区简介" prop="introduction">
          <el-input
            v-model="parkForm.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入园区简介"
          />
        </el-form-item>
        <el-form-item label="星级" prop="starLevel">
          <el-select v-model="parkForm.starLevel" placeholder="请选择星级" clearable style="width: 100%;">
            <el-option label="三星" :value="3" />
            <el-option label="四星" :value="4" />
            <el-option label="五星" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
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
        parkType: null,
        pageNum: 1,
        pageSize: 10
      },
      // 区县选项
      districtOptions: [
        '上城区', '下城区', '西湖区', '滨江区', '萧山区',
        '余杭区', '富阳区', '临安区', '临平区', '钱塘区',
        '桐庐县', '淳安县', '建德市'
      ],
      // 表格数据
      parkList: [],
      total: 0,
      loading: false,
      // 对话框
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      // 表单
      parkForm: {
        id: null,
        parkName: '',
        parkType: null,
        districtId: null,
        districtName: '',
        address: '',
        buildArea: null,
        landArea: null,
        contactName: '',
        contactPhone: '',
        introduction: '',
        starLevel: null
      },
      // 表单校验规则
      parkRules: {
        parkName: [
          { required: true, message: '请输入园区名称', trigger: 'blur' }
        ],
        districtName: [
          { required: true, message: '请选择所属区县', trigger: 'change' }
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
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        parkName: '',
        districtName: '',
        parkType: null,
        pageNum: 1,
        pageSize: 10
      }
      this.getList()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.dialogTitle = '新增园区'
      this.dialogVisible = true
      this.parkForm = {
        id: null,
        parkName: '',
        parkType: null,
        districtId: null,
        districtName: '',
        address: '',
        buildArea: null,
        landArea: null,
        contactName: '',
        contactPhone: '',
        introduction: '',
        starLevel: null
      }
      this.$nextTick(() => {
        this.$refs.parkForm && this.$refs.parkForm.clearValidate()
      })
    },
    /** 编辑按钮操作 */
    handleEdit(row) {
      this.dialogTitle = '编辑园区'
      this.dialogVisible = true
      getParkDetail(row.id).then(res => {
        this.parkForm = res.data
      })
      this.$nextTick(() => {
        this.$refs.parkForm && this.$refs.parkForm.clearValidate()
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm(`确认要删除园区"${row.parkName}"吗？`, '提示', {
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

.toolbar {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
