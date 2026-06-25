<template>
  <div class="park-mine-container">
    <div class="page-header">
      <el-tabs v-model="activeTab" class="tab-nav">
        <el-tab-pane label="基本信息" name="basic">
        </el-tab-pane>
        <el-tab-pane label="园区简介" name="intro">
        </el-tab-pane>
      </el-tabs>
      <el-button v-if="!isEditing" type="text" class="edit-btn" @click="handleEdit">编辑</el-button>
      <template v-else>
        <el-button type="primary" class="save-btn" @click="handleSave">保存</el-button>
        <el-button type="text" class="cancel-btn" @click="handleCancel">取消</el-button>
      </template>
    </div>

    <div class="tab-scroll-container">
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
            <span>{{ parkInfo.parkStatus || '-' }}</span>
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
            <el-input v-if="isEditing" v-model="formData.address" placeholder="请输入园区地址" />
            <span v-else>{{ parkInfo.address || '-' }}</span>
          </div>
          <div class="info-item">
            <label>是否升级改造</label>
            <el-select v-if="isEditing" v-model="formData.isUpgradable" placeholder="请选择">
              <el-option label="是" value="是" />
              <el-option label="否" value="否" />
            </el-select>
            <span v-else>{{ parkInfo.isUpgradable || '-' }}</span>
          </div>
          <div class="info-item">
            <label>改造提升内容</label>
            <el-input v-if="isEditing" v-model="formData.upgradeContent" placeholder="请输入改造提升内容" />
            <span v-else>{{ parkInfo.upgradeContent || '-' }}</span>
          </div>
          <div class="info-item">
            <label>开发模式</label>
            <el-input v-if="isEditing" v-model="formData.devMode" placeholder="请输入开发模式" />
            <span v-else>{{ parkInfo.devMode || '-' }}</span>
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
            <span>{{ parkInfo.parkType || '-' }}</span>
          </div>
          <div class="info-item">
            <label>主导产业</label>
            <span>{{ parkInfo.leadingIndustry || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">联系方式</div>
        <div class="info-grid">
          <div class="info-item">
            <label class="required">运营单位</label>
            <el-input v-if="isEditing" v-model="formData.operatorUnit" placeholder="请输入运营单位" />
            <span v-else>{{ parkInfo.operatorUnit || '-' }}</span>
          </div>
          <div class="info-item">
            <label>运营性质</label>
            <el-select v-if="isEditing" v-model="formData.operatorNature" placeholder="请选择运营性质">
              <el-option label="国有企业" value="国有企业" />
              <el-option label="民营企业" value="民营企业" />
              <el-option label="事业单位" value="事业单位" />
              <el-option label="其他" value="其他" />
            </el-select>
            <span v-else>{{ parkInfo.operatorNature || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">负责人</label>
            <el-input v-if="isEditing" v-model="formData.personInCharge" placeholder="请输入负责人" />
            <span v-else>{{ parkInfo.personInCharge || '-' }}</span>
          </div>
          <div class="info-item">
            <label>负责人电话</label>
            <el-input v-if="isEditing" v-model="formData.inChargePhone" placeholder="请输入负责人电话" />
            <span v-else>{{ parkInfo.inChargePhone || '-' }}</span>
          </div>
          <div class="info-item">
            <label>联系人</label>
            <el-input v-if="isEditing" v-model="formData.contactPerson" placeholder="请输入联系人" />
            <span v-else>{{ parkInfo.contactPerson || '-' }}</span>
          </div>
          <div class="info-item">
            <label>联系人电话</label>
            <el-input v-if="isEditing" v-model="formData.contactPhone" placeholder="请输入联系人电话" />
            <span v-else>{{ parkInfo.contactPhone || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">园区面积</div>
        <div class="info-grid">
          <div class="info-item">
            <label>园区总面积（亩）</label>
            <span>{{ parkInfo.landArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label>实际用地面积（亩）</label>
            <span>{{ parkInfo.buildArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label>已建建筑面积（平方米）</label>
            <span>{{ parkInfo.buildArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">园区已租面积（平方米）</label>
            <el-input v-if="isEditing" v-model.number="formData.leasedArea" placeholder="请输入已租面积" />
            <span v-else>{{ parkInfo.leasedArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">园区剩余可租面积（平方米）</label>
            <el-input v-if="isEditing" v-model.number="formData.remainingLeasableArea" placeholder="请输入剩余可租面积" />
            <span v-else>{{ parkInfo.remainingLeasableArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label>园区剩余可售面积（平方米）</label>
            <el-input v-if="isEditing" v-model.number="formData.remainingSellableArea" placeholder="请输入剩余可售面积" />
            <span v-else>{{ parkInfo.remainingSellableArea || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">入驻企业</div>
        <div class="section-desc">* 数据由后台统计生成，不可修改</div>
        <div class="info-grid">
          <div class="info-item">
            <label>入驻企业总数（家）</label>
            <span>{{ parkInfo.enterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>规模以上企业（家）</label>
            <span>{{ parkInfo.aboveScaleCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高新技术企业（家）</label>
            <span>{{ parkInfo.highTechCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>科技型中小企业（家）</label>
            <span>{{ parkInfo.techSmeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>入驻企业总数（家）</label>
            <span>{{ parkInfo.enterpriseCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>规模以上企业（家）</label>
            <span>{{ parkInfo.aboveScaleCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>创新型中小企业（家）</label>
            <span>{{ parkInfo.innovativeSmeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>国家专精特新中小企业（家）</label>
            <span>{{ parkInfo.hiddenChampionCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">入驻员工</div>
        <div class="section-desc">* 数据由后台统计生成，不可修改</div>
        <div class="info-grid">
          <div class="info-item">
            <label>入驻企业员工总数（人）</label>
            <span>{{ parkInfo.employeeCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>"国千"人才人数（人）</label>
            <span>{{ parkInfo.nationalSpecializedCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>"省千"人才人数（人）</label>
            <span>{{ parkInfo.provincialSpecializedCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>正高级工程师人数（人）</label>
            <span>{{ parkInfo.seniorEngineerCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级工程师人数（人）</label>
            <span>{{ parkInfo.senior2EngineerCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级技师人数（人）</label>
            <span>{{ parkInfo.seniorTechnicianCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>硕士及副高以上人数（人）</label>
            <span>{{ parkInfo.masterAndAboveCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>博士以上人数（人）</label>
            <span>{{ parkInfo.masterCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">创新专利</div>
        <div class="section-desc">* 数据由后台统计生成，不可修改</div>
        <div class="info-grid">
          <div class="info-item">
            <label>专利拥有量（件）</label>
            <span>{{ parkInfo.patentTotalCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>发明专利（件）</label>
            <span>{{ parkInfo.inventionCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>实用新型专利（件）</label>
            <span>{{ parkInfo.utilityModelCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>外观设计专利（件）</label>
            <span>{{ parkInfo.appearanceCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'intro'" class="tab-content">
      <div class="intro-section">
        <div class="section-title">园区图片</div>
        <div class="form-item">
          <label>园区图片</label>
          <div class="image-upload-container">
            <div v-if="displayImages.length === 0 && !isEditing" class="image-empty">
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
                <div v-if="isEditing" class="image-delete" @click.stop="handleDeleteImage(index)">
                  <i class="el-icon-close"></i>
                </div>
                <div v-if="index === 0" class="primary-tag">主图</div>
              </div>
              <div v-if="isEditing && formData.parkImages.length < 6" class="image-add" @click="handleImageUpload">
                <i class="el-icon-plus"></i>
              </div>
            </div>
            <div v-if="isEditing" class="image-tips">最多上传6张图片，支持jpg、jpeg、png格式，单张图片不超过10MB</div>
          </div>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">园区介绍</div>
        <div class="form-item">
          <label class="required">园区介绍</label>
          <el-input
            v-if="isEditing"
            type="textarea"
            v-model="formData.introduction"
            :rows="6"
            placeholder="请输入园区介绍..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
          <div v-else class="intro-text">{{ parkInfo.introduction || '-' }}</div>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">公共配套设施</div>
        <div class="form-item">
          <label>公共配套设施</label>
          <el-input
            v-if="isEditing"
            type="textarea"
            v-model="formData.publicFacilities"
            :rows="6"
            placeholder="请输入公共配套设施..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
          <div v-else class="intro-text">{{ parkInfo.publicFacilities || '-' }}</div>
        </div>
      </div>

      <div class="intro-section">
        <div class="section-title">公共配套服务</div>
        <div class="form-item">
          <label>公共配套服务</label>
          <el-input
            v-if="isEditing"
            type="textarea"
            v-model="formData.publicServices"
            :rows="6"
            placeholder="请输入公共配套服务..."
            maxlength="10000"
            show-word-limit
            class="intro-input"
          ></el-input>
          <div v-else class="intro-text">{{ parkInfo.publicServices || '-' }}</div>
        </div>
      </div>
    </div>
  </div>

    <input ref="parkImageInput" type="file" multiple accept="image/jpeg,image/png,image/jpg" style="display:none" @change="handleImageFileChange" />
  </div>
</template>

<script>
import { getParkDetail, updatePark } from '@/api/park'
import { uploadFile, deleteFile } from '@/api/tech-innovation'
import { mapGetters } from 'vuex'

export default {
  name: 'ParkMine',
  data() {
    return {
      loading: false,
      activeTab: 'basic',
      isEditing: false,
      parkInfo: {},
      formData: {
        parkName: '',
        parkStatus: '',
        parkType: '',
        districtName: '',
        leadingIndustry: '',
        operatorUnit: '',
        operatorNature: '',
        personInCharge: '',
        inChargePhone: '',
        contactPerson: '',
        contactPhone: '',
        landArea: '',
        buildArea: '',
        leasedArea: '',
        remainingLeasableArea: '',
        remainingSellableArea: '',
        address: '',
        isUpgradable: '',
        upgradeContent: '',
        devMode: '',
        introduction: '',
        parkImages: [],
        publicFacilities: '',
        publicServices: ''
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    displayImages() {
      if (this.formData.parkImages && this.formData.parkImages.length > 0) {
        return this.formData.parkImages
      }
      if (this.parkInfo.parkImages) {
        try {
          const parsed = JSON.parse(this.parkInfo.parkImages)
          return Array.isArray(parsed) ? parsed : []
        } catch (e) {
          return []
        }
      }
      return []
    },
    imageUrlList() {
      return this.displayImages.map(img => this.getImageUrl(img))
    }
  },
  created() {
    this.getParkInfo()
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

    initFormData() {
      let parkImages = []
      if (this.parkInfo.parkImages) {
        try {
          parkImages = JSON.parse(this.parkInfo.parkImages)
        } catch (e) {
          parkImages = []
        }
      }
      this.formData = {
        parkName: this.parkInfo.parkName || '',
        parkStatus: this.parkInfo.parkStatus || '',
        parkType: this.parkInfo.parkType || '',
        districtName: this.parkInfo.districtName || '',
        leadingIndustry: this.parkInfo.leadingIndustry || '',
        operatorUnit: this.parkInfo.operatorUnit || '',
        operatorNature: this.parkInfo.operatorNature || '',
        personInCharge: this.parkInfo.personInCharge || '',
        inChargePhone: this.parkInfo.inChargePhone || '',
        contactPerson: this.parkInfo.contactPerson || '',
        contactPhone: this.parkInfo.contactPhone || '',
        landArea: this.parkInfo.landArea || '',
        buildArea: this.parkInfo.buildArea || '',
        leasedArea: this.parkInfo.leasedArea || '',
        remainingLeasableArea: this.parkInfo.remainingLeasableArea || '',
        remainingSellableArea: this.parkInfo.remainingSellableArea || '',
        address: this.parkInfo.address || '',
        isUpgradable: this.parkInfo.isUpgradable || '',
        upgradeContent: this.parkInfo.upgradeContent || '',
        devMode: this.parkInfo.devMode || '',
        introduction: this.parkInfo.introduction || '',
        parkImages: parkImages,
        publicFacilities: this.parkInfo.publicFacilities || '',
        publicServices: this.parkInfo.publicServices || ''
      }
    },

    handleEdit() {
      this.isEditing = true
    },

    handleCancel() {
      this.isEditing = false
      this.initFormData()
    },

    async handleSave() {
      const parkId = this.userInfo.parkId
      if (!parkId) {
        this.$message.warning('未关联园区，请联系管理员')
        return
      }
      try {
        const saveData = {
          id: parkId,
          ...this.formData,
          // 所属区域由区县/市级管理员设置，园区端编辑时保留原值（为空则传 null，MyBatis-Plus 不覆盖）
          districtName: this.formData.districtName || this.parkInfo.districtName || null
        }
        saveData.parkImages = JSON.stringify(this.formData.parkImages)
        await updatePark(saveData)
        this.$message.success('保存成功')
        this.isEditing = false
        this.getParkInfo()
      } catch (error) {
        this.$message.error('保存失败')
      }
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
.park-mine-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: #f5f7fa;
  height: calc(100vh - 84px);
  overflow: hidden;
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
  flex-shrink: 0;
}

.tab-scroll-container {
  flex: 1;
  overflow-y: auto;
}

.tab-nav {
  flex: 1;
}

.save-btn {
  margin-left: 20px;
}

.cancel-btn {
  margin-left: 10px;
}

.edit-btn {
  margin-left: 20px;
  color: #409EFF;
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
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}

.section-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 16px;
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

.intro-text {
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
  min-height: 120px;
}

.intro-text {
  margin-top: 8px;
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

.primary-tag {
  position: absolute;
  bottom: 6px;
  left: 6px;
  background: rgba(64, 158, 255, 0.8);
  color: white;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 4px;
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