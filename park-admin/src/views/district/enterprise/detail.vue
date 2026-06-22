<template>
  <div class="enterprise-detail-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span class="breadcrumb-item">入驻企业</span>
      <span class="breadcrumb-separator">></span>
      <span class="breadcrumb-item active">企业详情</span>
    </div>

    <!-- 企业名称和标签 -->
    <el-card class="title-card" shadow="never">
      <div class="title-content">
        <h2 class="enterprise-name">{{ enterpriseData.enterpriseName || '企业详情' }}</h2>
        <div class="enterprise-tags">
          <el-tag v-if="enterpriseData.isHighTech === 1" type="success">国高</el-tag>
          <el-tag v-if="enterpriseData.isLittleGiant === 1" type="primary">小巨人</el-tag>
          <el-tag v-if="enterpriseData.isLeading === 1" type="warning">领军</el-tag>
          <el-tag v-if="enterpriseData.isProvincialTech === 1" type="info">省科</el-tag>
          <el-tag v-if="enterpriseData.isProvincialSme === 1" type="danger">省中小企业</el-tag>
        </div>
      </div>
    </el-card>

    <!-- 工商基本信息 -->
    <el-card class="info-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">工商基本信息</span>
      </div>
      <div class="info-grid">
        <!-- 第一列 -->
        <div class="info-column">
          <div class="info-row">
            <label class="info-label">企业名称</label>
            <span class="info-value">{{ enterpriseData.enterpriseName || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">企业地址</label>
            <span class="info-value">{{ enterpriseData.address || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">法定代表人</label>
            <span class="info-value">{{ enterpriseData.legalPerson || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">注册日期</label>
            <span class="info-value">{{ enterpriseData.registerDate || '-' }}</span>
          </div>
        </div>

        <!-- 第二列 -->
        <div class="info-column">
          <div class="info-row">
            <label class="info-label">统一信用代码</label>
            <span class="info-value">{{ enterpriseData.unifiedCreditCode || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">所属产业</label>
            <span class="info-value">{{ enterpriseData.industryName || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">联系人</label>
            <span class="info-value">{{ enterpriseData.contactName || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">经营范围</label>
            <span class="info-value">{{ enterpriseData.businessScope || '-' }}</span>
          </div>
        </div>

        <!-- 第三列 -->
        <div class="info-column">
          <div class="info-row">
            <label class="info-label">所属区域</label>
            <span class="info-value">{{ enterpriseData.district || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">企业状态</label>
            <span class="info-value">{{ enterpriseData.status || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">联系人电话</label>
            <span class="info-value">{{ enterpriseData.contactPhone || '-' }}</span>
          </div>
        </div>

        <!-- 第四列 -->
        <div class="info-column">
          <div class="info-row">
            <label class="info-label">所属园区</label>
            <span class="info-value">{{ enterpriseData.parkName || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">入驻时间</label>
            <span class="info-value">{{ enterpriseData.entryTime || '-' }}</span>
          </div>
          <div class="info-row">
            <label class="info-label">注册资本</label>
            <span class="info-value">{{ enterpriseData.registeredCapital || '-' }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 备注 -->
    <el-card class="info-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">备注</span>
      </div>
      <div class="remark-content">
        {{ enterpriseData.remark || '-' }}
      </div>
    </el-card>

    <!-- 变更记录 -->
    <el-card class="info-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">变更记录</span>
      </div>
      <div class="table-container">
        <el-table :data="changeRecords" border stripe style="width: 100%;" max-height="300">
          <el-table-column prop="changeDate" label="变更日期" width="150" align="center" />
          <el-table-column prop="changeType" label="变更类型" width="120" align="center" />
          <el-table-column prop="changeBefore" label="变更前" min-width="200" />
          <el-table-column prop="changeAfter" label="变更后" min-width="200" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'DistrictEnterpriseDetail',
  data() {
    return {
      enterpriseData: {},
      changeRecords: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      // 模拟数据
      this.enterpriseData = {
        enterpriseName: '杭州创新科技有限公司',
        unifiedCreditCode: '91330100MA27YXYM23',
        district: '西湖区',
        parkName: '创新科技园',
        address: '杭州市西湖区文三路188号',
        industryName: '芯片',
        status: '在营',
        entryTime: '2015-12-18',
        legalPerson: '张一峰',
        contactName: '徐二丽',
        contactPhone: '188****5666',
        registeredCapital: '1000万元',
        registerDate: '2012-12-08',
        businessScope: '从事智能设备的研发、生产和销售',
        remark: '从事智能设备的研发、生产和销售',
        isHighTech: 1,
        isLittleGiant: 1,
        isLeading: 1,
        isProvincialTech: 1,
        isProvincialSme: 1
      }

      this.changeRecords = [
        {
          changeDate: '2025-11-06',
          changeType: '经营范围',
          changeBefore: '',
          changeAfter: '从事智能设备的研发、生产和销售'
        },
        {
          changeDate: '2022-08-16',
          changeType: '企业地址',
          changeBefore: '浙江省杭州市滨江区西兴街道江陵路88号3幢',
          changeAfter: '浙江省杭州市滨江区西兴街道江陵路88号3幢'
        },
        {
          changeDate: '2021-10-02',
          changeType: '企业地址',
          changeBefore: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园3号楼',
          changeAfter: '浙江省杭州市滨江区西兴街道江陵路88号万轮科技园3号楼'
        }
      ]
    }
  }
}
</script>

<style scoped>
.enterprise-detail-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
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

.title-card {
  margin-bottom: 16px;
  background: #fff;
  padding: 16px;
}

.title-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.enterprise-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.enterprise-tags {
  display: flex;
  gap: 8px;
}

.enterprise-tags .el-tag {
  padding: 2px 8px;
  font-size: 12px;
}

.info-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
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
  margin-bottom: 6px;
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

.info-value {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}

.remark-content {
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
  min-height: 80px;
  line-height: 1.6;
  color: #606266;
  font-size: 14px;
}

.table-container {
  overflow-x: auto;
}

.table-container .el-table {
  --el-table-header-text-color: #606266;
  --el-table-row-hover-bg-color: #f5f7fa;
}

@media screen and (max-width: 1200px) {
  .info-grid {
    flex-wrap: wrap;
  }

  .info-column {
    width: calc(50% - 12px);
  }
}

@media screen and (max-width: 768px) {
  .info-column {
    width: 100%;
  }
}
</style>
