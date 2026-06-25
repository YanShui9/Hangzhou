<template>
  <el-dialog
    title="审核记录"
    :visible.sync="dialogVisible"
    width="640px"
    :close-on-click-modal="false"
    :show-close="true"
    class="audit-timeline-dialog"
  >
    <div class="timeline-wrapper">
      <div v-if="!history || history.length === 0" class="timeline-empty">
        <i class="el-icon-document"></i>
        <span>暂无审核记录</span>
      </div>
      <div v-else class="timeline-list">
        <div
          v-for="(item, index) in history"
          :key="index"
          :class="['timeline-item', { 'timeline-item--active': item.active }]"
        >
          <div class="timeline-connector" v-if="index < history.length - 1"></div>
          <div :class="['timeline-dot', terminalClass(item.terminal), item.active ? 'timeline-dot--active' : '']"></div>
          <div class="timeline-card">
            <div class="card-header">
              <span :class="['terminal-badge', 'badge--' + item.terminal]">{{ item.terminalName }}</span>
              <span class="action-name">{{ item.actionName }}</span>
            </div>
            <div class="card-body">
              <div class="actor-row">
                <i class="el-icon-user"></i>
                <span class="actor-name">{{ item.actorName || '-' }}</span>
              </div>
              <div v-if="item.previousStatus && item.currentStatus" class="status-change-row">
                <i class="el-icon-arrow-right"></i>
                <span class="status-badge status-badge--prev">{{ item.previousStatus }}</span>
                <span class="status-arrow">→</span>
                <span :class="['status-badge', 'status-badge--curr', statusClass(item.currentStatus)]">{{ item.currentStatus }}</span>
              </div>
              <div v-if="item.opinion" class="opinion-row">
                <i class="el-icon-chat-line-round"></i>
                <span class="opinion-text">{{ item.opinion }}</span>
              </div>
            </div>
            <div class="card-footer">
              <i class="el-icon-time"></i>
              <span class="time-text">{{ formatTime(item.time) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="small" @click="dialogVisible = false">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'AuditTimeline',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    history: {
      type: Array,
      default: () => []
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
  methods: {
    terminalClass(terminal) {
      if (terminal === 'park') return 'park'
      if (terminal === 'district') return 'district'
      if (terminal === 'city') return 'city'
      return ''
    },
    statusClass(status) {
      if (status === '待区县审' || status === '待市局审') return 'pending'
      if (status === '审核通过') return 'pass'
      if (status === '已驳回') return 'reject'
      return ''
    },
    formatTime(time) {
      if (!time) return '-'
      if (typeof time === 'string') {
        // 已经是字符串格式 "2026-06-25 10:30:00"
        return time.replace('T', ' ').substring(0, 19)
      }
      if (time instanceof Date) {
        const y = time.getFullYear()
        const m = String(time.getMonth() + 1).padStart(2, '0')
        const d = String(time.getDate()).padStart(2, '0')
        const h = String(time.getHours()).padStart(2, '0')
        const mi = String(time.getMinutes()).padStart(2, '0')
        const s = String(time.getSeconds()).padStart(2, '0')
        return `${y}-${m}-${d} ${h}:${mi}:${s}`
      }
      // LocalDateTime 等格式
      try {
        return String(time).replace('T', ' ').substring(0, 19)
      } catch (e) {
        return String(time)
      }
    }
  }
}
</script>

<style scoped>
.timeline-wrapper {
  max-height: 500px;
  overflow-y: auto;
  padding: 8px 4px;
}

.timeline-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #909399;
  gap: 8px;
}

.timeline-empty i {
  font-size: 32px;
  color: #dcdfe6;
}

.timeline-list {
  position: relative;
  padding-left: 8px;
}

.timeline-item {
  position: relative;
  padding-left: 32px;
  padding-bottom: 24px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-connector {
  position: absolute;
  left: 11px;
  top: 24px;
  bottom: 0;
  width: 2px;
  background: #e8e8e8;
}

.timeline-dot {
  position: absolute;
  left: 4px;
  top: 6px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #d9d9d9;
  border: 3px solid #fff;
  box-shadow: 0 0 0 2px #d9d9d9;
  z-index: 1;
}

.timeline-dot--active {
  box-shadow: 0 0 0 2px #67c23a;
}

/* 端颜色 */
.timeline-dot.park {
  background: #409EFF;
  box-shadow: 0 0 0 2px #409EFF;
}

.timeline-dot.park.timeline-dot--active {
  background: #67c23a;
  box-shadow: 0 0 0 2px #67c23a;
}

.timeline-dot.district {
  background: #e6a23c;
  box-shadow: 0 0 0 2px #e6a23c;
}

.timeline-dot.district.timeline-dot--active {
  background: #67c23a;
  box-shadow: 0 0 0 2px #67c23a;
}

.timeline-dot.city {
  background: #f56c6c;
  box-shadow: 0 0 0 2px #f56c6c;
}

.timeline-dot.city.timeline-dot--active {
  background: #67c23a;
  box-shadow: 0 0 0 2px #67c23a;
}

.timeline-card {
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px 14px;
  transition: box-shadow 0.2s;
}

.timeline-item--active .timeline-card {
  background: #f0f9eb;
  border-color: #c2e7b0;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.terminal-badge {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  color: #fff;
}

.badge--park {
  background: #409EFF;
}

.badge--district {
  background: #e6a23c;
}

.badge--city {
  background: #f56c6c;
}

.action-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 8px;
}

.actor-row,
.status-change-row,
.opinion-row {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.actor-row i,
.status-change-row i,
.opinion-row i {
  color: #909399;
  margin-top: 2px;
  flex-shrink: 0;
}

.actor-name {
  font-weight: 500;
  color: #303133;
}

.status-badge {
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge--prev {
  background: #f4f4f5;
  color: #909399;
}

.status-badge--curr.pending {
  background: #ecf5ff;
  color: #409EFF;
}

.status-badge--curr.pass {
  background: #f0f9eb;
  color: #67c23a;
}

.status-badge--curr.reject {
  background: #fef0f0;
  color: #f56c6c;
}

.status-arrow {
  color: #c0c4cc;
  font-size: 12px;
}

.opinion-text {
  color: #606266;
  line-height: 1.6;
  background: #fff;
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  width: 100%;
  box-sizing: border-box;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #c0c4cc;
  padding-top: 4px;
  border-top: 1px dashed #ebeef5;
}

.card-footer i {
  margin: 0;
  color: #c0c4cc;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
