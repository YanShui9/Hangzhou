<template>
  <div class="park-detail">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <el-button @click="goBack" icon="el-icon-arrow-left">返回列表</el-button>
    </div>

    <!-- 园区基本信息 -->
    <el-card class="info-card" shadow="never">
      <div class="card-title">园区详情</div>
      
      <div class="info-grid">
        <!-- 基本信息 -->
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-row">
            <span class="label">园区名称</span>
            <span class="value">{{ parkInfo.parkName }}</span>
          </div>
          <div class="info-row">
            <span class="label">园区地址</span>
            <span class="value">{{ parkInfo.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">园区状态</span>
            <span class="value">
              <el-tag :type="parkInfo.status === '已运营' ? 'success' : 'warning'">
                {{ parkInfo.status }}
              </el-tag>
            </span>
          </div>
          <div class="info-row">
            <span class="label">所属区域</span>
            <span class="value">{{ parkInfo.districtName }}</span>
          </div>
          <div class="info-row">
            <span class="label">园区类型</span>
            <span class="value">{{ parkTypeLabel }}</span>
          </div>
          <div class="info-row">
            <span class="label">主导产业</span>
            <span class="value">{{ parkInfo.mainIndustry }}</span>
          </div>
        </div>

        <!-- 土地性质 -->
        <div class="info-section">
          <h3 class="section-title">土地性质</h3>
          <div class="info-row">
            <span class="label">土地性质</span>
            <span class="value">{{ parkInfo.landNature }}</span>
          </div>
          <div class="info-row">
            <span class="label">开发模式</span>
            <span class="value">{{ parkInfo.developmentModel }}</span>
          </div>
          <div class="info-row">
            <span class="label">改造提升内容</span>
            <span class="value">{{ parkInfo.improvementContent }}</span>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="info-section">
          <h3 class="section-title">联系方式</h3>
          <div class="info-row">
            <span class="label">运营单位</span>
            <span class="value">{{ parkInfo.operator }}</span>
          </div>
          <div class="info-row">
            <span class="label">运营性质</span>
            <span class="value">{{ parkInfo.operationNature }}</span>
          </div>
          <div class="info-row">
            <span class="label">负责人</span>
            <span class="value">{{ parkInfo.responsiblePerson }}</span>
          </div>
          <div class="info-row">
            <span class="label">负责人电话</span>
            <span class="value">{{ parkInfo.responsiblePhone }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系人</span>
            <span class="value">{{ parkInfo.contactPerson }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系人电话</span>
            <span class="value">{{ parkInfo.contactPhone }}</span>
          </div>
        </div>

        <!-- 园区面积 -->
        <div class="info-section">
          <h3 class="section-title">园区面积</h3>
          <div class="info-row">
            <span class="label">实际用地面积（亩）</span>
            <span class="value">{{ parkInfo.landArea }} 亩</span>
          </div>
          <div class="info-row">
            <span class="label">已建建筑面积（平方米）</span>
            <span class="value">{{ parkInfo.constructedArea }} 平方米</span>
          </div>
          <div class="info-row">
            <span class="label">园区已租面积（平方米）</span>
            <span class="value">{{ parkInfo.rentedArea }} 平方米</span>
          </div>
          <div class="info-row">
            <span class="label">园区剩余可租面积（平方米）</span>
            <span class="value">{{ remainingArea }} 平方米</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 入驻企业统计 -->
    <el-card class="stats-card" shadow="never">
      <div class="card-title">入驻企业</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.enterpriseCount || 0 }}</div>
          <div class="stat-label">入驻企业总数（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.aboveScaleCount || 0 }}</div>
          <div class="stat-label">规模以上企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.highTechCount || 0 }}</div>
          <div class="stat-label">高新技术企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.scienceTypeCount || 0 }}</div>
          <div class="stat-label">科技型中小企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.stateOwnedCount || 0 }}</div>
          <div class="stat-label">随园驻区国有企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.smallGiantCount || 0 }}</div>
          <div class="stat-label">国家专精特新"小巨人"企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.creativeCount || 0 }}</div>
          <div class="stat-label">创新型中小企业（家）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.provinceSpecialCount || 0 }}</div>
          <div class="stat-label">省专精特新中小企业（家）</div>
        </div>
      </div>
    </el-card>

    <!-- 入驻员工统计 -->
    <el-card class="stats-card" shadow="never">
      <div class="card-title">入驻员工</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.employeeCount || 0 }}</div>
          <div class="stat-label">入驻企业员工总数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.phdCount || 0 }}</div>
          <div class="stat-label">"博士"人才人数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.masterCount || 0 }}</div>
          <div class="stat-label">"硕士"人才人数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.seniorEngineerCount || 0 }}</div>
          <div class="stat-label">正高级工程师人数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.highEngineerCount || 0 }}</div>
          <div class="stat-label">高级工程师人数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.middleEngineerCount || 0 }}</div>
          <div class="stat-label">中级职称以上人数（人）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.bachelorCount || 0 }}</div>
          <div class="stat-label">硕士以上人数（人）</div>
        </div>
      </div>
    </el-card>

    <!-- 创新专利 -->
    <el-card class="stats-card" shadow="never">
      <div class="card-title">创新专利</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.patentCount || 0 }}</div>
          <div class="stat-label">专利拥有量（件）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.inventionPatentCount || 0 }}</div>
          <div class="stat-label">发明专利（件）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.utilityModelCount || 0 }}</div>
          <div class="stat-label">实用新型专利（件）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ parkInfo.designPatentCount || 0 }}</div>
          <div class="stat-label">外观设计专利（件）</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getParkDetail } from '@/api/park'

export default {
  name: 'ParkDetail',
  data() {
    return {
      parkInfo: {}
    }
  },
  computed: {
    parkTypeLabel() {
      const typeMap = {
        1: '生产性制造类',
        2: '生产性服务类'
      }
      return typeMap[this.parkInfo.parkType] || this.parkInfo.parkType || '-'
    },
    remainingArea() {
      const constructed = Number(this.parkInfo.constructedArea) || 0
      const rented = Number(this.parkInfo.rentedArea) || 0
      return constructed - rented
    }
  },
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.fetchParkDetail(id)
    }
  },
  methods: {
    async fetchParkDetail(id) {
      try {
        const res = await getParkDetail(id)
        this.parkInfo = res.data || {}
      } catch (e) {
        console.error('获取园区详情失败:', e)
        this.$message.error('获取园区详情失败')
      }
    },
    goBack() {
      this.$router.push('/district/park')
    }
  }
}
</script>

<style scoped>
.park-detail {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.info-card {
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.info-section {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ddd;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  font-size: 13px;
  color: #909399;
  min-width: 120px;
}

.value {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  text-align: right;
  flex: 1;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1E40AF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
}

@media (max-width: 1200px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
