<template>
  <div class="enterprise-edit-page">
    <!-- 顶部操作区 -->
    <div class="top-bar">
      <!-- 面包屑 -->
      <div class="breadcrumb-area">
        <span class="breadcrumb-text">系统设置</span>
        <span class="breadcrumb-sep">&gt;</span>
        <span class="breadcrumb-text">企业信息</span>
        <span class="breadcrumb-sep">&gt;</span>
        <span class="breadcrumb-text active">编辑</span>
      </div>

      <!-- 右侧按钮 -->
      <div class="action-area">
        <el-button
          type="primary"
          size="small"
          :loading="submitLoading"
          @click="handleSave"
        >保存</el-button>
      </div>
    </div>

    <!-- 工商基本信息卡片 -->
    <el-card class="info-card" shadow="never" body-style="padding: 20px 24px;">
      <div class="card-title">工商基本信息</div>

      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="110px"
        class="info-form"
      >
        <!-- 第1行：企业名称、统一信用代码、所属区域、所属园区 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input
                v-model="dataForm.enterpriseName"
                placeholder="请输入企业名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="统一信用代码" prop="creditCode">
              <el-input
                v-model="dataForm.creditCode"
                placeholder="请输入统一社会信用代码"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属区域" prop="districtId">
              <el-select
                v-model="dataForm.districtId"
                placeholder="请选择所属区域"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in districtOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属园区" prop="parkId">
              <el-select
                v-model="dataForm.parkId"
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
          </el-col>
        </el-row>

        <!-- 第2行：企业地址、所属产业、企业状态、入驻时间 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="企业地址" prop="enterpriseAddress">
              <el-input
                v-model="dataForm.enterpriseAddress"
                placeholder="请输入企业地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所属产业" prop="industry">
              <el-select
                v-model="dataForm.industry"
                placeholder="请选择所属产业"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in industryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="企业状态" prop="status">
              <el-select
                v-model="dataForm.status"
                placeholder="请选择企业状态"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="入驻时间" prop="settledTime">
              <el-date-picker
                v-model="dataForm.settledTime"
                type="date"
                placeholder="请选择入驻时间"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第3行：法定代表人、联系人、联系人电话、注册资本 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input
                v-model="dataForm.legalPerson"
                placeholder="请输入法定代表人"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input
                v-model="dataForm.contactPerson"
                placeholder="请输入联系人"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="联系人电话" prop="contactPhone">
              <el-input
                v-model="dataForm.contactPhone"
                placeholder="请输入联系人电话"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="注册资本" prop="registeredCapital">
              <el-input
                v-model="dataForm.registeredCapital"
                placeholder="请输入注册资本"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第4行：注册日期 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="注册日期" prop="registerDate">
              <el-date-picker
                v-model="dataForm.registerDate"
                type="date"
                placeholder="请选择注册日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第5行：经营范围（跨4列） -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="经营范围" prop="businessScope">
              <el-input
                v-model="dataForm.businessScope"
                type="textarea"
                :rows="3"
                placeholder="请输入经营范围"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {
  getEnterpriseInfoById,
  updateEnterpriseInfo,
  saveEnterpriseInfo
} from '@/api/enterprise-info'

export default {
  name: 'EnterpriseInfoEdit',
  data() {
    return {
      submitLoading: false,
      enterpriseId: null,
      districtOptions: [
        { id: 1, name: '上城区' },
        { id: 2, name: '滨江区' },
        { id: 3, name: '萧山区' },
        { id: 4, name: '余杭区' },
        { id: 5, name: '富阳区' },
        { id: 6, name: '临安区' },
        { id: 7, name: '西湖区' }
      ],
      parkOptions: [],
      industryOptions: [
        { value: 'chip', label: '芯片' },
        { value: 'software', label: '软件' },
        { value: 'internet', label: '互联网' },
        { value: 'biomedical', label: '生物医药' },
        { value: 'new_energy', label: '新能源' },
        { value: 'new_material', label: '新材料' },
        { value: 'advanced_manufacturing', label: '先进制造' }
      ],
      statusOptions: [
        { value: 1, label: '参评' },
        { value: 2, label: '未参评' }
      ],
      dataForm: {
        enterpriseName: '',
        creditCode: '',
        districtId: null,
        parkId: null,
        enterpriseAddress: '',
        industry: null,
        status: null,
        settledTime: '',
        legalPerson: '',
        contactPerson: '',
        contactPhone: '',
        registeredCapital: '',
        registerDate: '',
        businessScope: ''
      },
      dataRules: {
        enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
        creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
        districtId: [{ required: true, message: '请选择所属区域', trigger: 'change' }],
        parkId: [{ required: true, message: '请选择所属园区', trigger: 'change' }],
        enterpriseAddress: [{ required: true, message: '请输入企业地址', trigger: 'blur' }],
        industry: [{ required: true, message: '请选择所属产业', trigger: 'change' }],
        status: [{ required: true, message: '请选择企业状态', trigger: 'change' }],
        settledTime: [{ required: true, message: '请选择入驻时间', trigger: 'change' }],
        legalPerson: [{ required: true, message: '请输入法定代表人', trigger: 'blur' }],
        contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        contactPhone: [{ required: true, message: '请输入联系人电话', trigger: 'blur' }],
        registeredCapital: [{ required: true, message: '请输入注册资本', trigger: 'blur' }],
        registerDate: [{ required: true, message: '请选择注册日期', trigger: 'change' }]
      }
    }
  },
  created() {
    this.enterpriseId = this.$route.query.id
    if (this.enterpriseId) {
      this.getDetail()
    }
  },
  methods: {
    async getDetail() {
      try {
        const res = await getEnterpriseInfoById(this.enterpriseId)
        const data = res.data
        this.dataForm = {
          enterpriseName: data.enterpriseName || '',
          creditCode: data.creditCode || '',
          districtId: data.districtId || null,
          parkId: data.parkId || null,
          enterpriseAddress: data.enterpriseAddress || '',
          industry: data.industry || null,
          status: data.status || null,
          settledTime: data.settledTime || '',
          legalPerson: data.legalPerson || '',
          contactPerson: data.contactPerson || '',
          contactPhone: data.contactPhone || '',
          registeredCapital: data.registeredCapital || '',
          registerDate: data.registerDate || '',
          businessScope: data.businessScope || ''
        }
      } catch (e) {
        console.error('获取企业信息详情失败', e)
      }
    },

    handleSave() {
      this.$refs.dataForm.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          if (this.enterpriseId) {
            await updateEnterpriseInfo({ id: this.enterpriseId, ...this.dataForm })
            this.$message.success('修改成功')
          } else {
            await saveEnterpriseInfo(this.dataForm)
            this.$message.success('新增成功')
          }
          this.$router.push({ path: '/system/enterprise-info' })
        } catch (e) {
          console.error('保存企业信息失败', e)
        } finally {
          this.submitLoading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.enterprise-edit-page {
  padding: 20px;
}

/* 顶部操作栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 4px 16px 4px;
}

.breadcrumb-area {
  display: flex;
  align-items: center;
}

.breadcrumb-text {
  font-size: 13px;
  color: #909399;
}

.breadcrumb-text.active {
  color: #303133;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #c0c4cc;
  font-size: 13px;
}

/* 工商基本信息卡片 */
.info-card {
  border-radius: 4px;
  margin-bottom: 0;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

/* 表单 */
.info-form {
  padding: 8px 0 0 0;
}

.info-form >>> .el-form-item {
  margin-bottom: 22px;
}

.info-form >>> .el-form-item__label {
  font-size: 14px;
  color: #606266;
}

/* 必填星号样式 */
.info-form >>> .el-form-item.is-required .el-form-item__label::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
  font-weight: bold;
}
</style>
