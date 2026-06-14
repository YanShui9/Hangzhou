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
      <el-select
        v-model="queryParams.status"
        placeholder="经营状态"
        style="width: 120px;"
        class="filter-item"
        clearable
      >
        <el-option label="在营" value="在营" />
        <el-option label="注销" value="注销" />
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
      <el-button
        class="filter-item"
        type="success"
        icon="el-icon-plus"
        @click="handleAdd"
      >
        新增企业
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
      <el-table-column label="统一社会信用代码" prop="creditCode" min-width="180" show-overflow-tooltip />
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
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(row)">
            删除
          </el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      append-to-body
    >
      <el-form
        ref="enterpriseForm"
        :model="enterpriseForm"
        :rules="enterpriseRules"
        label-width="120px"
      >
        <el-form-item label="所属园区" prop="parkId">
          <el-select
            v-model="enterpriseForm.parkId"
            placeholder="请选择所属园区"
            style="width: 100%;"
          >
            <el-option
              v-for="item in parkOptions"
              :key="item.id"
              :label="item.parkName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="enterpriseForm.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="enterpriseForm.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="行业代码" prop="industryCode">
              <el-input v-model="enterpriseForm.industryCode" placeholder="如：I65" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行业名称" prop="industryName">
              <el-input v-model="enterpriseForm.industryName" placeholder="如：软件和信息技术服务业" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="enterpriseForm.legalPerson" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资本（万元）" prop="registeredCapital">
              <el-input-number
                v-model="enterpriseForm.registeredCapital"
                :min="0"
                :precision="2"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="enterpriseForm.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="enterpriseForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="注册日期" prop="registerDate">
              <el-date-picker
                v-model="enterpriseForm.registerDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经营状态" prop="status">
              <el-select v-model="enterpriseForm.status" placeholder="请选择" style="width: 100%;">
                <el-option label="在营" value="在营" />
                <el-option label="注销" value="注销" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否参评" prop="isParticipate">
          <el-radio-group v-model="enterpriseForm.isParticipate">
            <el-radio :label="1">参评</el-radio>
            <el-radio :label="0">不参评</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="enterpriseForm.isParticipate === 0" label="不参评原因" prop="participateReason">
          <el-input v-model="enterpriseForm.participateReason" placeholder="请输入不参评原因" />
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
import { getEnterpriseList, getEnterpriseDetail, saveEnterprise, updateEnterprise, deleteEnterprise } from '@/api/enterprise'
import { getParkList } from '@/api/park'

export default {
  name: 'AdminEnterpriseList',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        enterpriseName: '',
        parkId: undefined,
        industryName: '',
        status: undefined
      },
      enterpriseList: [],
      total: 0,
      loading: false,
      parkOptions: [],
      industryOptions: [
        '软件和信息技术服务业',
        '计算机、通信和其他电子设备制造业',
        '医药制造业',
        '生态保护和环境治理业',
        '非金属矿物制品业',
        '其他'
      ],
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      enterpriseForm: {
        id: undefined,
        parkId: undefined,
        enterpriseName: '',
        creditCode: '',
        industryCode: '',
        industryName: '',
        legalPerson: '',
        registeredCapital: null,
        contactName: '',
        contactPhone: '',
        registerDate: '',
        status: '在营',
        isParticipate: 1,
        participateReason: ''
      },
      enterpriseRules: {
        parkId: [
          { required: true, message: '请选择所属园区', trigger: 'change' }
        ],
        enterpriseName: [
          { required: true, message: '请输入企业名称', trigger: 'blur' }
        ]
      }
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
        pageSize: 10,
        enterpriseName: '',
        parkId: undefined,
        industryName: '',
        status: undefined
      }
      this.getList()
    },
    handleAdd() {
      this.dialogTitle = '新增企业'
      this.dialogVisible = true
      this.enterpriseForm = {
        id: undefined,
        parkId: undefined,
        enterpriseName: '',
        creditCode: '',
        industryCode: '',
        industryName: '',
        legalPerson: '',
        registeredCapital: null,
        contactName: '',
        contactPhone: '',
        registerDate: '',
        status: '在营',
        isParticipate: 1,
        participateReason: ''
      }
      this.$nextTick(() => {
        this.$refs.enterpriseForm && this.$refs.enterpriseForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑企业'
      this.dialogVisible = true
      getEnterpriseDetail(row.id).then(response => {
        this.enterpriseForm = response.data
      })
      this.$nextTick(() => {
        this.$refs.enterpriseForm && this.$refs.enterpriseForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确认要删除企业"' + row.enterpriseName + '"吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEnterprise(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.enterpriseForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const request = this.enterpriseForm.id
            ? updateEnterprise(this.enterpriseForm)
            : saveEnterprise(this.enterpriseForm)
          request.then(() => {
            this.$message.success(this.enterpriseForm.id ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.getList()
          }).finally(() => {
            this.submitLoading = false
          })
        }
      })
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
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
