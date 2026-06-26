<template>
  <div class="park-detail">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="breadcrumb">
        <span>园区列表</span>
        <span class="separator">></span>
        <span>{{ isEditMode ? '编辑园区' : '园区详情' }}</span>
      </div>
      <div class="header-actions">
        <el-button v-if="!isEditMode" type="primary" icon="el-icon-edit" @click="enterEditMode">编辑</el-button>
        <el-button v-if="isEditMode" type="primary" icon="el-icon-check" @click="handleSave">保存</el-button>
        <el-button v-if="isEditMode" @click="cancelEdit">取消</el-button>
        <el-button icon="el-icon-arrow-left" @click="goBack">返回列表</el-button>
      </div>
    </div>

    <!-- 标签页切换 -->
    <el-card class="tab-card" shadow="never">
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="formData" label-width="120px" ref="basicForm">
            <!-- 基本信息 -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">基本信息</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="园区名称" prop="parkName">
                    <el-input 
                      v-model="formData.parkName" 
                      :disabled="!isEditMode"
                      placeholder="请输入园区名称" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="园区状态" prop="parkStatus">
                    <el-select 
                      v-model="formData.parkStatus" 
                      :disabled="!isEditMode"
                      placeholder="请选择园区状态"
                    >
                      <el-option label="已运营" value="已运营" />
                      <el-option label="在建" value="在建" />
                      <el-option label="规划" value="规划" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="土地性质" prop="landNature">
                    <el-select 
                      v-model="formData.landNature" 
                      :disabled="!isEditMode"
                      placeholder="请选择土地性质"
                    >
                      <el-option label="工业用地" value="工业用地" />
                      <el-option label="商业用地" value="商业用地" />
                      <el-option label="综合用地" value="综合用地" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="星级评定" prop="starLevel">
                    <el-select 
                      v-model="formData.starLevel" 
                      :disabled="!isEditMode"
                      placeholder="请选择星级"
                    >
                      <el-option label="一星级园区" value="1" />
                      <el-option label="二星级园区" value="2" />
                      <el-option label="三星级园区" value="3" />
                      <el-option label="四星级园区" value="4" />
                      <el-option label="五星级园区" value="5" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="园区地址" prop="address">
                    <el-input 
                      v-model="formData.address" 
                      :disabled="!isEditMode"
                      placeholder="请输入园区地址" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="是否升级改造" prop="isUpgrade">
                    <el-select 
                      v-model="formData.isUpgrade" 
                      :disabled="!isEditMode"
                      placeholder="请选择"
                    >
                      <el-option label="是" value="是" />
                      <el-option label="否" value="否" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="改造提升内容" prop="upgradeContent">
                    <el-input 
                      v-model="formData.upgradeContent" 
                      :disabled="!isEditMode"
                      placeholder="请输入改造提升内容" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="开发模式" prop="devMode">
                    <el-select 
                      v-model="formData.devMode" 
                      :disabled="!isEditMode"
                      placeholder="请选择开发模式"
                    >
                      <el-option label="政府主导开发" value="政府主导开发" />
                      <el-option label="工业地产开发" value="工业地产开发" />
                      <el-option label="企业联合开发" value="企业联合开发" />
                      <el-option label="龙头企业开发" value="龙头企业开发" />
                      <el-option label="专业机构开发" value="专业机构开发" />
                      <el-option label="村集体联合开发" value="村集体联合开发" />
                      <el-option label="政企合作" value="政企合作" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="土地来源" prop="landSource">
                    <el-select 
                      v-model="formData.landSource" 
                      :disabled="!isEditMode"
                      placeholder="请选择土地来源"
                    >
                      <el-option label="国有建设用地出让" value="国有建设用地出让" />
                      <el-option label="集体建设用地" value="集体建设用地" />
                      <el-option label="租赁用地" value="租赁用地" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="所属区域" prop="districtName">
                    <el-select 
                      v-model="formData.districtName" 
                      :disabled="!isEditMode"
                      placeholder="请选择所属区域"
                    >
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
                </el-col>
                <el-col :span="6">
                  <el-form-item label="园区类型" prop="parkType">
                    <el-select
                      v-model="formData.parkType"
                      :disabled="!isEditMode"
                      placeholder="请选择园区类型"
                    >
                      <el-option label="生产性制造类" value="生产性制造类" />
                      <el-option label="生产性服务类" value="生产性服务类" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="主导产业" prop="mainIndustry">
                    <el-input 
                      v-model="formData.mainIndustry" 
                      :disabled="!isEditMode"
                      placeholder="请输入主导产业" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- 联系方式 -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">联系方式</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="运营单位" prop="operationOrgName">
                    <el-input 
                      v-model="formData.operationOrgName" 
                      :disabled="!isEditMode"
                      placeholder="请输入运营单位" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="运营性质" prop="operationOrgNature">
                    <el-select 
                      v-model="formData.operationOrgNature" 
                      :disabled="!isEditMode"
                      placeholder="请选择运营性质"
                    >
                      <el-option label="国有企业" value="国有企业" />
                      <el-option label="民营企业" value="民营企业" />
                      <el-option label="合资企业" value="合资企业" />
                      <el-option label="外资企业" value="外资企业" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="负责人" prop="orgLeader">
                    <el-input 
                      v-model="formData.orgLeader" 
                      :disabled="!isEditMode"
                      placeholder="请输入负责人" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="负责人电话" prop="orgLeaderPhone">
                    <el-input 
                      v-model="formData.orgLeaderPhone" 
                      :disabled="!isEditMode"
                      placeholder="请输入负责人电话" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="联系人" prop="orgContact">
                    <el-input 
                      v-model="formData.orgContact" 
                      :disabled="!isEditMode"
                      placeholder="请输入联系人" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="联系人电话" prop="orgContactPhone">
                    <el-input 
                      v-model="formData.orgContactPhone" 
                      :disabled="!isEditMode"
                      placeholder="请输入联系人电话" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- 园区面积 -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">园区面积</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="实际用地面积（亩）" prop="landArea">
                    <el-input 
                      v-model.number="formData.landArea" 
                      :disabled="!isEditMode"
                      placeholder="请输入实际用地面积" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="已建建筑面积（平方米）" prop="buildArea">
                    <el-input 
                      v-model.number="formData.buildArea" 
                      :disabled="!isEditMode"
                      placeholder="请输入已建建筑面积" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="园区已租面积（平方米）" prop="rentedArea">
                    <el-input 
                      v-model.number="formData.rentedArea" 
                      :disabled="!isEditMode"
                      placeholder="请输入园区已租面积" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="园区剩余可租面积（平方米）" prop="rentRemainArea">
                    <el-input 
                      v-model.number="formData.rentRemainArea" 
                      :disabled="!isEditMode"
                      placeholder="请输入园区剩余可租面积" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="园区剩余可售面积（平方米）" prop="saleRemainArea">
                    <el-input 
                      v-model.number="formData.saleRemainArea" 
                      :disabled="!isEditMode"
                      placeholder="请输入园区剩余可售面积" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- 入驻企业（不可编辑） -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">入驻企业</span>
                <span class="tip-text">*数据由系统统计生成，不可修改</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">入驻企业总数（家）</span>
                    <span class="value">{{ formData.enterpriseCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">规模以上企业（家）</span>
                    <span class="value">{{ formData.aboveScaleCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">高新技术企业（家）</span>
                    <span class="value">{{ formData.highTechCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">科技型中小企业（家）</span>
                    <span class="value">{{ formData.techSmeCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">隐形冠军及培育企业（家）</span>
                    <span class="value">{{ formData.hiddenChampionCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">国家专精特新"小巨人"企业（家）</span>
                    <span class="value">{{ formData.nationalSrtiCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">创新型中小企业（家）</span>
                    <span class="value">{{ formData.innovativeSmeCount || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">省专精特新中小企业（家）</span>
                    <span class="value">{{ formData.provincialSrtiCount || 0 }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 入驻员工（不可编辑） -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">入驻员工</span>
                <span class="tip-text">*数据由系统统计生成，不可修改</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">入驻企业员工总数（人）</span>
                    <span class="value">{{ formatNumber(formData.employeeCount) }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">"国千"人才人数（人）</span>
                    <span class="value">{{ formData.nationalTalent || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">"省千"人才人数（人）</span>
                    <span class="value">{{ formData.provincialTalent || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">正高级工程师人数（人）</span>
                    <span class="value">{{ formData.seniorEngineer || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">高级工程师人数（人）</span>
                    <span class="value">{{ formData.engineer || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">高级技师人数（人）</span>
                    <span class="value">{{ formData.seniorTechnician || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">硕士及副高以上人数（人）</span>
                    <span class="value">{{ formData.masterAbove || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">硕士以上人数（人）</span>
                    <span class="value">{{ formData.masterDegree || 0 }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 创新专利（不可编辑） -->
            <div class="form-section">
              <div class="section-header">
                <span class="section-icon">●</span>
                <span class="section-title">创新专利</span>
                <span class="tip-text">*数据由系统统计生成，不可修改</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">专利拥有量（件）</span>
                    <span class="value">{{ formData.patentTotal || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">发明专利（件）</span>
                    <span class="value">{{ formData.patentInvention || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">实用新型专利（件）</span>
                    <span class="value">{{ formData.patentUtility || 0 }}</span>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="readonly-item">
                    <span class="label">外观设计专利（件）</span>
                    <span class="value">{{ formData.patentDesign || 0 }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-tab-pane>

        <!-- 园区简介 -->
        <el-tab-pane label="园区简介" name="introduction">
          <div class="intro-content">
            <!-- 园区介绍 -->
            <div class="intro-section">
              <h3 class="section-title">园区介绍</h3>
              <el-form :model="formData" label-width="0">
                <el-form-item>
                  <el-input
                    v-model="formData.introduction"
                    type="textarea"
                    :rows="8"
                    :disabled="!isEditMode"
                    placeholder="请输入园区介绍"
                    class="intro-textarea"
                  />
                </el-form-item>
              </el-form>
            </div>

            <!-- 园区图片 -->
            <div class="intro-section">
              <h3 class="section-title">园区图片</h3>
              <div class="image-upload-container">
                <div v-if="displayImages.length === 0" class="image-empty">
                  <i class="el-icon-picture-outline"></i>
                  <p>暂无图片</p>
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
                    <div class="primary-tag">主图</div>
                    <div v-if="isEditMode" class="image-delete" @click.stop="handleDeleteImage(index)">
                      <i class="el-icon-close"></i>
                    </div>
                  </div>
                  <div v-if="isEditMode && formData.parkImages.length < 6" class="image-add" @click="handleImageUpload">
                    <i class="el-icon-plus"></i>
                  </div>
                </div>
                <div v-if="isEditMode" class="image-tips">最多上传6张，支持jpg、jpeg、png格式，单张不超过10MB</div>
                <input ref="parkImageInput" type="file" multiple accept="image/jpeg,image/png,image/jpg" style="display:none" @change="handleImageFileChange" />
              </div>
            </div>

            <!-- 公共配套设施 -->
            <div class="intro-section">
              <h3 class="section-title">公共配套设施</h3>
              <el-form :model="formData" label-width="0">
                <el-form-item>
                  <el-input
                    v-model="formData.publicFacilities"
                    type="textarea"
                    :rows="3"
                    :disabled="!isEditMode"
                    placeholder="请输入公共配套设施"
                    class="intro-textarea"
                  />
                </el-form-item>
              </el-form>
            </div>

            <!-- 公共配套服务 -->
            <div class="intro-section">
              <h3 class="section-title">公共配套服务</h3>
              <el-form :model="formData" label-width="0">
                <el-form-item>
                  <el-input
                    v-model="formData.publicServices"
                    type="textarea"
                    :rows="3"
                    :disabled="!isEditMode"
                    placeholder="请输入公共配套服务"
                    class="intro-textarea"
                  />
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 图片预览使用 el-image 内置预览，无需单独弹窗 -->
  </div>
</template>

<script>
import { getParkDetail, updatePark } from '@/api/park'
import { uploadFile, deleteFile } from '@/api/tech-innovation'

/**
 * 园区详情页面（支持编辑模式）
 * @author park-team
 */
export default {
  name: 'ParkDetail',
  data() {
    return {
      parkInfo: {},
      formData: {},
      activeTab: 'basic',
      isEditMode: false
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
  /**
   * 页面挂载时获取园区详情
   */
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.fetchParkDetail(id)
    }
    
    // 检查是否从编辑入口进入
    const editParam = this.$route.query.edit
    if (editParam === '1') {
      this.isEditMode = true
    }
  },
  methods: {
    /**
     * 获取园区详情
     * @param {Number} id - 园区ID
     */
    async fetchParkDetail(id) {
      try {
        const res = await getParkDetail(id)
        const data = res.data || {}
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
        this.parkInfo = {
          ...data,
          mainIndustry: data.leadingIndustry || data.mainIndustry,
          operationOrgName: data.operatorUnit || data.operationOrgName,
          operationOrgNature: data.operatorNature || data.operationOrgNature,
          orgLeader: data.personInCharge || data.orgLeader,
          orgLeaderPhone: data.inChargePhone || data.orgLeaderPhone,
          orgContact: data.contactPerson || data.orgContact,
          orgContactPhone: data.contactPhone || data.orgContactPhone,
          isUpgrade: data.isUpgradable || data.isUpgrade,
          rentedArea: data.leasedArea || data.rentedArea,
          rentRemainArea: data.remainingLeasableArea || data.rentRemainArea,
          saleRemainArea: data.remainingSellableArea || data.saleRemainArea,
          parkImages
        }
        this.formData = { ...this.parkInfo }
        this.formData.parkImages = parkImages
      } catch (e) {
        console.error('获取园区详情失败:', e)
        this.$message.error('获取园区详情失败')
      }
    },
    /**
     * 获取图片完整 URL
     */
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
      if (url.startsWith('http://') || url.startsWith('https://')) return url
      if (url.startsWith('/api/')) return url
      if (url.startsWith('/')) return url
      return '/api/files/preview/' + url
    },
    /**
     * 图片加载失败处理
     */
    handleImageError(index) {
      console.warn('图片加载失败，索引:', index)
    },
    /**
     * 触发图片上传
     */
    handleImageUpload() {
      this.$refs.parkImageInput.click()
    },
    /**
     * 处理图片文件选择
     */
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
      for (let i = 0; i < toUpload.length; i++) {
        const file = toUpload[i]
        const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
        if (!isImage) {
          this.$message.error('仅支持 jpg、jpeg、png 格式')
          continue
        }
        const isLt10M = file.size / 1024 / 1024 < 10
        if (!isLt10M) {
          this.$message.error('单张图片大小不能超过 10MB')
          continue
        }
        try {
          const formData = new FormData()
          formData.append('file', file)
          formData.append('bizType', 'park_image')
          const res = await uploadFile(formData)
          const resData = res.data || res
          this.formData.parkImages.push({
            id: resData.id,
            url: resData.url || ('/api/files/preview/' + resData.id),
            name: resData.name || file.name
          })
        } catch (err) {
          this.$message.error('图片上传失败：' + file.name)
        }
      }
      event.target.value = ''
    },
    /**
     * 删除图片
     */
    async handleDeleteImage(index) {
      try {
        await this.$confirm('确定删除该图片？', '提示', { type: 'warning' })
        const image = this.formData.parkImages[index]
        if (image && image.id) {
          await deleteFile(image.id).catch(() => {})
        }
        this.formData.parkImages.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel' && err?.message) {
          this.$message.error('删除失败：' + err.message)
        }
      }
    },
    /**
     * 格式化数字
     * @param {Number} value - 数字
     */
    formatNumber(value) {
      if (value === null || value === undefined) return '-'
      return Number(value).toLocaleString()
    },
    /**
     * 返回园区列表页
     */
    goBack() {
      this.$router.push('/district/park')
    },
    /**
     * 进入编辑模式
     */
    enterEditMode() {
      this.isEditMode = true
    },
    /**
     * 取消编辑
     */
    cancelEdit() {
      this.isEditMode = false
      this.formData = { ...this.parkInfo }
      this.formData.parkImages = this.parkInfo.parkImages ? [...this.parkInfo.parkImages] : []
      this.$message.info('已取消编辑')
    },
    /**
     * 保存编辑内容
     */
    handleSave() {
      if (!this.formData.parkName) {
        this.$message.warning('请输入园区名称')
        return
      }

      // 前端字段 → 后端字段映射
      const submitData = {
        ...this.formData,
        leadingIndustry: this.formData.mainIndustry,
        operatorUnit: this.formData.operationOrgName,
        operatorNature: this.formData.operationOrgNature,
        personInCharge: this.formData.orgLeader,
        inChargePhone: this.formData.orgLeaderPhone,
        contactPerson: this.formData.orgContact,
        contactPhone: this.formData.orgContactPhone,
        isUpgradable: this.formData.isUpgrade,
        leasedArea: this.formData.rentedArea,
        remainingLeasableArea: this.formData.rentRemainArea,
        remainingSellableArea: this.formData.saleRemainArea,
        parkImages: this.formData.parkImages ? JSON.stringify(this.formData.parkImages) : null
      }

      updatePark(submitData).then(() => {
        this.$message.success('保存成功')
        this.isEditMode = false
        this.parkInfo = { ...this.formData }
      }).catch(() => {
        this.$message.error('保存失败')
      })
    }
  }
}
</script>

<style scoped>
.park-detail {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.breadcrumb .separator {
  margin: 0 8px;
  color: #c0c4cc;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.tab-card {
  margin-bottom: 20px;
}

.detail-tabs {
  .el-tabs__header {
    margin-bottom: 20px;
    border-bottom: 1px solid #e4e7ed;
  }
  .el-tabs__item {
    font-size: 14px;
    font-weight: 500;
    padding: 0 24px;
  }
}

/* 表单区域样式 */
.form-section {
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e4e7ed;
}

.section-icon {
  color: #409EFF;
  margin-right: 6px;
  font-size: 14px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.tip-text {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.required-label {
  color: #f56c6c;
  margin-right: 4px;
}

/* 只读数据项样式 */
.readonly-item {
  display: flex;
  flex-direction: column;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.readonly-item .label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.readonly-item .value {
  font-size: 14px;
  color: #303133;
  font-weight: 400;
}

/* 园区简介样式 */
.intro-content {
  padding: 16px;
}

.intro-section {
  margin-bottom: 16px;
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.intro-section:last-child {
  margin-bottom: 0;
}

.intro-section .section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e4e7ed;
}

.intro-textarea {
  width: 100%;
  min-height: 100px;
  background: #fff;
}

.image-container {
  padding: 12px;
  background: #fff;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
}

.image-uploader {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-uploader .el-upload--picture-card {
  width: 148px;
  height: 148px;
  line-height: 148px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  width: 100%;
}

/* 图片上传展示（新） */
.image-upload-container {
  width: 100%;
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

.image-item .image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-item .image-delete {
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

.image-item .image-delete:hover {
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: #909399;
}

.empty-state i {
  font-size: 36px;
  margin-bottom: 8px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .image-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .image-grid {
    grid-template-columns: 1fr;
  }
}
</style>