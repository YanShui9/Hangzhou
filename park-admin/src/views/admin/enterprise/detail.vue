<template>
  <div class="enterprise-detail-container">
    <!-- 面包屑 -->
    <div class="breadcrumb-bar">
      <span class="breadcrumb-link" @click="goBack">入驻企业</span>
      <i class="el-icon-arrow-right breadcrumb-sep"></i>
      <span class="breadcrumb-current">企业详情</span>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <el-icon class="el-icon-loading"></el-icon>
      <span class="loading-text">加载中...</span>
    </div>

    <!-- 主体内容 -->
    <div v-else class="detail-main">
      <!-- 企业名称 + 荣誉标签 -->
      <div class="header-card">
        <h2 class="enterprise-name">{{ info.enterpriseName || '--' }}</h2>
        <div class="honor-tags">
          <el-tag
            v-for="(tag, index) in honorTags"
            :key="index"
            size="mini"
            effect="plain"
          >{{ tag }}</el-tag>
        </div>
      </div>

      <!-- 工商基本信息 -->
      <div class="info-card">
        <div class="card-title">工商基本信息</div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label required">企业名称</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.enterpriseName || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">统一信用代码</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.creditCode || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">所属区域</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.districtName || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">所属园区</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.parkName || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">企业地址</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.address || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">所属产业</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.industryName || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">企业状态</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.status || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">入驻时间</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.entryDate || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">法定代表人</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.legalPerson || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">联系人</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.contactName || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">联系人电话</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.contactPhone || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">注册资本</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.registeredCapital || '--' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="info-label required">注册日期</span>
            <div class="info-value-wrap">
              <span class="info-value">{{ info.registerDate || '--' }}</span>
            </div>
          </div>
        </div>

        <!-- 经营范围 -->
        <div class="info-section">
          <span class="info-label required">经营范围</span>
          <div class="info-textarea">
            <span class="textarea-content">{{ info.businessScope || '--' }}</span>
          </div>
        </div>

        <!-- 备注 -->
        <div class="info-section">
          <span class="info-label">备注</span>
          <div class="info-textarea">
            <span class="textarea-content">{{ info.remark || '--' }}</span>
          </div>
        </div>
      </div>

      <!-- 变更记录 -->
      <div class="info-card">
        <div class="card-title">变更记录</div>
        <el-table :data="changeRecords" border stripe size="mini" class="change-table" style="width: 100%">
          <el-table-column prop="changeDate" label="变更日期" width="140" align="center" />
          <el-table-column prop="changeType" label="变更类型" width="140" align="center" />
          <el-table-column prop="beforeChange" label="变更前" min-width="200" />
          <el-table-column prop="afterChange" label="变更后" min-width="200" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getEnterpriseDetail } from '@/api/enterprise'

export default {
  name: 'AdminEnterpriseDetail',
  data() {
    return {
      loading: false,
      info: {},
      changeRecords: []
    }
  },
  computed: {
    honorTags() {
      const honor = this.info.enterpriseHonor || ''
      if (!honor) return []
      return honor.split('/').map(t => t.trim()).filter(t => t)
    }
  },
  created() {
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      const id = this.$route.params.id
      if (!id) {
        this.$message.warning('企业ID不存在')
        this.goBack()
        return
      }
      this.loading = true
      getEnterpriseDetail(id)
        .then(res => {
          this.info = res.data || {}
          this.changeRecords = (res.data && res.data.changeRecords) || this.buildMockChangeRecords()
        })
        .catch(() => {
          this.$message.error('加载企业详情失败')
          this.changeRecords = this.buildMockChangeRecords()
        })
        .finally(() => {
          this.loading = false
        })
    },
    goBack() {
      this.$router.push('/admin/enterprise')
    },
    buildMockChangeRecords() {
      return [
        {
          id: 1,
          changeDate: '2025-11-08',
          changeType: '经营范围',
          beforeChange: '从事智能设备的研发和销售',
          afterChange: '从事智能设备的研发、生产和销售'
        },
        {
          id: 2,
          changeDate: '2025-08-16',
          changeType: '企业地址',
          beforeChange: '浙江省杭州市滨江区西兴街道江陵路88号3幢',
          afterChange: '浙江省杭州市滨江区西兴街道江陵路88号3幢'
        },
        {
          id: 3,
          changeDate: '2022-08-16',
          changeType: '企业地址',
          beforeChange: '浙江省杭州市滨江区西兴街道江陵路88号3幢',
          afterChange: '浙江省杭州市滨江区西兴街道江陵路88号3幢'
        },
        {
          id: 4,
          changeDate: '2021-10-02',
          changeType: '企业地址',
          beforeChange: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园9号楼',
          afterChange: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园9号楼'
        },
        {
          id: 5,
          changeDate: '2021-10-02',
          changeType: '企业地址',
          beforeChange: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园9号楼',
          afterChange: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园9号楼'
        }
      ]
    }
  }
}
</script>

<style scoped>
.enterprise-detail-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 56px);
}

/* 面包屑 */
.breadcrumb-bar {
  background: #FFFFFF;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6B7280;
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

/* 加载 */
.loading-wrap {
  background: #FFFFFF;
  padding: 80px 0;
  text-align: center;
  color: #6B7280;
}

.loading-wrap .el-icon-loading {
  color: #1E40AF;
  font-size: 28px;
}

.loading-text {
  display: block;
  margin-top: 12px;
  font-size: 13px;
}

/* 主体内容 */
.detail-main {
  background: #FFFFFF;
  border-radius: 4px;
  padding: 20px;
}

/* 头部卡片 - 企业名称 + 荣誉标签 */
.header-card {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #E8EDF5;
}

.enterprise-name {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px 0;
}

.honor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.honor-tags >>> .el-tag {
  background: #EEF2FF;
  border-color: #C7D2FE;
  color: #4F46E5;
  font-size: 12px;
  padding: 4px 10px;
}

/* 信息卡片 */
.info-card {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px 20px;
  margin-bottom: 12px;
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  padding: 0 0 12px;
  border-bottom: 1px solid #F3F4F6;
  margin-bottom: 16px;
}

/* 信息网格 - 4列 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.info-label {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 6px;
}

.info-label.required::before {
  content: '*';
  color: #DC2626;
  margin-right: 2px;
}

.info-value-wrap {
  border: 1px solid #D9D9D9;
  border-radius: 4px;
  padding: 8px 12px;
  background: #FFFFFF;
}

.info-value {
  font-size: 13px;
  color: #1F2937;
  word-break: break-all;
  line-height: 1.5;
}

/* 信息区域（文本框） */
.info-section {
  margin-top: 16px;
}

.info-section .info-label {
  display: block;
  margin-bottom: 8px;
}

.info-textarea {
  border: 1px solid #D9D9D9;
  border-radius: 4px;
  padding: 12px;
  min-height: 72px;
  background: #FFFFFF;
}

.textarea-content {
  font-size: 13px;
  color: #1F2937;
  line-height: 1.6;
  word-break: break-all;
}

/* 变更记录表格 */
.change-table >>> .el-table__header th {
  background: #FAFBFC;
  color: #303133;
  font-weight: 600;
  font-size: 12px;
  text-align: center;
}

.change-table >>> .el-table__body td {
  font-size: 12px;
  color: #606266;
}

.change-table >>> .el-table__row--striped td {
  background: #FAFCFF;
}

.change-table >>> .el-table__row:hover > td {
  background: #F0F4FF !important;
}

/* 响应式 - 3列 */
@media (max-width: 1400px) {
  .info-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 响应式 - 2列 */
@media (max-width: 1024px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 响应式 - 1列 */
@media (max-width: 640px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
