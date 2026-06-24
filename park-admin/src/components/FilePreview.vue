<template>
  <el-dialog
    :title="'文件预览 - ' + fileName"
    width="90%"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="preview-container">
      <div v-if="loading" class="loading-state">
        <el-loading :text="'正在加载 ' + fileName" />
      </div>

      <div v-else-if="error" class="error-state">
        <i class="el-icon-error error-icon"></i>
        <p>{{ error }}</p>
      </div>

      <div v-else-if="fileType === 'excel'" class="excel-view">
        <div v-if="excelSheets.length > 0">
          <el-tabs v-model="activeSheet" type="card">
            <el-tab-pane
              v-for="(sheet, idx) in excelSheets"
              :key="idx"
              :label="sheet.name"
            >
              <el-table :data="sheet.data" border size="small" max-height="500">
                <el-table-column
                  v-for="(col, ci) in sheet.columns"
                  :key="ci"
                  :prop="col.key"
                  :label="col.label"
                  min-width="100"
                />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <div v-else-if="fileType === 'pdf'" class="pdf-view">
        <div v-if="pdfPages.length > 0" class="pdf-container">
          <div
            v-for="(page, idx) in pdfPages"
            :key="idx"
            class="pdf-page"
          >
            <canvas :ref="el => setPdfCanvas(idx, el)" class="pdf-canvas"></canvas>
          </div>
        </div>
      </div>

      <div v-else-if="fileType === 'image'" class="image-view">
        <img :src="fullUrl" alt="预览" class="preview-img" />
      </div>

      <div v-else-if="fileType === 'word'" class="word-view">
        <div v-html="wordHtml" class="word-content"></div>
      </div>

      <div v-else-if="fileType === 'text'" class="text-view">
        <pre class="text-content">{{ textContent }}</pre>
      </div>

      <div v-else class="iframe-view">
        <iframe :src="fullUrl" class="preview-iframe" frameborder="0"></iframe>
      </div>
    </div>

    <div slot="footer" class="preview-footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="downloadFile">下载原文件</el-button>
    </div>
  </el-dialog>
</template>

<script>
import * as XLSX from 'xlsx'
import mammoth from 'mammoth'
import * as pdfjsLib from 'pdfjs-dist'
import 'pdfjs-dist/build/pdf.worker.entry'

export default {
  name: 'FilePreview',
  props: {
    visible: { type: Boolean, default: false },
    fileUrl: { type: String, default: '' },
    fileName: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      error: '',
      fileType: '',
      excelSheets: [],
      activeSheet: '0',
      textContent: '',
      wordHtml: '',
      pdfPages: [],
      pdfDocument: null,
      pdfCanvasRefs: []
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
    },
    ext() {
      if (!this.fileName) return ''
      return this.fileName.split('.').pop().toLowerCase()
    },
    fullUrl() {
      if (!this.fileUrl) return ''
      if (this.fileUrl.startsWith('http')) return this.fileUrl
      return window.location.origin + this.fileUrl
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.loadFile()
      } else {
        this.reset()
      }
    }
  },
  methods: {
    async loadFile() {
      if (!this.fullUrl) {
        this.error = '文件地址不存在'
        return
      }

      this.loading = true
      this.error = ''

      const extMap = {
        xlsx: 'excel', xls: 'excel',
        pdf: 'pdf',
        jpg: 'image', jpeg: 'image', png: 'image', gif: 'image',
        docx: 'word', doc: 'word',
        txt: 'text', md: 'text', json: 'text'
      }

      this.fileType = extMap[this.ext] || 'other'

      try {
        if (this.fileType === 'excel') {
          await this.loadExcel()
        } else if (this.fileType === 'word') {
          await this.loadWord()
        } else if (this.fileType === 'pdf') {
          await this.loadPdf()
        } else if (this.fileType === 'text') {
          await this.loadText()
        }
      } catch (err) {
        this.error = '文件加载失败：' + (err.message || '未知错误')
      } finally {
        this.loading = false
      }
    },
    async loadExcel() {
      try {
        const response = await fetch(this.fullUrl)
        const arrayBuffer = await response.arrayBuffer()
        const workbook = XLSX.read(arrayBuffer, { type: 'array' })

        this.excelSheets = workbook.SheetNames.map((name, idx) => {
          const worksheet = workbook.Sheets[name]
          const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })

          if (jsonData.length === 0) {
            return { name, columns: [], data: [] }
          }

          const headers = jsonData[0]
          const columns = headers.map((header, ci) => ({
            key: 'col' + ci,
            label: String(header || '列' + (ci + 1))
          }))

          const data = jsonData.slice(1).map((row, ri) => {
            const rowData = {}
            row.forEach((cell, ci) => {
              rowData['col' + ci] = cell || ''
            })
            return rowData
          }).filter(row => Object.values(row).some(v => v))

          return { name, columns, data }
        })

        this.activeSheet = '0'
      } catch (e) {
        this.error = 'Excel解析失败：' + (e.message || '未知错误')
      }
    },
    async loadWord() {
      try {
        const response = await fetch(this.fullUrl)
        const arrayBuffer = await response.arrayBuffer()

        const result = await mammoth.extractRawText({ arrayBuffer })
        const text = result.value

        const paragraphs = text.split('\n').filter(p => p.trim())
        this.wordHtml = paragraphs.map(p => '<p>' + p + '</p>').join('') || '<p>文档内容为空</p>'
      } catch (e) {
        this.error = 'Word解析失败：' + (e.message || '未知错误')
      }
    },
    async loadPdf() {
      try {
        pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.worker.min.js'

        const response = await fetch(this.fullUrl)
        const arrayBuffer = await response.arrayBuffer()

        this.pdfDocument = await pdfjsLib.getDocument({ data: arrayBuffer }).promise
        this.pdfPages = Array.from({ length: this.pdfDocument.numPages }, (_, i) => i + 1)

        await this.$nextTick()
        await this.renderPdfPages()
      } catch (e) {
        this.error = 'PDF解析失败：' + (e.message || '未知错误')
      }
    },
    async renderPdfPages() {
      if (!this.pdfDocument) return

      for (let i = 0; i < this.pdfPages.length; i++) {
        const page = await this.pdfDocument.getPage(this.pdfPages[i])
        const canvas = this.pdfCanvasRefs[i]

        if (canvas) {
          const viewport = page.getViewport({ scale: 1.5 })
          canvas.width = viewport.width
          canvas.height = viewport.height

          const context = canvas.getContext('2d')
          await page.render({
            canvasContext: context,
            viewport
          }).promise
        }
      }
    },
    setPdfCanvas(index, el) {
      if (el) {
        this.pdfCanvasRefs[index] = el
      }
    },
    async loadText() {
      const response = await fetch(this.fullUrl)
      this.textContent = await response.text()
    },
    downloadFile() {
      const link = document.createElement('a')
      link.href = this.fullUrl
      link.download = this.fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    reset() {
      this.loading = false
      this.error = ''
      this.excelSheets = []
      this.textContent = ''
      this.wordHtml = ''
      this.pdfPages = []
      this.pdfDocument = null
      this.pdfCanvasRefs = []
    },
    handleClose() {
      this.reset()
    }
  }
}
</script>

<style scoped>
.preview-container {
  height: calc(80vh - 120px);
  overflow: auto;
}

.loading-state, .error-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.error-icon {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 16px;
}

.error-state {
  flex-direction: column;
  color: #f56c6c;
}

.excel-view {
  padding: 16px;
}

.pdf-view {
  padding: 16px;
}

.pdf-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pdf-page {
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pdf-canvas {
  max-width: 100%;
  height: auto;
}

.preview-iframe {
  width: 100%;
  height: 100%;
}

.image-view {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.preview-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.word-view {
  padding: 20px;
  height: 100%;
  overflow: auto;
}

.word-content {
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
}

.word-content p {
  margin-bottom: 12px;
}

.text-view {
  padding: 20px;
}

.text-content {
  white-space: pre-wrap;
  word-break: break-all;
  font-family: 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
}

.preview-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>