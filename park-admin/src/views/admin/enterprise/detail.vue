<template>
  <div class="enterprise-detail-container">
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
          <span class="info-label">经营范围</span>
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
      info: {}
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
        })
        .catch(() => {
          this.$message.error('加载企业详情失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    goBack() {
      this.$router.push('/admin/enterprise')
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
