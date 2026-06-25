<template>
  <div class="enterprise-detail">
    <!-- 顶部标题栏 -->
    <div class="detail-header">
      <div class="header-left">
        <span class="title">企业详情</span>
      </div>
    </div>

    <!-- 返回与标题 -->
    <div class="detail-nav">
      <div class="back-btn" @click="goBack">
        <i class="el-icon-back" />
        <span>返回列表</span>
      </div>
    </div>
    <div class="detail-title">
      <h2>{{ enterprise.enterpriseName || '-' }}</h2>
    </div>

    <!-- 工商基本信息 -->
    <div class="detail-section">
      <div class="section-bar">
        <span class="section-title">工商基本信息</span>
      </div>

      <div v-loading="loading" class="section-body info-grid">
        <template v-if="!loading && enterprise.enterpriseName">
          <div class="info-item">
            <div class="info-label">企业名称</div>
            <div class="info-value">{{ enterprise.enterpriseName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">统一信用代码</div>
            <div class="info-value">{{ enterprise.creditCode || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">所属区域</div>
            <div class="info-value">{{ enterprise.districtName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">所属园区</div>
            <div class="info-value">{{ enterprise.parkName || '-' }}</div>
          </div>

          <div class="info-item">
            <div class="info-label">企业地址</div>
            <div class="info-value">{{ enterprise.address || enterprise.registeredAddress || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">所属产业</div>
            <div class="info-value">{{ enterprise.industry || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">企业经营状态</div>
            <div class="info-value">{{ enterprise.registerStatus || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">入驻时间</div>
            <div class="info-value">{{ enterprise.entryStartTime || '-' }}</div>
          </div>

          <div class="info-item">
            <div class="info-label">法定代表人</div>
            <div class="info-value">{{ enterprise.legalPerson || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">联系人</div>
            <div class="info-value">{{ enterprise.contactName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">联系人电话</div>
            <div class="info-value">{{ enterprise.contactPhone || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">注册资本</div>
            <div class="info-value">{{ formatCapital(enterprise.registeredCapital) }}</div>
          </div>

          <div class="info-item">
            <div class="info-label">注册日期</div>
            <div class="info-value">{{ formatDate(enterprise.registerDate) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">员工人数</div>
            <div class="info-value">{{ enterprise.employeeCount || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">年产值(万元)</div>
            <div class="info-value">{{ formatNumber(enterprise.annualOutput) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">年纳税额(万元)</div>
            <div class="info-value">{{ formatNumber(enterprise.annualTax) }}</div>
          </div>

          <div class="info-item">
            <div class="info-label">租赁面积(㎡)</div>
            <div class="info-value">{{ formatNumber(enterprise.rentArea) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">租赁时间</div>
            <div class="info-value">{{ enterprise.rentTime || '-' }}</div>
          </div>
        </template>

        <el-empty v-else-if="!loading" description="未找到企业信息" />
      </div>

      <!-- 经营范围 -->
      <div class="business-scope">
        <div class="scope-label">经营范围</div>
        <div class="scope-text">
          {{ enterprise.businessScope || '-' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getEnterpriseDetailForPark } from '@/api/enterprise'

export default {
  name: 'ParkEnterpriseDetail',
  data() {
    return {
      loading: false,
      enterprise: {}
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      this.fetchDetail(id)
    }
  },
  methods: {
    fetchDetail(id) {
      this.loading = true
      getEnterpriseDetail(id).then(response => {
        this.enterprise = response.data || {}
      }).catch(() => {
        this.enterprise = {}
        this.$message.error('获取企业详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    goBack() {
      this.$router.push({ name: 'ParkEnterprise' })
    },
    formatDate(val) {
      if (!val) return '-'
      if (typeof val === 'string') {
        return val.length > 10 ? val.substring(0, 10) : val
      }
      return val
    },
    formatNumber(val) {
      if (val === null || val === undefined || val === '') return '-'
      return val
    },
    formatCapital(val) {
      if (val === null || val === undefined || val === '') return '-'
      return `${val}万元人民币`
    }
  }
}
</script>

<style scoped>
.enterprise-detail {
  padding: 16px 20px 30px;
  min-height: 100%;
  box-sizing: border-box;
}

/* 顶部标题栏 */
.detail-header {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 14px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.header-left .title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 返回导航 */
.detail-nav {
  background: #fff;
  padding: 14px 20px 0;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  transition: color 0.2s;
}

.back-btn:hover {
  color: #409eff;
}

.back-btn .el-icon-back {
  font-size: 16px;
}

/* 企业名称标题 */
.detail-title {
  background: #fff;
  padding: 12px 20px 24px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.detail-title h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  word-break: break-all;
}

/* 工商基本信息 */
.detail-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  padding: 20px 24px 24px;
}

.section-bar {
  position: relative;
  padding-left: 12px;
  margin-bottom: 24px;
}

.section-bar::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: #409eff;
  border-radius: 2px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px 32px;
  min-height: 200px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.info-label {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  line-height: 1.6;
  word-break: break-all;
}

/* 经营范围 */
.business-scope {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.scope-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
}

.scope-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.8;
  word-break: break-all;
  background: #fafafa;
  border-radius: 6px;
  padding: 16px 20px;
  border: 1px solid #f0f0f0;
  white-space: pre-wrap;
}

/* 响应式 */
@media screen and (max-width: 1200px) {
  .info-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 992px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .user-info .user-name {
    max-width: 100px;
  }
}

@media screen and (max-width: 768px) {
  .enterprise-detail {
    padding: 10px;
  }
  .info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .detail-title h2 {
    font-size: 18px;
  }
  .detail-header {
    padding: 12px 14px;
  }
  .detail-section {
    padding: 16px;
  }
  .user-info .user-name {
    display: none;
  }
  .scope-text {
    padding: 12px 14px;
  }
}

@media screen and (max-width: 480px) {
  .header-right {
    gap: 12px;
  }
  .detail-title {
    padding: 10px 14px 18px;
  }
  .detail-title h2 {
    font-size: 16px;
  }
}
</style>
