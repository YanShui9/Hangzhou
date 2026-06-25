<template>
  <el-dialog
    :title="'文件预览 - ' + fileName"
    :visible.sync="dialogVisible"
    width="90%"
    top="5vh"
    :close-on-click-modal="true"
    :before-close="handleClose"
    custom-class="file-preview-dialog"
    append-to-body
  >
    <div v-loading="loading" style="min-height: 400px;">
      <!-- Excel 预览 -->
      <div v-show="previewType === 'excel'" :id="excelContainerId" style="width: 100%; height: 70vh;"></div>
      <!-- Word 文档预览 -->
      <div v-show="previewType === 'docx'" :id="docxContainerId" style="width: 100%; height: 70vh; overflow: auto; background: #f5f5f5;"></div>
      <!-- PDF 预览 -->
      <iframe
        v-show="previewType === 'pdf'"
        :src="blobUrl"
        style="width: 100%; height: 70vh; border: none;"
      />
      <!-- 图片预览 -->
      <div v-show="previewType === 'image'" style="text-align: center;">
        <img :src="blobUrl" style="max-width: 100%; max-height: 70vh; object-fit: contain;" />
      </div>
      <!-- 其他文件类型 -->
      <div v-show="previewType === 'other'" style="text-align: center; padding: 40px 0;">
        <p style="color: #909399; margin-bottom: 16px;">该文件类型不支持在线预览，请下载后查看</p>
        <el-button type="primary" size="small" @click="downloadFile">下载文件</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import LuckyExcel from 'luckyexcel'
import { renderAsync } from 'docx-preview'

export default {
  name: 'FilePreview',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    fileUrl: {
      type: String,
      default: ''
    },
    fileName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      previewType: 'other',
      blobUrl: '',
      excelContainerId: 'fp-excel-' + this._uid,
      docxContainerId: 'fp-docx-' + this._uid
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible(val) {
      if (val && this.fileUrl) {
        this.$nextTick(() => {
          this.previewFile()
        })
      } else if (!val) {
        this.cleanup()
      }
    }
  },
  beforeDestroy() {
    this.cleanup()
  },
  methods: {
    async previewFile() {
      this.loading = true
      this.previewType = 'other'
      this.cleanup()

      try {
        const response = await fetch(this.fileUrl)
        if (!response.ok) {
          throw new Error('文件加载失败')
        }
        const blob = await response.blob()
        const arrayBuffer = await blob.arrayBuffer()
        const ext = (this.fileName || '').toLowerCase()

        if (ext.endsWith('.xlsx') || ext.endsWith('.xls')) {
          this.previewType = 'excel'
          this.$nextTick(() => {
            this.renderExcel(arrayBuffer)
          })
        } else if (ext.endsWith('.docx')) {
          this.previewType = 'docx'
          this.$nextTick(() => {
            const container = document.getElementById(this.docxContainerId)
            renderAsync(blob, container, null, {
              className: 'docx-preview',
              inWrapper: true,
              ignoreWidth: false,
              ignoreHeight: false,
              breakPages: true,
              experimental: true
            }).catch(err => {
              console.error('Word文档预览失败', err)
              this.$message.error('Word文档预览失败，请下载后查看')
              this.previewType = 'other'
              this.blobUrl = window.URL.createObjectURL(blob)
            })
          })
        } else if (ext.endsWith('.doc')) {
          this.previewType = 'other'
          this.blobUrl = window.URL.createObjectURL(blob)
        } else if (ext.endsWith('.pdf')) {
          this.previewType = 'pdf'
          this.blobUrl = window.URL.createObjectURL(blob)
        } else if (ext.endsWith('.png') || ext.endsWith('.jpg') || ext.endsWith('.jpeg') || ext.endsWith('.gif') || ext.endsWith('.svg')) {
          this.previewType = 'image'
          this.blobUrl = window.URL.createObjectURL(blob)
        } else {
          this.previewType = 'other'
          this.blobUrl = window.URL.createObjectURL(blob)
        }
      } catch (e) {
        console.error('预览失败', e)
        this.$message.error('预览失败，请稍后重试')
        this.dialogVisible = false
      } finally {
        this.loading = false
      }
    },
    renderExcel(arrayBuffer) {
      const file = new File([arrayBuffer], this.fileName || 'preview.xlsx')
      LuckyExcel.transformExcelToLucky(file, (exportJson) => {
        if (!exportJson || !exportJson.sheets || exportJson.sheets.length === 0) {
          this.$message.error('无法解析Excel文件')
          this.dialogVisible = false
          return
        }
        window.luckysheet.create({
          container: this.excelContainerId,
          data: exportJson.sheets,
          title: exportJson.info?.name || '文件预览',
          showinfobar: false,
          showsheetbar: true,
          showstatisticBar: false,
          allowCopy: false,
          allowEdit: false,
          showtoolbar: false,
          showConfigWindowResize: false,
          showsheetbarConfig: { add: false, menu: false },
          hook: {}
        })
      })
    },
    downloadFile() {
      if (this.blobUrl) {
        const a = document.createElement('a')
        a.href = this.blobUrl
        a.download = this.fileName || 'download'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
      }
    },
    cleanup() {
      if (window.luckysheet) {
        try {
          window.luckysheet.destroy()
        } catch (e) {
          // ignore
        }
      }
      const docxContainer = document.getElementById(this.docxContainerId)
      if (docxContainer) {
        docxContainer.innerHTML = ''
      }
      if (this.blobUrl) {
        window.URL.revokeObjectURL(this.blobUrl)
        this.blobUrl = ''
      }
    },
    handleClose() {
      this.cleanup()
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
/deep/ .file-preview-dialog .el-dialog__body {
  padding: 10px 20px;
}
</style>
