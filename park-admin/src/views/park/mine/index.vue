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
            <el-select v-if="isEditing" v-model="formData.isUpgrade" placeholder="请选择">
              <el-option label="是" value="1" />
              <el-option label="否" value="0" />
            </el-select>
            <span v-else>{{ parkInfo.isUpgrade === '1' ? '是' : parkInfo.isUpgrade === '0' ? '否' : '-' }}</span>
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
            <span>{{ parkInfo.mainIndustry || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">联系方式</div>
        <div class="info-grid">
          <div class="info-item">
            <label class="required">运营单位</label>
            <el-input v-if="isEditing" v-model="formData.operationOrgName" placeholder="请输入运营单位" />
            <span v-else>{{ parkInfo.operationOrgName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>运营性质</label>
            <el-select v-if="isEditing" v-model="formData.operationOrgNature" placeholder="请选择运营性质">
              <el-option label="国有企业" value="国有企业" />
              <el-option label="民营企业" value="民营企业" />
              <el-option label="外资企业" value="外资企业" />
              <el-option label="其他" value="其他" />
            </el-select>
            <span v-else>{{ parkInfo.operationOrgNature || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">负责人</label>
            <el-input v-if="isEditing" v-model="formData.orgLeader" placeholder="请输入负责人" />
            <span v-else>{{ parkInfo.orgLeader || '-' }}</span>
          </div>
          <div class="info-item">
            <label>负责人电话</label>
            <el-input v-if="isEditing" v-model="formData.orgLeaderPhone" placeholder="请输入负责人电话" />
            <span v-else>{{ parkInfo.orgLeaderPhone || '-' }}</span>
          </div>
          <div class="info-item">
            <label>联系人</label>
            <el-input v-if="isEditing" v-model="formData.orgContact" placeholder="请输入联系人" />
            <span v-else>{{ parkInfo.orgContact || '-' }}</span>
          </div>
          <div class="info-item">
            <label>联系人电话</label>
            <el-input v-if="isEditing" v-model="formData.orgContactPhone" placeholder="请输入联系人电话" />
            <span v-else>{{ parkInfo.orgContactPhone || '-' }}</span>
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
            <el-input v-if="isEditing" v-model.number="formData.rentedArea" placeholder="请输入已租面积" />
            <span v-else>{{ parkInfo.rentedArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="required">园区剩余可租面积（平方米）</label>
            <el-input v-if="isEditing" v-model.number="formData.rentRemainArea" placeholder="请输入剩余可租面积" />
            <span v-else>{{ parkInfo.rentRemainArea || '-' }}</span>
          </div>
          <div class="info-item">
            <label>园区剩余可售面积（平方米）</label>
            <el-input v-if="isEditing" v-model.number="formData.saleRemainArea" placeholder="请输入剩余可售面积" />
            <span v-else>{{ parkInfo.saleRemainArea || '-' }}</span>
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
            <span>{{ parkInfo.nationalSrtiCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>"省千"人才人数（人）</label>
            <span>{{ parkInfo.provincialSrtiCount || 0 }}</span>
          </div>
          <div class="info-item">
            <label>正高级工程师人数（人）</label>
            <span>{{ parkInfo.seniorEngineer || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级工程师人数（人）</label>
            <span>{{ parkInfo.engineer || 0 }}</span>
          </div>
          <div class="info-item">
            <label>高级技师人数（人）</label>
            <span>{{ parkInfo.seniorTechnician || 0 }}</span>
          </div>
          <div class="info-item">
            <label>硕士及副高以上人数（人）</label>
            <span>{{ parkInfo.masterAbove || 0 }}</span>
          </div>
          <div class="info-item">
            <label>博士以上人数（人）</label>
            <span>{{ parkInfo.masterDegree || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">创新专利</div>
        <div class="section-desc">* 数据由后台统计生成，不可修改</div>
        <div class="info-grid">
          <div class="info-item">
            <label>专利拥有量（件）</label>
            <span>{{ parkInfo.patentTotal || 0 }}</span>
          </div>
          <div class="info-item">
            <label>发明专利（件）</label>
            <span>{{ parkInfo.patentInvention || 0 }}</span>
          </div>
          <div class="info-item">
            <label>实用新型专利（件）</label>
            <span>{{ parkInfo.patentUtility || 0 }}</span>
          </div>
          <div class="info-item">
            <label>外观设计专利（件）</label>
            <span>{{ parkInfo.patentDesign || 0 }}</span>
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
                <img :src="getImageUrl(image)" alt="园区图片" class="image-preview" @error="handleImageError(index)" />
                <div v-if="isEditing" class="image-delete" @click="handleDeleteImage(index)">
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

    <div v-show="activeTab === 'stats'" class="tab-content">
      <div class="stats-header">
        <div class="year-selector">
          <el-select v-model="selectedYear" class="year-select">
            <el-option label="2026年度" value="2026"></el-option>
            <el-option label="2025年度" value="2025"></el-option>
            <el-option label="2024年度" value="2024"></el-option>
          </el-select>
        </div>
        <el-button type="primary" class="save-btn" @click="handleSaveStats">保存</el-button>
      </div>

      <div class="quarter-cards">
        <div class="quarter-card" v-for="(quarter, index) in quarters" :key="index">
          <div class="quarter-header">
            <span class="quarter-title">{{ quarter.name }}</span>
            <span :class="['status-tag', quarter.status]">{{ quarter.status === 'filled' ? '已填报' : '未填报' }}</span>
          </div>
          <div class="quarter-footer">
            <el-button :type="quarter.status === 'filled' ? 'default' : 'primary'" size="small" @click="handleFillQuarter(index)">
              {{ quarter.status === 'filled' ? '重新填报' : '立即填报' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="stats-table-section">
        <div class="section-title">运营数据季度对比表</div>
        <el-table :data="tableData" border class="stats-table" :span-method="objectSpanMethod">
          <el-table-column label="指标类型" width="120">
            <template slot-scope="scope">
              {{ scope.row.typeName }}
            </template>
          </el-table-column>
          <el-table-column label="指标名称（单位）" width="200">
            <template slot-scope="scope">
              {{ scope.row.name }}
            </template>
          </el-table-column>
          <el-table-column label="季度数据" align="center">
            <el-table-column label="第一季度" width="140" align="center">
              <template slot-scope="scope">
                {{ scope.row.q1 === null ? '--' : scope.row.q1 }}
              </template>
            </el-table-column>
            <el-table-column label="第二季度" width="140" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.q2 === null">--</span>
                <span v-else>
                  {{ scope.row.q2 }}
                  <span v-if="scope.row.trend === 'up'" class="trend-icon up">▲</span>
                  <span v-else-if="scope.row.trend === 'down'" class="trend-icon down">▼</span>
                </span>
              </template>
            </el-table-column>
            <el-table-column label="第三季度" width="140" align="center">
              <template slot-scope="scope">
                {{ scope.row.q3 === null ? '--' : scope.row.q3 }}
              </template>
            </el-table-column>
            <el-table-column label="第四季度" width="140" align="center">
              <template slot-scope="scope">
                {{ scope.row.q4 === null ? '--' : scope.row.q4 }}
              </template>
            </el-table-column>
          </el-table-column>
        </el-table>
    </div>
    </div>
  </div>

    <el-dialog
      :title="fillQuarterDialog.title"
      :visible.sync="fillQuarterDialog.visible"
      width="700px"
      class="fill-quarter-dialog"
      :close-on-click-modal="false"
    >
      <el-form ref="fillQuarterForm" :model="fillQuarterForm" :rules="fillQuarterRules" label-width="160px">
        <div class="form-section">
          <div class="section-header">
            <i class="section-icon">▌</i>
            <span class="section-title">入驻企业</span>
          </div>
          <div class="form-row">
            <el-form-item label="当前入驻企业（家）" prop="currentEnterprises">
              <el-input v-model.number="fillQuarterForm.currentEnterprises" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="入驻企业总人数（人）" prop="totalEmployees">
              <el-input v-model.number="fillQuarterForm.totalEmployees" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="入驻企业名单">
              <div class="upload-area">
                <el-button type="default" size="small" @click="handleDownloadTemplate">下载模板</el-button>
                <el-upload
                  :action="getUploadUrl()"
                  :headers="{ Authorization: 'Bearer ' + userInfo.token }"
                  :show-file-list="false"
                  :on-success="handleEnterpriseListUploadSuccess"
                  :on-error="handleFileUploadError"
                  :before-upload="beforeEnterpriseListUpload"
                  accept=".xlsx,.xls"
                >
                  <el-button type="primary" size="small">上传解析</el-button>
                </el-upload>
              </div>
              <div v-if="fillQuarterForm.enterpriseListFileName" class="uploaded-file">
                <i class="el-icon-document"></i>
                <span>{{ fillQuarterForm.enterpriseListFileName }}</span>
                <el-button type="text" size="small" @click="clearFile">删除</el-button>
              </div>
              <div v-if="fillQuarterForm.currentEnterprises" class="parse-result">
                <span>解析结果：共 <strong>{{ fillQuarterForm.currentEnterprises }}</strong> 家企业</span>
                <span v-if="fillQuarterForm.highTechCount">，其中高新技术企业 <strong>{{ fillQuarterForm.highTechCount }}</strong> 家</span>
                <span v-if="fillQuarterForm.nationalSrtiCount">，专精特新小巨人 <strong>{{ fillQuarterForm.nationalSrtiCount }}</strong> 家</span>
                <span v-if="fillQuarterForm.hiddenChampionCount">，隐形冠军 <strong>{{ fillQuarterForm.hiddenChampionCount }}</strong> 家</span>
              </div>
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <i class="section-icon">▌</i>
            <span class="section-title">园区面积</span>
          </div>
          <div class="form-row">
            <el-form-item label="园区已租面积（平方米）" prop="rentedArea">
              <el-input v-model.number="fillQuarterForm.rentedArea" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="园区剩余可租面积（平米）" prop="availableRentArea">
              <el-input v-model.number="fillQuarterForm.availableRentArea" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="园区剩余可售面积（平方米）" prop="availableSaleArea">
              <el-input v-model.number="fillQuarterForm.availableSaleArea" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <i class="section-icon">▌</i>
            <span class="section-title">入驻员工</span>
          </div>
          <div class="form-row">
            <el-form-item label="入驻企业员工总数（人）" prop="employeeCount">
              <el-input v-model.number="fillQuarterForm.employeeCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="“国千”人才人数（人）" prop="nationalTalentCount">
              <el-input v-model.number="fillQuarterForm.nationalTalentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="“省千”人才人数（人）" prop="provincialTalentCount">
              <el-input v-model.number="fillQuarterForm.provincialTalentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="正高级工程师人数（人）" prop="seniorEngineerCount">
              <el-input v-model.number="fillQuarterForm.seniorEngineerCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="高级工程师人数（人）" prop="engineerCount">
              <el-input v-model.number="fillQuarterForm.engineerCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="高级技师人数（人）" prop="technicianCount">
              <el-input v-model.number="fillQuarterForm.technicianCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="硕士及副高以上人数（人）" prop="masterAndSeniorCount">
              <el-input v-model.number="fillQuarterForm.masterAndSeniorCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="硕士以上人数（人）" prop="masterCount">
              <el-input v-model.number="fillQuarterForm.masterCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <i class="section-icon">▌</i>
            <span class="section-title">创新专利</span>
          </div>
          <div class="form-row">
            <el-form-item label="专利拥有量（件）" prop="patentCount">
              <el-input v-model.number="fillQuarterForm.patentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="发明专利（件）" prop="inventionPatentCount">
              <el-input v-model.number="fillQuarterForm.inventionPatentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="实用新型专利（件）" prop="utilityPatentCount">
              <el-input v-model.number="fillQuarterForm.utilityPatentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
            <el-form-item label="外观设计专利（件）" prop="designPatentCount">
              <el-input v-model.number="fillQuarterForm.designPatentCount" placeholder="请输入" class="form-input"></el-input>
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancelFill">取消</el-button>
        <el-button type="primary" @click="handleConfirmFill">确认</el-button>
      </div>
    </el-dialog>

    <input ref="parkImageInput" type="file" multiple accept="image/jpeg,image/png,image/jpg" style="display:none" @change="handleImageFileChange" />
  </div>
</template>

<script>
import { getParkDetail, updatePark } from '@/api/park'
import { getOperationQuarterList, getOperationQuarter, saveOperationQuarter, downloadEnterpriseListTemplate, uploadAndParseEnterpriseList } from '@/api/operation'
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
      selectedYear: '2026',
      quarters: [
        { name: '第一季度', status: 'pending', data: null },
        { name: '第二季度', status: 'pending', data: null },
        { name: '第三季度', status: 'pending', data: null },
        { name: '第四季度', status: 'pending', data: null }
      ],
      tableData: [
        { typeName: '入驻企业', name: '入驻企业总数（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '规模以上企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '高新技术企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '科技型中小企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '隐形冠军及培育企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '专精特新小巨人企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '创新型中小企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '省专精特新中小企业（家）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '入驻员工', name: '入驻企业员工总数（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '"国千"人才（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '"省千"人才（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '正高级工程师人数（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '高级工程师人数（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '高级技师人数（人）', q1: null, q2: null, q3: null, q4: null, trend: null },
        { typeName: '', name: '硕士以上及副高人数（人）', q1: null, q2: null, q3: null, q4: null, trend: null }
      ],
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
        parkImages: [],
        publicFacilities: '',
        publicServices: ''
      },
      fillQuarterDialog: {
        visible: false,
        title: '',
        quarterIndex: 0
      },
      fillQuarterForm: {
        currentEnterprises: '',
        totalEmployees: '',
        enterpriseListFileId: '',
        enterpriseListFileName: '',
        enterpriseListFileUrl: '',
        rentedArea: '',
        availableRentArea: '',
        availableSaleArea: '',
        employeeCount: '',
        nationalTalentCount: '',
        provincialTalentCount: '',
        seniorEngineerCount: '',
        engineerCount: '',
        technicianCount: '',
        masterAndSeniorCount: '',
        masterCount: '',
        patentCount: '',
        inventionPatentCount: '',
        utilityPatentCount: '',
        designPatentCount: '',
        aboveScaleCount: '',
        highTechCount: '',
        techSmeCount: '',
        hiddenChampionCount: '',
        nationalSrtiCount: '',
        innovativeSmeCount: '',
        provincialSrtiCount: ''
      },
      fillQuarterRules: {
        currentEnterprises: [
          { required: true, message: '请输入当前入驻企业数量', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        totalEmployees: [
          { required: true, message: '请输入入驻企业总人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        rentedArea: [
          { required: true, message: '请输入园区已租面积', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负数字', trigger: 'blur' }
        ],
        availableRentArea: [
          { required: true, message: '请输入园区剩余可租面积', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负数字', trigger: 'blur' }
        ],
        availableSaleArea: [
          { required: true, message: '请输入园区剩余可售面积', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负数字', trigger: 'blur' }
        ],
        employeeCount: [
          { required: true, message: '请输入入驻企业员工总数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        nationalTalentCount: [
          { required: true, message: '请输入"国千"人才人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        provincialTalentCount: [
          { required: true, message: '请输入"省千"人才人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        seniorEngineerCount: [
          { required: true, message: '请输入正高级工程师人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        engineerCount: [
          { required: true, message: '请输入高级工程师人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        technicianCount: [
          { required: true, message: '请输入高级技师人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        masterAndSeniorCount: [
          { required: true, message: '请输入硕士及副高以上人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        masterCount: [
          { required: true, message: '请输入硕士以上人数', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        patentCount: [
          { required: true, message: '请输入专利拥有量', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        inventionPatentCount: [
          { required: true, message: '请输入发明专利数量', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        utilityPatentCount: [
          { required: true, message: '请输入实用新型专利数量', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ],
        designPatentCount: [
          { required: true, message: '请输入外观设计专利数量', trigger: 'blur' },
          { type: 'number', min: 0, message: '请输入非负整数', trigger: 'blur' }
        ]
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
    }
  },
  created() {
    this.getParkInfo()
  },
  watch: {
    selectedYear(newVal) {
      this.loadQuarterData()
    },
    activeTab(newVal) {
      if (newVal === 'stats') {
        this.loadQuarterData()
      }
    }
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
          mainIndustry: this.parkInfo.mainIndustry || '',
          operationOrgName: this.parkInfo.operationOrgName || '',
          operationOrgNature: this.parkInfo.operationOrgNature || '',
          orgLeader: this.parkInfo.orgLeader || '',
          orgLeaderPhone: this.parkInfo.orgLeaderPhone || '',
          orgContact: this.parkInfo.orgContact || '',
          orgContactPhone: this.parkInfo.orgContactPhone || '',
          landArea: this.parkInfo.landArea || '',
          buildArea: this.parkInfo.buildArea || '',
          rentedArea: this.parkInfo.rentedArea || '',
          rentRemainArea: this.parkInfo.rentRemainArea || '',
          saleRemainArea: this.parkInfo.saleRemainArea || '',
          address: this.parkInfo.address || '',
          isUpgrade: this.parkInfo.isUpgrade || '',
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
          ...this.formData
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
      const files = event.target.files
      if (!files || files.length === 0) return

      const remainingCount = 6 - this.formData.parkImages.length
      if (files.length > remainingCount) {
        this.$message.error(`最多只能上传6张图片，还可以上传${remainingCount}张`)
        return
      }

      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        const isImage = file.type.startsWith('image/')
        if (!isImage) {
          this.$message.error('只支持图片格式')
          continue
        }
        if (file.size > 10 * 1024 * 1024) {
          this.$message.error('单张图片大小不能超过10MB')
          continue
        }

        try {
          const formData = new FormData()
          formData.append('file', file)
          formData.append('bizType', 'park_image')

          const res = await uploadFile(formData)
          const data = res.data || res

          this.formData.parkImages.push({
            id: data.id,
            url: data.url,
            name: data.name
          })
        } catch (err) {
          this.$message.error('图片上传失败：' + file.name)
        }
      }
      event.target.value = ''
    },

    async handleDeleteImage(index) {
      try {
        await this.$confirm('确定删除该图片？', '提示', { type: 'warning' })
        const image = this.formData.parkImages[index]
        if (image.id) {
          await deleteFile(image.id).catch(() => {})
        }
        this.formData.parkImages.splice(index, 1)
      } catch (err) {
        if (err !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },

    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        if (row.typeName) {
          let count = 1
          for (let i = rowIndex + 1; i < this.tableData.length; i++) {
            if (!this.tableData[i].typeName) {
              count++
            } else {
              break
            }
          }
          if (count > 1) {
            return {
              rowspan: count,
              colspan: 1
            }
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },

    async loadQuarterData() {
      const parkId = this.userInfo.parkId
      if (!parkId) return
      
      try {
        const res = await getOperationQuarterList(parkId, parseInt(this.selectedYear))
        const dataList = res.data || []
        
        this.quarters.forEach((q, index) => {
          const found = dataList.find(item => item.quarter === index + 1)
          q.status = found ? 'filled' : 'pending'
          q.data = found || null
        })
        
        this.updateTableData()
      } catch (error) {
        console.error('加载季度数据失败', error)
      }
    },

    updateTableData() {
      this.tableData[0].q1 = this.quarters[0].data?.currentEnterprises || null
      this.tableData[0].q2 = this.quarters[1].data?.currentEnterprises || null
      this.tableData[0].q3 = this.quarters[2].data?.currentEnterprises || null
      this.tableData[0].q4 = this.quarters[3].data?.currentEnterprises || null
      
      this.tableData[1].q1 = this.quarters[0].data?.aboveScaleCount || null
      this.tableData[1].q2 = this.quarters[1].data?.aboveScaleCount || null
      this.tableData[1].q3 = this.quarters[2].data?.aboveScaleCount || null
      this.tableData[1].q4 = this.quarters[3].data?.aboveScaleCount || null
      
      this.tableData[2].q1 = this.quarters[0].data?.highTechCount || null
      this.tableData[2].q2 = this.quarters[1].data?.highTechCount || null
      this.tableData[2].q3 = this.quarters[2].data?.highTechCount || null
      this.tableData[2].q4 = this.quarters[3].data?.highTechCount || null
      
      this.tableData[3].q1 = this.quarters[0].data?.techSmeCount || null
      this.tableData[3].q2 = this.quarters[1].data?.techSmeCount || null
      this.tableData[3].q3 = this.quarters[2].data?.techSmeCount || null
      this.tableData[3].q4 = this.quarters[3].data?.techSmeCount || null
      
      this.tableData[4].q1 = this.quarters[0].data?.hiddenChampionCount || null
      this.tableData[4].q2 = this.quarters[1].data?.hiddenChampionCount || null
      this.tableData[4].q3 = this.quarters[2].data?.hiddenChampionCount || null
      this.tableData[4].q4 = this.quarters[3].data?.hiddenChampionCount || null
      
      this.tableData[5].q1 = this.quarters[0].data?.nationalSrtiCount || null
      this.tableData[5].q2 = this.quarters[1].data?.nationalSrtiCount || null
      this.tableData[5].q3 = this.quarters[2].data?.nationalSrtiCount || null
      this.tableData[5].q4 = this.quarters[3].data?.nationalSrtiCount || null
      
      this.tableData[6].q1 = this.quarters[0].data?.innovativeSmeCount || null
      this.tableData[6].q2 = this.quarters[1].data?.innovativeSmeCount || null
      this.tableData[6].q3 = this.quarters[2].data?.innovativeSmeCount || null
      this.tableData[6].q4 = this.quarters[3].data?.innovativeSmeCount || null
      
      this.tableData[7].q1 = this.quarters[0].data?.provincialSrtiCount || null
      this.tableData[7].q2 = this.quarters[1].data?.provincialSrtiCount || null
      this.tableData[7].q3 = this.quarters[2].data?.provincialSrtiCount || null
      this.tableData[7].q4 = this.quarters[3].data?.provincialSrtiCount || null
      
      this.tableData[8].q1 = this.quarters[0].data?.employeeCount || null
      this.tableData[8].q2 = this.quarters[1].data?.employeeCount || null
      this.tableData[8].q3 = this.quarters[2].data?.employeeCount || null
      this.tableData[8].q4 = this.quarters[3].data?.employeeCount || null
      
      this.tableData[9].q1 = this.quarters[0].data?.nationalTalentCount || null
      this.tableData[9].q2 = this.quarters[1].data?.nationalTalentCount || null
      this.tableData[9].q3 = this.quarters[2].data?.nationalTalentCount || null
      this.tableData[9].q4 = this.quarters[3].data?.nationalTalentCount || null
      
      this.tableData[10].q1 = this.quarters[0].data?.provincialTalentCount || null
      this.tableData[10].q2 = this.quarters[1].data?.provincialTalentCount || null
      this.tableData[10].q3 = this.quarters[2].data?.provincialTalentCount || null
      this.tableData[10].q4 = this.quarters[3].data?.provincialTalentCount || null
      
      this.tableData[11].q1 = this.quarters[0].data?.seniorEngineerCount || null
      this.tableData[11].q2 = this.quarters[1].data?.seniorEngineerCount || null
      this.tableData[11].q3 = this.quarters[2].data?.seniorEngineerCount || null
      this.tableData[11].q4 = this.quarters[3].data?.seniorEngineerCount || null
      
      this.tableData[12].q1 = this.quarters[0].data?.engineerCount || null
      this.tableData[12].q2 = this.quarters[1].data?.engineerCount || null
      this.tableData[12].q3 = this.quarters[2].data?.engineerCount || null
      this.tableData[12].q4 = this.quarters[3].data?.engineerCount || null
      
      this.tableData[13].q1 = this.quarters[0].data?.technicianCount || null
      this.tableData[13].q2 = this.quarters[1].data?.technicianCount || null
      this.tableData[13].q3 = this.quarters[2].data?.technicianCount || null
      this.tableData[13].q4 = this.quarters[3].data?.technicianCount || null
      
      this.tableData[14].q1 = this.quarters[0].data?.masterAndSeniorCount || null
      this.tableData[14].q2 = this.quarters[1].data?.masterAndSeniorCount || null
      this.tableData[14].q3 = this.quarters[2].data?.masterAndSeniorCount || null
      this.tableData[14].q4 = this.quarters[3].data?.masterAndSeniorCount || null
    },

    async handleFillQuarter(index) {
      this.fillQuarterDialog.quarterIndex = index
      this.fillQuarterDialog.title = this.quarters[index].name
      this.fillQuarterDialog.visible = true
      
      await this.loadQuarterFormData(index + 1)
    },

    async loadQuarterFormData(quarter) {
      const parkId = this.userInfo.parkId
      if (!parkId) return
      
      try {
        const res = await getOperationQuarter(parkId, parseInt(this.selectedYear), quarter)
        const data = res.data
        
        if (data) {
          this.fillQuarterForm = {
            currentEnterprises: data.currentEnterprises || '',
            totalEmployees: data.totalEmployees || '',
            enterpriseListFileId: data.enterpriseListFileId || '',
            enterpriseListFileName: data.enterpriseListFileName || '',
            enterpriseListFileUrl: data.enterpriseListFileUrl || '',
            rentedArea: data.rentedArea || '',
            availableRentArea: data.availableRentArea || '',
            availableSaleArea: data.availableSaleArea || '',
            employeeCount: data.employeeCount || '',
            nationalTalentCount: data.nationalTalentCount || '',
            provincialTalentCount: data.provincialTalentCount || '',
            seniorEngineerCount: data.seniorEngineerCount || '',
            engineerCount: data.engineerCount || '',
            technicianCount: data.technicianCount || '',
            masterAndSeniorCount: data.masterAndSeniorCount || '',
            masterCount: data.masterCount || '',
            patentCount: data.patentCount || '',
            inventionPatentCount: data.inventionPatentCount || '',
            utilityPatentCount: data.utilityPatentCount || '',
            designPatentCount: data.designPatentCount || '',
            aboveScaleCount: data.aboveScaleCount || '',
            highTechCount: data.highTechCount || '',
            techSmeCount: data.techSmeCount || '',
            hiddenChampionCount: data.hiddenChampionCount || '',
            nationalSrtiCount: data.nationalSrtiCount || '',
            innovativeSmeCount: data.innovativeSmeCount || '',
            provincialSrtiCount: data.provincialSrtiCount || ''
          }
        } else {
          this.resetFillQuarterForm()
        }
      } catch (error) {
        this.resetFillQuarterForm()
      }
    },

    resetFillQuarterForm() {
      this.fillQuarterForm = {
        currentEnterprises: '',
        totalEmployees: '',
        enterpriseListFileId: '',
        enterpriseListFileName: '',
        enterpriseListFileUrl: '',
        rentedArea: '',
        availableRentArea: '',
        availableSaleArea: '',
        employeeCount: '',
        nationalTalentCount: '',
        provincialTalentCount: '',
        seniorEngineerCount: '',
        engineerCount: '',
        technicianCount: '',
        masterAndSeniorCount: '',
        masterCount: '',
        patentCount: '',
        inventionPatentCount: '',
        utilityPatentCount: '',
        designPatentCount: '',
        aboveScaleCount: '',
        highTechCount: '',
        techSmeCount: '',
        hiddenChampionCount: '',
        nationalSrtiCount: '',
        innovativeSmeCount: '',
        provincialSrtiCount: ''
      }
      if (this.$refs.fillQuarterForm) {
        this.$refs.fillQuarterForm.resetFields()
      }
    },

    handleCancelFill() {
      this.fillQuarterDialog.visible = false
      this.resetFillQuarterForm()
    },

    async handleConfirmFill() {
      this.$refs.fillQuarterForm.validate(async (valid) => {
        if (valid) {
          const parkId = this.userInfo.parkId
          if (!parkId) {
            this.$message.warning('未关联园区，请联系管理员')
            return
          }
          
          const quarterIndex = this.fillQuarterDialog.quarterIndex
          const quarter = quarterIndex + 1
          
          try {
            const data = {
              parkId: parkId,
              year: parseInt(this.selectedYear),
              quarter: quarter,
              ...this.fillQuarterForm
            }
            
            await saveOperationQuarter(data)
            
            this.quarters[quarterIndex].status = 'filled'
            this.fillQuarterDialog.visible = false
            this.$message.success('填报成功')
            this.loadQuarterData()
            this.getParkInfo()
          } catch (error) {
            this.$message.error('保存失败')
          }
        } else {
          this.$message.error('请填写所有必填项')
        }
      })
    },

    getUploadUrl() {
      const parkId = this.userInfo.parkId
      return `/api/operation-quarter/upload/enterprise-list?parkId=${parkId}`
    },

    beforeEnterpriseListUpload(file) {
      const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
      if (!isExcel) {
        this.$message.error('只支持Excel文件（.xlsx或.xls格式）')
        return false
      }
      return true
    },

    async handleDownloadTemplate() {
      try {
        const res = await downloadEnterpriseListTemplate()
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '产业发展数据模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        this.$message.error('下载失败')
      }
    },

    handleEnterpriseListUploadSuccess(response) {
      if (response.code === 200) {
        const data = response.data
        this.fillQuarterForm.currentEnterprises = data.totalCount || this.fillQuarterForm.currentEnterprises
        this.fillQuarterForm.highTechCount = data.highTechCount || ''
        this.fillQuarterForm.hiddenChampionCount = data.hiddenChampionCount || ''
        this.fillQuarterForm.nationalSrtiCount = data.nationalSrtiCount || ''
        this.fillQuarterForm.innovativeSmeCount = data.innovativeSmeCount || ''
        this.fillQuarterForm.provincialSrtiCount = data.provincialSrtiCount || ''
        this.fillQuarterForm.enterpriseListFileName = '企业名单.xlsx'
        this.$message.success('解析成功，已自动统计企业数量和荣誉类型')
      } else {
        this.$message.error(response.message || '解析失败')
      }
    },

    handleFileUploadError() {
      this.$message.error('上传失败，请重试')
    },

    clearFile() {
      this.fillQuarterForm.enterpriseListFileId = ''
      this.fillQuarterForm.enterpriseListFileName = ''
      this.fillQuarterForm.enterpriseListFileUrl = ''
    },

    handleSaveStats() {
      this.$message.success('运营数据保存成功')
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

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.year-selector {
  display: flex;
  align-items: center;
}

.year-select {
  width: 160px;
}

.quarter-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.quarter-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.quarter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.quarter-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.status-tag.filled {
  background: #e8f5e9;
  color: #67c23a;
}

.status-tag.pending {
  background: #fff8e1;
  color: #e6a23c;
}

.quarter-footer {
  display: flex;
  justify-content: flex-end;
}

.stats-table-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stats-table {
  width: 100%;
}

.stats-table /deep/ .el-table__header th {
  background: #fafafa;
  font-weight: 500;
}

.trend-icon {
  font-size: 12px;
  margin-left: 4px;
}

.trend-icon.up {
  color: #67c23a;
}

.trend-icon.down {
  color: #e6a23c;
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

.fill-quarter-dialog /deep/ .el-dialog__header {
  border-bottom: 1px solid #f0f0f0;
}

.fill-quarter-dialog /deep/ .el-dialog__title {
  font-size: 16px;
  font-weight: 600;
}

.form-section {
  margin-bottom: 24px;
}

.image-upload-container {
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

.form-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.section-icon {
  color: #409eff;
  font-size: 16px;
  margin-right: 8px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-item-group {
  flex: 1;
}

.form-input {
  width: 100%;
}

.upload-area {
  display: flex;
  gap: 12px;
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

.uploaded-file i {
  color: #409eff;
}

.uploaded-file button {
  margin-left: auto;
  color: #f56c6c;
}

.parse-result {
  margin-top: 12px;
  padding: 12px 16px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 14px;
  color: #1f2937;
  line-height: 1.8;
}

.parse-result strong {
  color: #409eff;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  width: 80px;
}

@media screen and (max-width: 768px) {
  .fill-quarter-dialog /deep/ .el-dialog {
    width: 95% !important;
    margin: 0 auto;
  }
  .form-row {
    flex-direction: column;
    gap: 12px;
  }
}
</style>