<template>
  <div class="stat-card" @click="$emit('click')">
    <div class="stat-header">
      <div class="stat-icon" :class="color">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path v-if="icon === 'building'" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
          <path v-else-if="icon === 'shop'" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"></path>
          <path v-else-if="icon === 'user'" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
          <path v-else-if="icon === 'chart'" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
          <path v-else d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z"></path>
        </svg>
      </div>
      <span class="stat-change" :class="trendClass" v-if="trend !== undefined">
        {{ trend > 0 ? '+' : '' }}{{ trend }}%
      </span>
    </div>
    <div class="stat-value">{{ formattedValue }}</div>
    <div class="stat-label">{{ label }}</div>
  </div>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    value: { type: [Number, String], required: true },
    label: { type: String, required: true },
    icon: { type: String, default: 'chart' },
    color: { type: String, default: 'blue' },
    trend: { type: Number, default: undefined },
    prefix: { type: String, default: '' },
    suffix: { type: String, default: '' },
    decimals: { type: Number, default: 0 }
  },
  computed: {
    formattedValue() {
      const num = Number(this.value)
      if (isNaN(num)) return this.value
      return this.prefix + num.toFixed(this.decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',') + this.suffix
    },
    trendClass() {
      return this.trend >= 0 ? 'up' : 'down'
    }
  }
}
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #E8EDF5;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border-color: #1E40AF;
}
.stat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-icon svg {
  width: 20px;
  height: 20px;
}
.stat-icon.blue { background: #EFF6FF; color: #1E40AF; }
.stat-icon.green { background: #ECFDF5; color: #059669; }
.stat-icon.orange { background: #FFFBEB; color: #D97706; }
.stat-icon.red { background: #FEF2F2; color: #DC2626; }
.stat-change {
  font-size: 12px;
  font-weight: 500;
  padding: 3px 8px;
  border-radius: 6px;
}
.stat-change.up { color: #059669; background: #ECFDF5; }
.stat-change.down { color: #DC2626; background: #FEF2F2; }
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  font-variant-numeric: tabular-nums;
  line-height: 1.1;
  margin-bottom: 6px;
}
.stat-label {
  font-size: 14px;
  color: #6B7280;
}
</style>
