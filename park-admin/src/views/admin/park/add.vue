<template>
  <div class="park-add-container">
    <!-- 面包屑 -->
    <div class="breadcrumb-bar">
      <div class="breadcrumb-left">
        <span class="breadcrumb-link" @click="goBack">园区列表</span>
        <i class="el-icon-arrow-right breadcrumb-sep"></i>
        <span class="breadcrumb-current">新增园区</span>
      </div>
      <div class="breadcrumb-right">
        <el-button size="small" @click="handleReset">重置</el-button>
        <el-button type="primary" size="small" :loading="saveLoading" @click="handleSave">保存</el-button>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="info-card">
      <div class="card-title">基本信息</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label required">园区名称</label>
          <el-input v-model="formData.parkName" size="small" placeholder="请输入园区名称" />
        </div>
        <div class="form-field">
          <label class="field-label">园区代码</label>
          <el-input v-model="formData.parkCode" size="small" placeholder="如 DS2026001" />
        </div>
        <div class="form-field">
          <label class="field-label required">园区类型</label>
          <el-select v-model="formData.parkType" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in parkTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">所属区域</label>
          <el-select v-model="formData.districtName" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in districtOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">园区状态</label>
          <el-select v-model="formData.parkStatus" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in parkStatusOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label">园区认定</label>
          <el-select v-model="formData.recognition" size="small" placeholder="请选择" style="width: 100%">
            <el-option label="已认定" value="已认定" />
            <el-option label="未认定" value="未认定" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label">星级评定</label>
          <el-select v-model="formData.starLevel" size="small" placeholder="请选择" style="width: 100%">
            <el-option label="一星级" :value="1" />
            <el-option label="二星级" :value="2" />
            <el-option label="三星级" :value="3" />
            <el-option label="四星级" :value="4" />
            <el-option label="五星级" :value="5" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">开发模式</label>
          <el-select v-model="formData.devMode" size="small" placeholder="请选择" style="width: 100%">
            <el-option label="政府主导" value="政府主导" />
            <el-option label="企业自建" value="企业自建" />
            <el-option label="政企合作" value="政企合作" />
            <el-option label="市场运营" value="市场运营" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">土地来源</label>
          <el-select v-model="formData.landSource" size="small" placeholder="请选择" style="width: 100%">
            <el-option label="划拨" value="划拨" />
            <el-option label="出让" value="出让" />
            <el-option label="租赁" value="租赁" />
            <el-option label="国有建设用地出让" value="国有建设用地出让" />
            <el-option label="集体建设用地" value="集体建设用地" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">土地性质</label>
          <el-select v-model="formData.landNature" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in landNatureOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">主导产业</label>
          <el-select v-model="formData.leadingIndustry" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in leadingIndustryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label">是否升级改造</label>
          <el-select v-model="formData.isUpgradable" size="small" placeholder="请选择" style="width: 100%">
            <el-option label="是" value="是" />
            <el-option label="否" value="否" />
          </el-select>
        </div>
        <div class="form-field" style="grid-column: span 2">
          <label class="field-label required">园区地址</label>
          <el-input v-model="formData.address" size="small" placeholder="请输入园区地址" />
        </div>
        <div class="form-field" style="grid-column: span 2">
          <label class="field-label">改造提升内容</label>
          <el-input v-model="formData.upgradeContent" size="small" placeholder="请输入改造提升内容" />
        </div>
      </div>
    </div>

    <!-- 联系方式 -->
    <div class="info-card">
      <div class="card-title">联系方式</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label required">运营单位</label>
          <el-input v-model="formData.operatorUnit" size="small" placeholder="请输入运营单位" />
        </div>
        <div class="form-field">
          <label class="field-label required">运营性质</label>
          <el-select v-model="formData.operatorNature" size="small" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in operatorNatureOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="form-field">
          <label class="field-label required">负责人</label>
          <el-input v-model="formData.personInCharge" size="small" placeholder="请输入负责人" />
        </div>
        <div class="form-field">
          <label class="field-label required">负责人电话</label>
          <el-input v-model="formData.inChargePhone" size="small" placeholder="请输入负责人电话" />
        </div>
        <div class="form-field">
          <label class="field-label required">联系人</label>
          <el-input v-model="formData.contactPerson" size="small" placeholder="请输入联系人" />
        </div>
        <div class="form-field">
          <label class="field-label required">联系人电话</label>
          <el-input v-model="formData.contactPhone" size="small" placeholder="请输入联系人电话" />
        </div>
      </div>
    </div>

    <!-- 园区面积 -->
    <div class="info-card">
      <div class="card-title">园区面积</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label required">实际用地面积（亩）</label>
          <el-input-number v-model="formData.landArea" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label required">已建建筑面积（平方米）</label>
          <el-input-number v-model="formData.buildArea" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label required">园区已租面积（平方米）</label>
          <el-input-number v-model="formData.leasedArea" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label required">园区剩余可租面积（平方米）</label>
          <el-input-number v-model="formData.remainingLeasableArea" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label required">园区剩余可售面积（平方米）</label>
          <el-input-number v-model="formData.remainingSellableArea" :min="0" size="small" style="width: 100%" />
        </div>
      </div>
    </div>

    <!-- 入驻企业 -->
    <div class="info-card">
      <div class="card-title">入驻企业</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label">入驻企业总数（家）</label>
          <el-input-number v-model="formData.enterpriseCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">规模以上企业（家）</label>
          <el-input-number v-model="formData.aboveScaleCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">高新技术企业（家）</label>
          <el-input-number v-model="formData.highTechCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">科技型中小企业（家）</label>
          <el-input-number v-model="formData.techSmeCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">隐形冠军及培育企业（家）</label>
          <el-input-number v-model="formData.hiddenChampionCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">专精特新"小巨人"企业（家）</label>
          <el-input-number v-model="formData.nationalSpecializedCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">创新型中小企业（家）</label>
          <el-input-number v-model="formData.innovativeSmeCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">省专精特新中小企业（家）</label>
          <el-input-number v-model="formData.provincialSpecializedCount" :min="0" size="small" style="width: 100%" />
        </div>
      </div>
    </div>

    <!-- 入驻员工 -->
    <div class="info-card">
      <div class="card-title">入驻员工</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label">入驻企业员工总数（人）</label>
          <el-input-number v-model="formData.employeeCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">"国千"人才人数（人）</label>
          <el-input-number v-model="formData.national1000TalentCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">"省千"人才人数（人）</label>
          <el-input-number v-model="formData.provincial1000TalentCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">正高级工程师人数（人）</label>
          <el-input-number v-model="formData.seniorEngineerCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">高级工程师人数（人）</label>
          <el-input-number v-model="formData.senior2EngineerCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">高级技师人数（人）</label>
          <el-input-number v-model="formData.seniorTechnicianCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">硕士及副高以上人数（人）</label>
          <el-input-number v-model="formData.masterAndAboveCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">硕士以上人数（人）</label>
          <el-input-number v-model="formData.masterCount" :min="0" size="small" style="width: 100%" />
        </div>
      </div>
    </div>

    <!-- 创新专利 -->
    <div class="info-card">
      <div class="card-title">创新专利</div>
      <div class="form-grid">
        <div class="form-field">
          <label class="field-label">专利拥有量（件）</label>
          <el-input-number v-model="formData.patentTotalCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">发明专利（件）</label>
          <el-input-number v-model="formData.inventionCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">实用新型专利（件）</label>
          <el-input-number v-model="formData.utilityModelCount" :min="0" size="small" style="width: 100%" />
        </div>
        <div class="form-field">
          <label class="field-label">外观设计专利（件）</label>
          <el-input-number v-model="formData.appearanceCount" :min="0" size="small" style="width: 100%" />
        </div>
      </div>
    </div>

    <!-- 园区简介 -->
    <div class="info-card">
      <div class="card-title">园区简介</div>
      <div class="form-grid">
        <div class="form-field" style="grid-column: span 4">
          <label class="field-label">园区介绍</label>
          <el-input
            v-model="formData.introduction"
            type="textarea"
            :rows="4"
            placeholder="请输入园区介绍..."
            maxlength="10000"
            show-word-limit
          />
        </div>
        <div class="form-field" style="grid-column: span 4">
          <label class="field-label">公共配套设施</label>
          <el-input
            v-model="formData.publicFacilities"
            type="textarea"
            :rows="3"
            placeholder="请输入公共配套设施..."
            maxlength="10000"
            show-word-limit
          />
        </div>
        <div class="form-field" style="grid-column: span 4">
          <label class="field-label">公共配套服务</label>
          <el-input
            v-model="formData.publicServices"
            type="textarea"
            :rows="3"
            placeholder="请输入公共配套服务..."
            maxlength="10000"
            show-word-limit
          />
        </div>
        <div class="form-field" style="grid-column: span 4">
          <label class="field-label">园区图片</label>
          <div class="upload-area">
            <div class="upload-btn" @click="handleUploadImage">
              <i class="el-icon-plus upload-icon"></i>
            </div>
            <div v-if="formData.parkImages && formData.parkImages.length > 0" class="image-preview-list">
              <div v-for="(img, index) in formData.parkImages" :key="index" class="image-preview">
                <img :src="img" :alt="`园区图片${index + 1}`" />
                <i class="el-icon-delete image-delete" @click="removeImage(index)"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { savePark } from '@/api/park'

export default {
  name: 'AdminParkAdd',
  data() {
    return {
      parkStatusOptions: ['规划中', '建设中', '已投运'],
      landNatureOptions: ['工业用地', '商业用地', '商务用地', '其他'],
      districtOptions: ['滨江区', '萧山区', '余杭区', '西湖区', '上城区', '拱墅区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
      parkTypeOptions: ['生产性制造类', '生产性服务类'],
      leadingIndustryOptions: ['数字经济', '智能制造', '生物医药', '新材料', '新能源', '集成电路', '科技服务', '其他'],
      operatorNatureOptions: ['国有企业', '民营企业', '事业单位', '其他'],
      saveLoading: false,
      formData: {
        // 基本信息
        parkName: '',
        parkCode: '',
        parkType: '',
        districtName: '',
        parkStatus: '',
        recognition: '',
        starLevel: null,
        devMode: '',
        landSource: '',
        landNature: '',
        leadingIndustry: '',
        isUpgradable: '',
        upgradeContent: '',
        address: '',
        // 联系方式
        operatorUnit: '',
        operatorNature: '',
        personInCharge: '',
        inChargePhone: '',
        contactPerson: '',
        contactPhone: '',
        // 园区面积
        landArea: null,
        buildArea: null,
        leasedArea: null,
        remainingLeasableArea: null,
        remainingSellableArea: null,
        // 入驻企业
        enterpriseCount: null,
        aboveScaleCount: null,
        highTechCount: null,
        techSmeCount: null,
        hiddenChampionCount: null,
        nationalSpecializedCount: null,
        innovativeSmeCount: null,
        provincialSpecializedCount: null,
        // 入驻员工
        employeeCount: null,
        national1000TalentCount: null,
        provincial1000TalentCount: null,
        seniorEngineerCount: null,
        senior2EngineerCount: null,
        seniorTechnicianCount: null,
        masterAndAboveCount: null,
        masterCount: null,
        // 创新专利
        patentTotalCount: null,
        inventionCount: null,
        utilityModelCount: null,
        appearanceCount: null,
        // 园区简介
        introduction: '',
        parkImages: [],
        publicFacilities: '',
        publicServices: ''
      }
    }
  },
  methods: {
    goBack() {
      this.$router.push('/admin/park')
    },
    async handleSave() {
      if (!this.formData.parkName) {
        this.$message.warning('请输入园区名称')
        return
      }
      if (!this.formData.districtName) {
        this.$message.warning('请选择所属区域')
        return
      }
      this.saveLoading = true
      try {
        const payload = {
          ...this.formData,
          parkImages: JSON.stringify(this.formData.parkImages || [])
        }
        await savePark(payload)
        this.$message.success('新增园区成功')
        // 保存后跳转到列表页
        this.$router.push('/admin/park')
      } catch (error) {
        this.$message.error('新增园区失败')
      } finally {
        this.saveLoading = false
      }
    },
    handleReset() {
      this.formData = {
        parkName: '', parkCode: '', parkType: '', districtName: '', parkStatus: '', recognition: '',
        starLevel: null, devMode: '', landSource: '', landNature: '', leadingIndustry: '',
        isUpgradable: '', upgradeContent: '', address: '',
        operatorUnit: '', operatorNature: '', personInCharge: '', inChargePhone: '',
        contactPerson: '', contactPhone: '',
        landArea: null, buildArea: null, leasedArea: null, remainingLeasableArea: null, remainingSellableArea: null,
        enterpriseCount: null, aboveScaleCount: null, highTechCount: null, techSmeCount: null,
        hiddenChampionCount: null, nationalSpecializedCount: null, innovativeSmeCount: null, provincialSpecializedCount: null,
        employeeCount: null, national1000TalentCount: null, provincial1000TalentCount: null,
        seniorEngineerCount: null, senior2EngineerCount: null, seniorTechnicianCount: null,
        masterAndAboveCount: null, masterCount: null,
        patentTotalCount: null, inventionCount: null, utilityModelCount: null, appearanceCount: null,
        introduction: '', parkImages: [], publicFacilities: '', publicServices: ''
      }
    },
    handleUploadImage() {
      this.$message.info('图片上传功能开发中')
    },
    removeImage(index) {
      this.formData.parkImages.splice(index, 1)
    }
  }
}
</script>

<style scoped>
.park-add-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 56px);
}

.breadcrumb-bar {
  background: #FFFFFF;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #6B7280;
}

.breadcrumb-left {
  display: flex;
  align-items: center;
}

.breadcrumb-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-link {
  color: #1E40AF;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #1E3A8A;
  text-decoration: underline;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #9CA3AF;
  font-size: 12px;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 500;
}

.info-card {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px 20px;
  margin-bottom: 12px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.info-card:hover {
  border-color: #1E40AF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  padding: 0 0 12px;
  border-bottom: 1px solid #F3F4F6;
  margin-bottom: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 20px;
}

.form-field {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.field-label {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 6px;
}

.field-label.required::before {
  content: '*';
  color: #DC2626;
  margin-right: 2px;
}

/* 图片上传区域 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-btn {
  width: 108px;
  height: 108px;
  border: 2px dashed #D1D5DB;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #F9FAFB;
}

.upload-btn:hover {
  border-color: #1E40AF;
  background: #F0F4FF;
}

.upload-icon {
  font-size: 24px;
  color: #9CA3AF;
}

.upload-btn:hover .upload-icon {
  color: #1E40AF;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-preview {
  position: relative;
  width: 108px;
  height: 108px;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-delete {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-preview:hover .image-delete {
  opacity: 1;
}

.image-delete:hover {
  background: rgba(220, 38, 38, 0.8);
}

/* 响应式 */
@media (max-width: 1400px) {
  .form-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
