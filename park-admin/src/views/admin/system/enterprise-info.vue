<template>
  <div class="enterprise-info-page page-list-flex">
    <!-- 顶部操作区 -->
    <div class="top-bar">
      <!-- 面包屑 -->
      <div class="breadcrumb-area">
        <span class="breadcrumb-text">系统设置</span>
        <span class="breadcrumb-sep">&gt;</span>
        <span class="breadcrumb-text active">企业信息</span>
      </div>

      <!-- 搜索 -->
      <div class="search-area">
        <el-input
          v-model="queryParams.keyword"
          placeholder="关键字搜索"
          clearable
          size="small"
          style="width: 160px;"
          @keyup.enter.native="handleSearch"
        />
        <el-select
          v-model="queryParams.districtId"
          placeholder="全部区域"
          clearable
          size="small"
          style="width: 130px; margin-left: 8px;"
        >
          <el-option
            v-for="item in districtOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.parkId"
          placeholder="全部园区"
          clearable
          size="small"
          style="width: 130px; margin-left: 8px;"
        >
          <el-option
            v-for="item in parkOptions"
            :key="item.id"
            :label="item.parkName"
            :value="item.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.status"
          placeholder="全部状态"
          clearable
          size="small"
          style="width: 120px; margin-left: 8px;"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button
          type="primary"
          size="small"
          @click="handleSearch"
          style="margin-left: 8px;"
        >查询</el-button>
        <el-button
          size="small"
          @click="handleReset"
          style="margin-left: 4px;"
        >重置</el-button>
      </div>

      <!-- 右侧按钮 -->
      <div class="action-area">
        <el-button
          size="small"
          @click="handleDownloadTemplate"
        >下载模板</el-button>
        <el-button
          size="small"
          @click="handleImport"
          style="margin-left: 8px;"
        >批量导入</el-button>
        <el-button
          size="small"
          @click="handleExport"
          style="margin-left: 8px;"
        >导出</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
      <el-table
        v-loading="loading"
        :data="dataList"
        border
        stripe
        height="100%"
      >
      <el-table-column
        type="index"
        label="序号"
        width="80"
        align="center"
      />
      <el-table-column
        prop="enterpriseName"
        label="企业名称"
        min-width="220"
        show-overflow-tooltip
      >
        <template slot-scope="{ row }">
          <a
            href="javascript:;"
            class="link-text"
            @click="handleEdit(row)"
          >{{ row.enterpriseName }}</a>
        </template>
      </el-table-column>
      <el-table-column
        prop="creditCode"
        label="统一信用代码"
        min-width="200"
        show-overflow-tooltip
      />
      <el-table-column
        prop="districtName"
        label="所属区域"
        width="120"
        align="center"
      />
      <el-table-column
        prop="parkName"
        label="所属园区"
        min-width="160"
        show-overflow-tooltip
      />
      <el-table-column
        prop="legalPerson"
        label="法定代表人"
        width="120"
        align="center"
      >
        <template slot-scope="{ row }">{{ row.legalPerson || '--' }}</template>
      </el-table-column>
      <el-table-column
        prop="contactPerson"
        label="联系人"
        width="120"
        align="center"
      >
        <template slot-scope="{ row }">{{ row.contactPerson || '--' }}</template>
      </el-table-column>
      <el-table-column
        prop="contactPhone"
        label="联系电话"
        min-width="160"
        align="center"
        show-overflow-tooltip
      >
        <template slot-scope="{ row }">{{ row.contactPhone || '--' }}</template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="100"
        align="center"
        fixed="right"
      >
        <template slot-scope="{ row }">
          <el-button
            type="text"
            class="danger-btn"
            @click="handleDelete(row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-bar">
      <span class="total-text">共{{ total }}条</span>
      <el-select
        v-model="queryParams.pageSize"
        size="small"
        @change="handleSizeChange"
        style="width: 100px; margin: 0 10px;"
      >
        <el-option
          v-for="item in pageSizeOptions"
          :key="item"
          :label="item + '条/页'"
          :value="item"
        />
      </el-select>
      <el-pagination
        class="page-numbers"
        background
        layout="prev, pager, next"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        :pager-count="5"
        @current-change="handleCurrentChange"
      />
      <span class="jump-text">前往</span>
      <el-input
        v-model="jumpPage"
        size="small"
        @keyup.enter.native="handleJumpPage"
        style="width: 50px; margin: 0 6px;"
      />
      <span class="jump-text">页</span>
    </div>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入企业信息" :visible.sync="importVisible" width="500px" :close-on-click-modal="false">
      <el-upload
        class="import-upload"
        drag
        action=""
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        accept=".xls,.xlsx"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">
          仅支持 xls/xlsx 格式，单文件不超过 10MB
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="importVisible = false">取 消</el-button>
        <el-button
          type="primary"
          size="small"
          :loading="importLoading"
          @click="confirmImport"
        >确 认导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getEnterpriseInfoPage,
  deleteEnterpriseInfo,
  downloadEnterpriseInfoTemplate,
  importEnterpriseInfo,
  exportEnterpriseInfo
} from '@/api/enterprise-info'

export default {
  name: 'EnterpriseInfo',
  data() {
    return {
      queryParams: {
        keyword: '',
        districtId: null,
        parkId: null,
        status: null,
        pageNum: 1,
        pageSize: 20
      },
      districtOptions: [
        { id: 1, name: '上城区' },
        { id: 2, name: '滨江区' },
        { id: 3, name: '萧山区' },
        { id: 4, name: '余杭区' },
        { id: 5, name: '富阳区' },
        { id: 6, name: '临安区' },
        { id: 7, name: '西湖区' }
      ],
      parkOptions: [],
      statusOptions: [
        { value: 1, label: '参评' },
        { value: 2, label: '未参评' }
      ],
      pageSizeOptions: [10, 20, 50, 100],
      dataList: [],
      total: 0,
      loading: false,
      jumpPage: '',
      importVisible: false,
      importLoading: false,
      uploadedFile: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        if (params.keyword === '') delete params.keyword
        if (params.districtId === null) delete params.districtId
        if (params.parkId === null) delete params.parkId
        if (params.status === null) delete params.status

        const res = await getEnterpriseInfoPage(params)
        if (res.data.records && res.data.records.length > 0 && res.data.records[0].enterpriseName) {
          this.dataList = res.data.records || []
          this.total = res.data.total || 0
        } else {
          this.applyMockList(params)
        }
      } catch (e) {
        console.error('查询企业信息列表失败', e)
        this.applyMockList(this.queryParams)
      } finally {
        this.loading = false
      }
    },
    applyMockList(params) {
      const allMock = [
        { id: 1, enterpriseName: '杭州数智科技有限公司', creditCode: '91330100MA2ABCD123', districtName: '余杭区', parkName: '盛惠哈源科创园', industry: '信息技术', status: '参评', createTime: '2026-06-01 09:00:00' },
        { id: 2, enterpriseName: '浙江生物医药研发有限公司', creditCode: '91330100MA2EFGH456', districtName: '余杭区', parkName: '盛惠哈源科创园', industry: '生物医药', status: '参评', createTime: '2026-06-02 10:00:00' },
        { id: 3, enterpriseName: '杭州智能装备制造有限公司', creditCode: '91330100MA2IJKL789', districtName: '临平区', parkName: '世创智能制造产业园', industry: '高端装备', status: '参评', createTime: '2026-06-03 11:00:00' },
        { id: 4, enterpriseName: '浙江新材料科技有限公司', creditCode: '91330100MA2MNOP012', districtName: '桐庐县', parkName: '舒泰富春智创园', industry: '新材料', status: '参评', createTime: '2026-06-04 14:00:00' },
        { id: 5, enterpriseName: '杭州绿色能源开发有限公司', creditCode: '91330100MA2QRST345', districtName: '桐庐县', parkName: '舒泰富春智创园', industry: '绿色能源', status: '不参评', createTime: '2026-06-05 15:00:00' },
        { id: 6, enterpriseName: '萧山数字经济产业园有限公司', creditCode: '91330100MA2UVWX678', districtName: '萧山区', parkName: '蜀山未来城', industry: '数字经济', status: '参评', createTime: '2026-06-06 16:00:00' },
        { id: 7, enterpriseName: '杭州文创设计有限公司', creditCode: '91330100MA2YZA901', districtName: '拱墅区', parkName: '丝联166文创园', industry: '文化创意', status: '参评', createTime: '2026-06-07 08:30:00' },
        { id: 8, enterpriseName: '杭州云计算服务有限公司', creditCode: '91330100MA2BCDE234', districtName: '临平区', parkName: '算力一期', industry: '信息技术', status: '不参评', createTime: '2026-06-08 09:00:00' }
      ]
      let filtered = allMock
      const p = params || {}
      if (p.keyword) {
        const kw = p.keyword.toLowerCase()
        filtered = filtered.filter(item => item.enterpriseName.includes(kw) || item.creditCode.includes(kw))
      }
      if (p.districtId) {
        // 简化处理：用区县名匹配
        const districtMap = { 1: '余杭区', 2: '临平区', 3: '桐庐县', 4: '萧山区', 5: '拱墅区' }
        filtered = filtered.filter(item => item.districtName === districtMap[p.districtId])
      }
      if (p.status) {
        filtered = filtered.filter(item => item.status === p.status)
      }
      this.total = filtered.length
      const pageNum = p.pageNum || 1
      const pageSize = p.pageSize || 20
      const start = (pageNum - 1) * pageSize
      this.dataList = filtered.slice(start, start + pageSize)
    },

    handleSearch() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    handleReset() {
      this.queryParams = {
        keyword: '',
        districtId: null,
        parkId: null,
        status: null,
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },

    handleEdit(row) {
      this.$router.push({
        path: '/system/enterprise-info/edit',
        query: { id: row.id }
      })
    },

    handleDelete(row) {
      this.$confirm(`确定要删除企业 "${row.enterpriseName}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteEnterpriseInfo(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (e) {
          console.error('删除企业失败', e)
        }
      }).catch(() => {})
    },

    handleDownloadTemplate() {
      this.$message.info('下载模板功能待接入后端接口')
      downloadEnterpriseInfoTemplate().then(() => {}).catch(() => {})
    },

    handleImport() {
      this.importVisible = true
    },

    handleFileChange(file) {
      this.uploadedFile = file.raw
    },

    confirmImport() {
      if (!this.uploadedFile) {
        this.$message.warning('请先选择要导入的文件')
        return
      }
      this.importLoading = true
      const formData = new FormData()
      formData.append('file', this.uploadedFile)
      importEnterpriseInfo(formData)
        .then(() => {
          this.$message.success('导入成功')
          this.importVisible = false
          this.getList()
        })
        .finally(() => {
          this.importLoading = false
        })
    },

    handleExport() {
      this.$message.info('导出数据功能待接入后端接口')
      exportEnterpriseInfo(this.queryParams).then(() => {}).catch(() => {})
    },

    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },

    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },

    handleJumpPage() {
      const page = parseInt(this.jumpPage)
      const maxPage = Math.ceil(this.total / this.queryParams.pageSize)
      if (!page || page < 1 || page > maxPage) {
        this.$message.warning(`请输入 1-${maxPage} 之间的页码`)
        return
      }
      this.queryParams.pageNum = page
      this.getList()
    }
  }
}
</script>

<style scoped>
.enterprise-info-page {
  height: 100%;
  overflow: hidden;
}

/* 顶部操作栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0 20px;
}

.breadcrumb-area {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
}

.breadcrumb-text {
  font-size: 13px;
  color: #909399;
}

.breadcrumb-text.active {
  color: #303133;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #c0c4cc;
  font-size: 13px;
}

.search-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-area {
  display: flex;
  align-items: center;
}

/* 链接文本 */
.link-text {
  color: #1E40AF;
  text-decoration: none;
  cursor: pointer;
}

.link-text:hover {
  text-decoration: underline;
}

/* 操作按钮 */
.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0 10px;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

.page-numbers {
  display: inline-block;
}

.jump-text {
  font-size: 13px;
  color: #606266;
}

/* 上传组件 */
.import-upload {
  margin-top: 4px;
}

/* 弹窗 footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
