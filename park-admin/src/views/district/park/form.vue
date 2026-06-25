<template>
  <div class="park-form-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span class="breadcrumb-item">园区列表</span>
      <span class="breadcrumb-separator">></span>
      <span class="breadcrumb-item active">{{ isEdit ? '编辑园区' : '新增园区' }}</span>
    </div>

    <!-- 主内容卡片 -->
    <el-card class="main-card" shadow="never">
      <!-- 卡片头部 -->
      <div class="card-header">
        <div class="header-left">
          <h2 class="page-title">{{ isEdit ? '编辑园区' : '新增园区' }}</h2>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </div>
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" type="card">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="section-container">
            <h3 class="section-title">基本信息</h3>
            <div class="info-grid">
              <!-- 第一列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">园区名称</label>
                  <el-input v-model="formData.parkName" placeholder="请输入" class="info-input" />
                </div>
                <div class="info-row">
                  <label class="info-label">是否升级改造</label>
                  <el-select v-model="formData.isUpgrade" placeholder="请选择" class="info-select">
                    <el-option label="是" value="是" />
                    <el-option label="否" value="否" />
                  </el-select>
                </div>
                <div class="info-row">
                  <label class="info-label">所属区域</label>
                  <el-select v-model="formData.district" placeholder="请选择" class="info-select">
                    <el-option label="上城区" value="上城区" />
                    <el-option label="下城区" value="下城区" />
                    <el-option label="西湖区" value="西湖区" />
                    <el-option label="拱墅区" value="拱墅区" />
                    <el-option label="江干区" value="江干区" />
                    <el-option label="滨江区" value="滨江区" />
                    <el-option label="萧山区" value="萧山区" />
                    <el-option label="余杭区" value="余杭区" />
                  </el-select>
                </div>
              </div>

              <!-- 第二列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">园区状态</label>
                  <el-select v-model="formData.parkStatus" placeholder="请选择" class="info-select">
                    <el-option label="已投运" value="已投运" />
                    <el-option label="建设中" value="建设中" />
                    <el-option label="规划中" value="规划中" />
                  </el-select>
                </div>
                <div class="info-row">
                  <label class="info-label">改造提升内容</label>
                  <el-input v-model="formData.upgradeContent" placeholder="请输入" class="info-input" />
                </div>
                <div class="info-row">
                  <label class="info-label">园区类型</label>
                  <el-select v-model="formData.parkType" placeholder="请选择" class="info-select">
                    <el-option label="生产性制造类" value="生产性制造类" />
                    <el-option label="生产性服务类" value="生产性服务类" />
                  </el-select>
                </div>
              </div>

              <!-- 第三列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">土地性质</label>
                  <el-select v-model="formData.landNature" placeholder="请选择" class="info-select">
                    <el-option label="工业用地" value="工业用地" />
                    <el-option label="商业用地" value="商业用地" />
                    <el-option label="综合用地" value="综合用地" />
                  </el-select>
                </div>
                <div class="info-row">
                  <label class="info-label">开发模式</label>
                  <el-select v-model="formData.devMode" placeholder="请选择开发模式" class="info-input">
                    <el-option label="政府主导开发" value="政府主导开发" />
                    <el-option label="工业地产开发" value="工业地产开发" />
                    <el-option label="企业联合开发" value="企业联合开发" />
                    <el-option label="龙头企业开发" value="龙头企业开发" />
                    <el-option label="专业机构开发" value="专业机构开发" />
                    <el-option label="村集体联合开发" value="村集体联合开发" />
                    <el-option label="政企合作" value="政企合作" />
                  </el-select>
                </div>
              </div>

              <!-- 第四列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">园区地址</label>
                  <el-input v-model="formData.address" placeholder="请输入" class="info-input" />
                </div>
                <div class="info-row">
                  <label class="info-label">土地来源</label>
                  <el-input v-model="formData.landSource" placeholder="请输入" class="info-input" />
                </div>
              </div>
            </div>
          </div>

          <!-- 联系方式 -->
          <div class="section-container">
            <h3 class="section-title">联系方式</h3>
            <div class="info-grid">
              <!-- 第一列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">运营单位</label>
                  <el-input v-model="formData.operator" placeholder="请输入" class="info-input" />
                </div>
                <div class="info-row">
                  <label class="info-label">联系人</label>
                  <el-input v-model="formData.contactPerson" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第二列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">运营性质</label>
                  <el-select v-model="formData.operationNature" placeholder="请选择" class="info-select">
                    <el-option label="国有企业" value="国有企业" />
                    <el-option label="民营企业" value="民营企业" />
                    <el-option label="事业单位" value="事业单位" />
                  </el-select>
                </div>
                <div class="info-row">
                  <label class="info-label">联系人电话</label>
                  <el-input v-model="formData.contactPersonPhone" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第三列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">负责人</label>
                  <el-input v-model="formData.contactName" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第四列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">负责人电话</label>
                  <el-input v-model="formData.contactPhone" placeholder="请输入" class="info-input" />
                </div>
              </div>
            </div>
          </div>

          <!-- 园区面积 -->
          <div class="section-container">
            <h3 class="section-title">园区面积</h3>
            <div class="info-grid">
              <!-- 第一列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">实际用地面积（亩）</label>
                  <el-input v-model.number="formData.actualLandArea" placeholder="请输入" class="info-input" />
                </div>
                <div class="info-row">
                  <label class="info-label">园区剩余可售面积（平方米）</label>
                  <el-input v-model.number="formData.sellableArea" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第二列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">已建建筑面积（平方米）</label>
                  <el-input v-model.number="formData.buildArea" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第三列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">园区已租面积（平方米）</label>
                  <el-input v-model.number="formData.rentedArea" placeholder="请输入" class="info-input" />
                </div>
              </div>

              <!-- 第四列 -->
              <div class="info-column">
                <div class="info-row">
                  <label class="info-label">园区剩余可租面积（平方米）</label>
                  <el-input v-model.number="formData.availableArea" placeholder="请输入" class="info-input" />
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 园区简介 -->
        <el-tab-pane label="园区简介" name="introduction">
          <!-- 园区介绍 -->
          <div class="introduction-section">
            <h3 class="section-title">园区介绍</h3>
            <div class="section-content">
              <el-input
                v-model="formData.introduction"
                type="textarea"
                :rows="8"
                placeholder="请输入园区介绍"
                class="intro-textarea"
              />
            </div>
          </div>

          <!-- 园区图片 -->
          <div class="introduction-section">
            <h3 class="section-title">园区图片</h3>
            <div class="section-content">
              <div class="image-upload-container">
                <div v-if="displayImages.length === 0" class="image-empty">
                  <i class="el-icon-picture-outline"></i>
                  <p>暂无园区图片</p>
                </div>
                <div v-else class="image-list">
                  <div v-for="(image, index) in displayImages" :key="index" class="image-item">
                    <el-image
                      :src="getImageUrl(image)"
                      :preview-src-list="imageUrlList"
                      :initial-index="index"
                      fit="cover"
                      class="image-preview"
                      @error="handleImageError(index)"
                    />
                    <div class="image-delete" @click.stop="handleDeleteImage(index)">
                      <i class="el-icon-close"></i>
                    </div>
                  </div>
                  <div v-if="formData.parkImages.length < 6" class="image-add" @click="handleImageUpload">
                    <i class="el-icon-plus"></i>
                  </div>
                </div>
                <div class="image-tips">最多上传6张图片，支持jpg、jpeg、png格式，单张图片不超过10MB</div>
              </div>
            </div>
          </div>

          <!-- 公共配套设施 -->
          <div class="introduction-section">
            <h3 class="section-title">公共配套设施</h3>
            <div class="section-content">
              <el-input
                v-model="formData.publicFacilities"
                type="textarea"
                :rows="4"
                placeholder="请输入公共配套设施"
                class="intro-textarea"
              />
            </div>
          </div>

          <!-- 公共配套服务 -->
          <div class="introduction-section">
            <h3 class="section-title">公共配套服务</h3>
            <div class="section-content">
              <el-input
                v-model="formData.publicServices"
                type="textarea"
                :rows="4"
                placeholder="请输入公共配套服务"
                class="intro-textarea"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 入驻企业数据 -->
      <div class="section-container">
        <div class="section-header">
          <h3 class="section-title">入驻企业</h3>
          <span class="tip-text">*数据由系统统计生成，不可修改</span>
        </div>
        <div class="data-grid">
          <div class="data-item">
            <span class="data-value">{{ formData.enterpriseCount }}</span>
            <span class="data-label">入驻企业总数（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.aboveScaleCount }}</span>
            <span class="data-label">规模以上企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.highTechCount }}</span>
            <span class="data-label">高新技术企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.smeCount }}</span>
            <span class="data-label">科技型中小企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.demonstrationCount }}</span>
            <span class="data-label">隐形冠军及培育企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.nationalSmeCount }}</span>
            <span class="data-label">国家专精特新“小巨人”企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.innovationCount }}</span>
            <span class="data-label">创新型中小企业（家）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.provincialSmeCount }}</span>
            <span class="data-label">省专精特新中小企业（家）</span>
          </div>
        </div>
      </div>

      <!-- 入驻员工数据 -->
      <div class="section-container">
        <div class="section-header">
          <h3 class="section-title">入驻员工</h3>
          <span class="tip-text">*数据由系统统计生成，不可修改</span>
        </div>
        <div class="data-grid">
          <div class="data-item">
            <span class="data-value">{{ formData.employeeCount }}</span>
            <span class="data-label">入驻企业员工总数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.coreTalentCount }}</span>
            <span class="data-label">“国千”人才人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.provincialTalentCount }}</span>
            <span class="data-label">“省千”人才人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.seniorEngineerCount }}</span>
            <span class="data-label">正高级工程师人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.engineerCount }}</span>
            <span class="data-label">高级工程师人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.highTechPersonCount }}</span>
            <span class="data-label">高端技术人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.phdCount }}</span>
            <span class="data-label">硕士及副高以上人数（人）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.masterCount }}</span>
            <span class="data-label">硕士以上人数（人）</span>
          </div>
        </div>
      </div>

      <!-- 创新专利数据 -->
      <div class="section-container">
        <div class="section-header">
          <h3 class="section-title">创新专利</h3>
          <span class="tip-text">*数据由系统统计生成，不可修改</span>
        </div>
        <div class="data-grid">
          <div class="data-item">
            <span class="data-value">{{ formData.patentCount }}</span>
            <span class="data-label">专利拥有量（件）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.inventionCount }}</span>
            <span class="data-label">发明专利（件）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.utilityModelCount }}</span>
            <span class="data-label">实用新型专利（件）</span>
          </div>
          <div class="data-item">
            <span class="data-value">{{ formData.designCount }}</span>
            <span class="data-label">外观设计专利（件）</span>
          </div>
        </div>
      </div>
    </el-card>

    <input ref="parkImageInput" type="file" multiple accept="image/jpeg,image/png,image/jpg" style="display:none" @change="handleImageFileChange" />
  </div>
</template>

<script>
import { savePark, updatePark, getParkDetail } from '@/api/park'
import { uploadFile, deleteFile } from '@/api/tech-innovation'

export default {
  name: 'ParkForm',
  data() {
    return {
      activeTab: 'basic',
      isEdit: false,
      parkId: null,
      formData: {
        parkName: '',
        parkStatus: null,
        landNature: null,
        address: '',
        isUpgrade: null,
        upgradeContent: '',
        devMode: '',
        landSource: '',
        district: '',
        parkType: '',
        introduction: '',
        parkImages: [],
        publicFacilities: '',
        publicServices: '',
        actualLandArea: null,
        buildArea: null,
        rentedArea: null,
        availableArea: null,
        sellableArea: null,
        operator: '',
        operationNature: null,
        contactName: '',
        contactPhone: '',
        contactPerson: '',
        contactPersonPhone: '',
        // 入驻企业数据
        enterpriseCount: 256,
        aboveScaleCount: 48,
        highTechCount: 56,
        smeCount: 101,
        demonstrationCount: 18,
        nationalSmeCount: 12,
        innovationCount: 8,
        provincialSmeCount: 15,
        // 入驻员工数据
        employeeCount: 2856,
        coreTalentCount: 49,
        provincialTalentCount: 85,
        seniorEngineerCount: 98,
        engineerCount: 142,
        highTechPersonCount: 356,
        phdCount: 408,
        masterCount: 408,
        // 创新专利数据
        patentCount: 625,
        inventionCount: 125,
        utilityModelCount: 300,
        designCount: 200
      }
    }
  },
  computed: {
    displayImages() {
      if (this.formData.parkImages && this.formData.parkImages.length > 0) {
        return this.formData.parkImages
      }
      return []
    },
    imageUrlList() {
      return this.displayImages.map(img => this.getImageUrl(img))
    }
  },
  created() {
    const path = this.$route.path
    if (path.includes('/edit/')) {
      this.isEdit = true
      this.parkId = parseInt(this.$route.params.id)
      this.loadParkData()
    }
  },
  methods: {
    loadParkData() {
      getParkDetail(this.parkId).then(res => {
        const data = res.data
        // parkImages 后端存 JSON 字符串，前端需解析为数组
        let parkImages = []
        if (data.parkImages) {
          try {
            const parsed = typeof data.parkImages === 'string' ? JSON.parse(data.parkImages) : data.parkImages
            parkImages = Array.isArray(parsed) ? parsed : []
          } catch (e) {
            parkImages = []
          }
        }
        this.formData = {
          ...this.formData,
          ...data,
          parkImages
        }
      })
    },
    handleSubmit() {
      if (!this.formData.parkName) {
        this.$message.warning('请输入园区名称')
        return
      }

      // 前端字段 → 后端字段映射
      const params = {
        ...this.formData,
        districtName: this.formData.districtName || this.formData.district,
        isUpgradable: this.formData.isUpgradable || this.formData.isUpgrade,
        landArea: this.formData.landArea || this.formData.actualLandArea,
        leasedArea: this.formData.leasedArea || this.formData.rentedArea,
        remainingLeasableArea: this.formData.remainingLeasableArea || this.formData.availableArea,
        remainingSellableArea: this.formData.remainingSellableArea || this.formData.sellableArea,
        operatorUnit: this.formData.operatorUnit || this.formData.operator,
        operatorNature: this.formData.operatorNature || this.formData.operationNature,
        personInCharge: this.formData.personInCharge || this.formData.contactName,
        inChargePhone: this.formData.inChargePhone || this.formData.contactPhone,
        contactPerson: this.formData.contactPerson || this.formData.contactPerson,
        contactPhone: this.formData.contactPhone || this.formData.contactPersonPhone,
        parkImages: this.formData.parkImages ? (typeof this.formData.parkImages === 'string' ? this.formData.parkImages : JSON.stringify(this.formData.parkImages)) : null
      }
      if (this.isEdit) {
        params.id = this.parkId
      }

      const api = this.isEdit ? updatePark : savePark
      api(params).then(() => {
        this.$message.success(this.isEdit ? '修改成功' : '新增成功')
        this.$router.push('/district/park')
      }).catch(() => {
        this.$message.error(this.isEdit ? '修改失败' : '新增失败')
      })
    },
    getImageUrl(image) {
      if (!image) return ''
      let url = ''
      if (typeof image === 'string') {
        url = image
      } else if (image.url) {
        url = image.url
      } else if (image.fileUrl) {
        url = image.fileUrl
      }
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      if (url.startsWith('/api/')) {
        return url
      }
      if (url.startsWith('/')) {
        return url
      }
      return '/api/files/preview/' + url
    },
    handleImageError(index) {
      console.warn('图片加载失败，索引:', index)
    },
    handleImageUpload() {
      this.$refs.parkImageInput.click()
    },
    async handleImageFileChange(event) {
      const files = Array.from(event.target.files || [])
      if (files.length === 0) return
      const remaining = 6 - this.formData.parkImages.length
      if (remaining <= 0) {
        this.$message.warning('最多上传6张图片')
        event.target.value = ''
        return
      }
      const toUpload = files.slice(0, remaining)
      try {
        for (const file of toUpload) {
          const formData = new FormData()
          formData.append('file', file)
          formData.append('bizType', 'park_image')
          const res = await uploadFile(formData)
          if (res.data && res.data.url) {
            this.formData.parkImages.push({
              url: res.data.url,
              name: res.data.name || file.name,
              id: res.data.id
            })
          }
        }
        this.$message.success('图片上传成功')
      } catch (e) {
        console.error('图片上传失败', e)
        this.$message.error('图片上传失败')
      } finally {
        event.target.value = ''
      }
    },
    async handleDeleteImage(index) {
      try {
        const image = this.formData.parkImages[index]
        if (image && image.id) {
          await deleteFile(image.id)
        }
        this.formData.parkImages.splice(index, 1)
        this.$message.success('删除成功')
      } catch (e) {
        console.error('删除图片失败', e)
        this.$message.error('删除失败')
      }
    }
  }
}
</script>

<style scoped>
.park-form-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}

.breadcrumb-item {
  color: #909399;
}

.breadcrumb-item.active {
  color: #303133;
  font-weight: 500;
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #c0c4cc;
}

.main-card {
  position: relative;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.header-right {
  display: flex;
  gap: 8px;
}

.section-container {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 16px 0;
}

.section-header .section-title {
  margin-bottom: 0;
}

.tip-text {
  font-size: 12px;
  color: #909399;
}

.info-grid {
  display: flex;
  gap: 24px;
}

.info-column {
  flex: 1;
}

.info-row {
  display: flex;
  flex-direction: column;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.info-label::before {
  content: '';
  width: 4px;
  height: 4px;
  background: #d9d9d9;
  border-radius: 50%;
  margin-right: 8px;
}

.info-input,
.info-select {
  width: 100%;
}

.intro-textarea {
  margin-top: 8px;
}

.intro-textarea .el-textarea {
  width: 100%;
}

.data-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.data-item {
  width: calc(25% - 12px);
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
}

.data-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.data-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

@media screen and (max-width: 1200px) {
  .info-grid {
    flex-wrap: wrap;
  }

  .info-column {
    width: calc(50% - 12px);
  }

  .data-item {
    width: calc(33.33% - 11px);
  }
}

@media screen and (max-width: 768px) {
  .info-column {
    width: 100%;
  }

  .data-item {
    width: calc(50% - 8px);
  }
}

/* 园区简介样式 */
.introduction-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.introduction-section .section-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ebeef5;
}

.section-content {
  margin-top: 8px;
}

.introduction-section .intro-textarea {
  margin-top: 0;
}

.introduction-section .intro-textarea .el-textarea {
  width: 100%;
}

.introduction-section .el-textarea__inner {
  border-radius: 4px;
  padding: 12px;
  font-size: 14px;
}

/* 图片上传区域 */
.image-upload-container {
  min-height: 180px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 140px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  color: #909399;
}

.image-empty i {
  font-size: 40px;
  margin-bottom: 8px;
}

.image-empty p {
  font-size: 12px;
  margin: 0;
}

.image-item {
  position: relative;
  width: 140px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-delete {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  font-size: 14px;
  transition: background 0.2s;
}

.image-delete:hover {
  background: rgba(245, 108, 108, 0.8);
}

.image-add {
  width: 140px;
  height: 140px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.2s;
}

.image-add:hover {
  border-color: #409eff;
}

.image-add .el-icon-plus {
  font-size: 28px;
  color: #c0c4cc;
}

.image-add:hover .el-icon-plus {
  color: #409eff;
}

.image-tips {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
