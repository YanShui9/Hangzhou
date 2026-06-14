<template>
  <div class="park-mine-container">
    <div class="page-header">
      <el-tabs v-model="activeTab" class="tab-nav">
        <el-tab-pane label="基本信息" name="basic">
        </el-tab-pane>
        <el-tab-pane label="园区简介" name="intro">
        </el-tab-pane>
        <el-tab-pane label="运营数据" name="stats">
        </el-tab-pane>
      </el-tabs>
      <el-button type="primary" class="save-btn" @click="handleSave">保存</el-button>
    </div>

    <div v-show="activeTab === 'basic'" class="tab-content">
      <div class="info-section">
        <div class="section-title">基本信息</div>
        <div class="info-grid">
          <div class="info-item">
            <label class="required">园区名称</label>
            <span>{{ parkInfo.parkName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>园区状态</label>
            <span>{{ parkInfo.status === 1 ? '已运营' : parkInfo.status === 2 ? '建设中' : '-' }}</span>
          </div>
          <div class="info-item">
            <label>土地性质</label>
            <span>{{ parkInfo.landNature || '-' }}</span>
          </div>
          <div class="info-item">
            <label>星级评定</label>
            <span>{{ parkInfo.starLevel ? parkInfo.starLevel + '星级园区' : '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">园区地址</label>
            <span>{{ parkInfo.address || '-' }}</span>
          </div>
          <div class="info-item">
            <label>是否升级改造</label>
            <span>{{ parkInfo.isUpgrade === 1 ? '是' : parkInfo.isUpgrade === 0 ? '否' : '-' }}</span>
          </div>
          <div class="info-item">
            <label>改造提升内容</label>
            <span>{{ parkInfo.upgradeContent || '-' }}</span>
          </div>
          <div class="info-item">
            <label>开发模式</label>
            <span>{{ parkInfo.devMode || '-' }}</span>
          </div>
          <div class="info-item">
            <label>土地来源</label>
            <span>{{ parkInfo.landSource || '-' }}</span>
          </div>
          <div class="info-item">
            <label>所属区域</label>
            <span>{{ parkInfo.districtName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>园区类型</label>
            <span>{{ parkInfo.parkTypeName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>主导产业</label>
            <span>{{ parkInfo.mainIndustry || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">联系方式</div>
        <div class="info-grid">
          <div class="info-item">
            <label class="required">运营单位</label>
            <el-input v-model="formData.operatingCompany" placeholder="请输入运营单位" />
          </div>
          <div class="info-item">
            <label>运营性质</label>
            <el-select v-model="formData.operatingNature" placeholder="请选择运营性质">
              <el-option label="国有企业" value="国有企业" />
              <el-option label="民营企业" value="民营企业" />
              <el-option label="外资企业" value="外资企业" />
              <el-option label="其他" value="其他" />
            </el-select>
          </div>
          <div class="info-item">
            <label class="required">负责人</label>
            <el-input v-model="formData.responsiblePerson" placeholder="请输入负责人" />
          </div>
          <div class="info-item">
            <label>负责人电话</label>
            <el-input v-model="formData.responsiblePhone" placeholder="请输入负责人电话" />
          </div>
          <div class="info-item">
            <label>联系人</label>
            <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
          </div>
          <div class="info-item">
            <label>联系人电话</label>
            <el-input v-model="formData.contactPhone" placeholder="请输入联系人电话" />
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">园区面积</div>
        <div class="info-grid">
          <div class="info-item">
            <label>园区总面积（亩）</label>
            <el-input v-model.number="formData.totalArea" placeholder="请输入园区总面积" />
          </div>
          <div class="info-item">
            <label>实际用地面积（亩）</label>
            <el-input v-model.number="formData.actualLandArea" placeholder="请输入实际用地面积" />
          </div>
          <div class="info-item">
            <label>已建建筑面积（平方米）</label>
            <el-input v-model.number="formData.constructedArea" placeholder="请输入已建建筑面积" />
          </div>
          <div class="info-item">
            <label>园区已租面积（平方米）</label>
            <el-input v-model.number="formData.rentedArea" placeholder="请输入已租面积" />
          </div>
          <div class="info-item">
            <label>园区剩余可租面积（平方米）</label>
            <el-input v-model.number="formData.availableArea" placeholder="请输入剩余可租面积" />
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">入驻企业</div>
        <div class="info-grid">
          <div class="info-item">
            <label>入驻企业总数（家）</label>
            <span>{{ stats.enterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>规模以上企业（家）</label>
            <span>{{ stats.largeEnterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高新技术企业（家）</label>
            <span>{{ stats.highTechEnterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>科技型中小企业（家）</label>
            <span>{{ stats.smeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>入驻企业总数（家）</label>
            <span>{{ stats.enterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>规模以上企业（家）</label>
            <span>{{ stats.largeEnterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>创新型中小企业（家）</label>
            <span>{{ stats.innovativeSmeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>国家专精特新中小企业（家）</label>
            <span>{{ stats.specializedSmeCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">入驻员工</div>
        <div class="info-grid">
          <div class="info-item">
            <label>入驻企业员工总数（人）</label>
            <span>{{ stats.employeeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>"国千"人才人数（人）</label>
            <span>{{ stats.nationalTalentCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>"省千"人才人数（人）</label>
            <span>{{ stats.provincialTalentCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>正高级工程师人数（人）</label>
            <span>{{ stats.seniorEngineerCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级工程师人数（人）</label>
            <span>{{ stats.engineerCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级技师人数（人）</label>
            <span>{{ stats.seniorTechnicianCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>硕士及副高以上人数（人）</label>
            <span>{{ stats.masterCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>博士以上人数（人）</label>
            <span>{{ stats.doctorCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">创新专利</div>
        <div class="info-grid">
          <div class="info-item">
            <label>专利拥有量（件）</label>
            <span>{{ stats.patentCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>发明专利（件）</label>
            <span>{{ stats.inventionPatentCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>实用新型专利（件）</label>
            <span>{{ stats.utilityModelCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>外观设计专利（件）</label>
            <span>{{ stats.designPatentCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'intro'" class="tab-content">
      <div class="intro-section">
        <div class="section-title">园区介绍</div>
        <div class="form-item">
          <label class="required">园区介绍</label>
          <el-input
            type="textarea"
            v-model="formData.introduction"
            :rows="6"
            placeholder="请输入园区介绍..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">园区图片</div>
        <div class="form-item">
          <label class="required">园区图片</label>
          <div class="upload-area" @click="triggerUpload">
            <div class="upload-icon">
              <i class="el-icon-plus"></i>
            </div>
            <div class="upload-text">点击上传图片</div>
            <input type="file" ref="uploadInput" class="upload-input" accept="image/*" @change="handleImageUpload" />
          </div>
          <div v-if="formData.parkImage" class="image-preview">
            <img :src="formData.parkImage" alt="园区图片" />
            <button class="remove-image" @click="removeImage">
              <i class="el-icon-delete"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">公共配套设施</div>
        <div class="form-item">
          <label>公共配套设施</label>
          <el-input
            type="textarea"
            v-model="formData.publicFacilities"
            :rows="6"
            placeholder="请输入公共配套设施..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">公共配套服务</div>
        <div class="form-item">
          <label>公共配套服务</label>
          <el-input
            type="textarea"
            v-model="formData.publicServices"
            :rows="6"
            placeholder="请输入公共配套服务..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'stats'" class="tab-content">
      <div class="stats-section">
        <div class="section-title">运营数据统计</div>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-value">{{ stats.enterpriseCount || 0 }}</div>
            <div class="stat-label">入驻企业数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.employeeCount || 0 }}</div>
            <div class="stat-label">员工总数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.patentCount || 0 }}</div>
            <div class="stat-label">专利总数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.highTechEnterpriseCount || 0 }}</div>
            <div class="stat-label">高新技术企业</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getParkDetail, getParkStats, updatePark } from '@/api/park'
import { mapGetters } from 'vuex'

export default {
  name: 'ParkMine',
  data() {
    return {
      loading: false,
      activeTab: 'basic',
      parkInfo: {},
      stats: {},
      formData: {
        operatingCompany: '',
        operatingNature: '',
        responsiblePerson: '',
        responsiblePhone: '',
        contactPerson: '',
        contactPhone: '',
        totalArea: '',
        actualLandArea: '',
        constructedArea: '',
        rentedArea: '',
        availableArea: '',
        introduction: '',
        parkImage: '',
        publicFacilities: '',
        publicServices: ''
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.getParkInfo()
    this.getParkStatistics()
  },
  methods: {
    async getParkInfo() {
      const parkId = this.userInfo.parkId
      if (!parkId) {
        this.$message.warning('未关联园区，请联系管理员')
        return
      }
      this.loading = true
      try {
        const res = await getParkDetail(parkId)
        this.parkInfo = res.data || {}
        this.initFormData()
      } catch (error) {
        this.$message.error('获取园区信息失败')
      } finally {
        this.loading = false
      }
    },

    async getParkStatistics() {
      const parkId = this.userInfo.parkId
      if (!parkId) return
      try {
        const res = await getParkStats(parkId)
        this.stats = res.data || {}
      } catch (error) {
        console.error('获取园区统计数据失败:', error)
      }
    },

    initFormData() {
      this.formData = {
        operatingCompany: this.parkInfo.operatingCompany || '',
        operatingNature: this.parkInfo.operatingNature || '',
        responsiblePerson: this.parkInfo.responsiblePerson || '',
        responsiblePhone: this.parkInfo.responsiblePhone || '',
        contactPerson: this.parkInfo.contactPerson || '',
        contactPhone: this.parkInfo.contactPhone || '',
        totalArea: this.parkInfo.totalArea || '',
        actualLandArea: this.parkInfo.actualLandArea || '',
        constructedArea: this.parkInfo.constructedArea || '',
        rentedArea: this.parkInfo.rentedArea || '',
        availableArea: this.parkInfo.availableArea || '',
        introduction: this.parkInfo.introduction || '',
        parkImage: this.parkInfo.parkImage || '',
        publicFacilities: this.parkInfo.publicFacilities || '',
        publicServices: this.parkInfo.publicServices || ''
      }
    },

    triggerUpload() {
      this.$refs.uploadInput.click()
    },

    handleImageUpload(event) {
      const file = event.target.files[0]
      if (!file) return
      if (!file.type.startsWith('image/')) {
        this.$message.error('请选择图片文件')
        return
      }
      const reader = new FileReader()
      reader.onload = (e) => {
        this.formData.parkImage = e.target.result
      }
      reader.readAsDataURL(file)
    },

    removeImage() {
      this.formData.parkImage = ''
      this.$refs.uploadInput.value = ''
    },

    async handleSave() {
      const parkId = this.userInfo.parkId
      if (!parkId) {
        this.$message.warning('未关联园区，请联系管理员')
        return
      }
      try {
        await updatePark({
          id: parkId,
          ...this.formData
        })
        this.$message.success('保存成功')
      } catch (error) {
        this.$message.error('保存失败')
      }
    }
  }
}
</script>

<style scoped>
.park-mine-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tab-nav {
  flex: 1;
}

.save-btn {
  margin-left: 20px;
}

.tab-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 24px;
}

.info-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.info-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.info-item label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.info-item span {
  font-size: 14px;
  color: #303133;
  line-height: 24px;
}

.info-item .el-input,
.info-item .el-select {
  width: 100%;
}

.intro-section {
  margin-bottom: 28px;
  padding-bottom: 28px;
  border-bottom: 1px solid #f0f0f0;
}

.intro-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.form-item {
  display: flex;
  flex-direction: column;
}

.form-item label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.form-item label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.intro-input {
  width: 100%;
}

.intro-input /deep/ .el-textarea__inner {
  border-radius: 8px;
  resize: vertical;
  min-height: 120px !important;
}

.upload-area {
  width: 100%;
  height: 180px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.upload-area:hover {
  border-color: #409eff;
  background: #f0f5ff;
}

.upload-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e8f4ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.upload-icon i {
  font-size: 24px;
  color: #409eff;
}

.upload-text {
  font-size: 14px;
  color: #909399;
}

.upload-input {
  display: none;
}

.image-preview {
  margin-top: 16px;
  position: relative;
  max-width: 400px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview img {
  width: 100%;
  height: auto;
  display: block;
}

.remove-image {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-image:hover {
  background: rgba(0, 0, 0, 0.8);
}

.remove-image i {
  font-size: 16px;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: white;
}

.stat-card .stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-card .stat-label {
  font-size: 14px;
  opacity: 0.9;
}

@media screen and (max-width: 1200px) {
  .info-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 900px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 600px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  .save-btn {
    margin-left: 0;
    width: 100%;
  }
}
</style>