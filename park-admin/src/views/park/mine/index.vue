<template>
  <div class="park-mine-container">
    <el-card v-loading="loading" class="park-info-card" shadow="never">
      <div slot="header" class="card-header">
        <span>园区基本信息</span>
        <el-tag v-if="parkInfo.starLevel" type="warning" size="small">
          {{ parkInfo.starLevel }}星园区
        </el-tag>
        <el-tag v-else type="info" size="small">未评定</el-tag>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="园区名称">{{ parkInfo.parkName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="园区类型">
          {{ parkInfo.parkType === 1 ? '制造类' : parkInfo.parkType === 2 ? '服务类' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属区县">{{ parkInfo.districtName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="星级">
          <span v-if="parkInfo.starLevel">{{ parkInfo.starLevel }}星</span>
          <span v-else>未评定</span>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ parkInfo.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="已建面积">{{ parkInfo.buildArea ? parkInfo.buildArea.toFixed(2) + ' 亩' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="用地面积">{{ parkInfo.landArea ? parkInfo.landArea.toFixed(2) + ' 亩' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ parkInfo.contactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ parkInfo.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="园区简介" :span="2">{{ parkInfo.introduction || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parkInfo.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parkInfo.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 园区统计数据 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #409eff;">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.enterpriseCount || 0 }}</div>
              <div class="stat-label">入驻企业数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #67c23a;">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.employeeCount || 0 }}</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <i class="el-icon-medal"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.patentCount || 0 }}</div>
              <div class="stat-label">专利总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getParkDetail } from '@/api/park'
import { mapGetters } from 'vuex'

export default {
  name: 'ParkMine',
  data() {
    return {
      loading: false,
      parkInfo: {},
      stats: {
        enterpriseCount: 0,
        employeeCount: 0,
        patentCount: 0
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.getParkInfo()
  },
  methods: {
    /** 获取园区信息 */
    getParkInfo() {
      const parkId = this.userInfo.parkId
      if (!parkId) {
        this.$message.warning('未关联园区，请联系管理员')
        return
      }
      this.loading = true
      getParkDetail(parkId).then(res => {
        this.parkInfo = res.data
      }).catch(() => {
        this.$message.error('获取园区信息失败')
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.park-mine-container {
  padding: 20px;
}

.park-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-row {
  margin-top: 20px;
}

.stat-card {
  cursor: default;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 30px;
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
