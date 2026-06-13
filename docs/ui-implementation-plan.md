# UI 设计系统实施计划

> 将设计规范落地到 Vue 2 + ElementUI 项目的具体步骤

---

## 📋 实施概览

| 阶段 | 内容 | 预计时间 | 优先级 |
|------|------|----------|--------|
| Phase 1 | 基础样式变量 | 2小时 | P0 |
| Phase 2 | ElementUI 主题定制 | 3小时 | P0 |
| Phase 3 | 全局组件优化 | 4小时 | P1 |
| Phase 4 | 数据看板优化 | 6小时 | P1 |
| Phase 5 | 响应式适配 | 4小时 | P2 |
| Phase 6 | 动画与交互 | 3小时 | P2 |
| Phase 7 | 可访问性优化 | 2小时 | P3 |

**总计：约 24 小时**

---

## Phase 1: 基础样式变量（P0）

### 目标
建立全局 SCSS 变量系统，统一设计语言

### 步骤

#### 1.1 创建变量文件结构

```
src/styles/
├── variables/
│   ├── _colors.scss      # 配色变量
│   ├── _typography.scss  # 字体变量
│   ├── _spacing.scss     # 间距变量
│   ├── _radius.scss      # 圆角变量
│   ├── _shadow.scss      # 阴影变量
│   └── _animation.scss   # 动画变量
├── mixins/
│   ├── _responsive.scss  # 响应式 mixin
│   └── _typography.scss  # 文字 mixin
├── base/
│   ├── _reset.scss       # 样式重置
│   └── _global.scss      # 全局基础样式
├── element-override.scss # ElementUI 主题覆盖
└── index.scss           # 统一入口
```

#### 1.2 编写变量文件

**`variables/_colors.scss`**
```scss
// 主色
$primary-50: #EBF5FF;
$primary-100: #D6EBFF;
$primary-200: #ADD6FF;
$primary-300: #85C1FF;
$primary-400: #5CACFF;
$primary-500: #409EFF;
$primary-600: #337ECC;
$primary-700: #265F99;
$primary-800: #1A3F66;
$primary-900: #0D2033;

// 语义色
$success: #67C23A;
$warning: #E6A23C;
$danger: #F56C6C;
$info: #909399;

// 文字色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #C0C4CC;

// 背景色
$bg-page: #F5F7FA;
$bg-card: #FFFFFF;
$bg-hover: #F5F7FA;
$bg-selected: #ECF5FF;

// 边框色
$border-lighter: #EBEEF5;
$border-light: #E4E7ED;
$border-base: #DCDFE6;

// 图表色板
$chart-colors: (
  'blue': #409EFF,
  'green': #67C23A,
  'orange': #E6A23C,
  'red': #F56C6C,
  'purple': #9B59B6,
  'cyan': #1ABC9C,
  'dark': #34495E,
  'gray': #95A5A6
);
```

**`variables/_typography.scss`**
```scss
// 字体栈
$font-family-base: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
$font-family-mono: 'SF Mono', 'Fira Code', 'Consolas', 'Monaco', monospace;

// 字号
$font-size-xs: 12px;
$font-size-sm: 13px;
$font-size-base: 14px;
$font-size-md: 16px;
$font-size-lg: 18px;
$font-size-xl: 20px;
$font-size-2xl: 24px;
$font-size-3xl: 30px;

// 行高
$line-height-tight: 1.25;
$line-height-normal: 1.5;
$line-height-relaxed: 1.75;

// 字重
$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;
```

**`variables/_spacing.scss`**
```scss
$space-1: 4px;
$space-2: 8px;
$space-3: 12px;
$space-4: 16px;
$space-5: 20px;
$space-6: 24px;
$space-8: 32px;
$space-10: 40px;
$space-12: 48px;
```

**`variables/_radius.scss`**
```scss
$radius-none: 0;
$radius-sm: 2px;
$radius-base: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;
$radius-full: 9999px;
```

**`variables/_shadow.scss`**
```scss
$shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
$shadow-base: 0 2px 4px rgba(0, 0, 0, 0.08);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
$shadow-xl: 0 12px 48px rgba(0, 0, 0, 0.15);
$shadow-blue: 0 4px 12px rgba(64, 158, 255, 0.3);
```

**`variables/_animation.scss`**
```scss
$duration-fast: 150ms;
$duration-normal: 250ms;
$duration-slow: 350ms;
$duration-slower: 500ms;

$ease-in: cubic-bezier(0.4, 0, 1, 1);
$ease-out: cubic-bezier(0, 0, 0.2, 1);
$ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);
```

#### 1.3 创建统一入口

**`styles/index.scss`**
```scss
// 变量
@import './variables/colors';
@import './variables/typography';
@import './variables/spacing';
@import './variables/radius';
@import './variables/shadow';
@import './variables/animation';

// Mixin
@import './mixins/responsive';
@import './mixins/typography';

// 基础样式
@import './base/reset';
@import './base/global';

// ElementUI 覆盖
@import './element-override';
```

#### 1.4 在 Vue 中引入

**`vue.config.js`**
```javascript
module.exports = {
  css: {
    loaderOptions: {
      scss: {
        additionalData: `
          @import "@/styles/variables/_colors.scss";
          @import "@/styles/variables/_typography.scss";
          @import "@/styles/variables/_spacing.scss";
          @import "@/styles/variables/_radius.scss";
          @import "@/styles/variables/_shadow.scss";
          @import "@/styles/variables/_animation.scss";
        `
      }
    }
  }
};
```

---

## Phase 2: ElementUI 主题定制（P0）

### 目标
覆盖 ElementUI 默认样式，统一视觉风格

### 步骤

#### 2.1 创建 ElementUI 覆盖文件

**`styles/element-override.scss`**
```scss
// ============================================
// ElementUI 主题覆盖
// ============================================

// 颜色变量
$--color-primary: #409EFF;
$--color-success: #67C23A;
$--color-warning: #E6A23C;
$--color-danger: #F56C6C;
$--color-info: #909399;

// 文字颜色
$--color-text-primary: #303133;
$--color-text-regular: #606266;
$--color-text-secondary: #909399;
$--color-text-placeholder: #C0C4CC;

// 边框颜色
$--border-color-base: #DCDFE6;
$--border-color-light: #E4E7ED;
$--border-color-lighter: #EBEEF5;

// 背景颜色
$--background-color-base: #F5F7FA;

// 字体
$--font-size-base: 14px;
$--font-size-medium: 14px;
$--font-size-small: 13px;
$--font-size-mini: 12px;

// 圆角
$--border-radius-base: 4px;
$--border-radius-small: 2px;

// 引入 ElementUI 主题
@import "~element-ui/packages/theme-chalk/src/index";

// ============================================
// 自定义覆盖样式
// ============================================

// 按钮优化
.el-button {
  font-weight: 500;
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &--primary {
    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
    
    &:hover {
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
    }
  }
}

// 表格优化
.el-table {
  th {
    background-color: #F5F7FA !important;
    font-weight: 600;
    color: #303133;
  }
  
  // 紧凑模式
  &--mini {
    th, td {
      padding: 6px 0;
    }
  }
}

// 卡片优化
.el-card {
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .el-card__header {
    border-bottom: 1px solid #EBEEF5;
    padding: 16px 20px;
  }
  
  .el-card__body {
    padding: 20px;
  }
}

// 表单优化
.el-form-item__label {
  font-weight: 500;
  color: #303133;
}

.el-input__inner {
  &:focus {
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}

// 分页优化
.el-pagination {
  margin-top: 20px;
  
  .el-pager li {
    border-radius: 4px;
    
    &.active {
      box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
    }
  }
}

// 标签优化
.el-tag {
  border: none;
  font-weight: 500;
}

// 对话框优化
.el-dialog {
  border-radius: 12px;
  overflow: hidden;
  
  .el-dialog__header {
    padding: 20px 20px 16px;
    border-bottom: 1px solid #EBEEF5;
  }
  
  .el-dialog__body {
    padding: 20px;
  }
  
  .el-dialog__footer {
    padding: 16px 20px 20px;
    border-top: 1px solid #EBEEF5;
  }
}

// 消息提示优化
.el-message {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
```

#### 2.2 在 main.js 中引入

```javascript
import Vue from 'vue';
import ElementUI from 'element-ui';
import './styles/element-override.scss'; // 自定义主题
import App from './App.vue';

Vue.use(ElementUI);
```

---

## Phase 3: 全局组件优化（P1）

### 目标
创建可复用的全局组件，统一交互模式

### 步骤

#### 3.1 统计卡片组件

**`src/components/StatCard.vue`**
```vue
<template>
  <div class="stat-card" :class="colorClass" @click="$emit('click')">
    <div class="stat-card__icon" :style="iconStyle">
      <i :class="icon"></i>
    </div>
    <div class="stat-card__content">
      <div class="stat-card__value">{{ formattedValue }}</div>
      <div class="stat-card__label">{{ label }}</div>
    </div>
    <div class="stat-card__trend" v-if="trend !== undefined">
      <i :class="trendClass"></i>
      <span>{{ Math.abs(trend) }}%</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    value: { type: [Number, String], required: true },
    label: { type: String, required: true },
    icon: { type: String, default: 'el-icon-data-line' },
    color: { type: String, default: 'blue' },
    trend: { type: Number, default: undefined },
    prefix: { type: String, default: '' },
    suffix: { type: String, default: '' },
    decimals: { type: Number, default: 0 }
  },
  computed: {
    colorClass() {
      return `stat-card--${this.color}`;
    },
    iconStyle() {
      const colors = {
        blue: 'linear-gradient(135deg, #409EFF, #66B1FF)',
        green: 'linear-gradient(135deg, #67C23A, #85CE61)',
        orange: 'linear-gradient(135deg, #E6A23C, #EBB563)',
        red: 'linear-gradient(135deg, #F56C6C, #F78989)'
      };
      return { background: colors[this.color] || colors.blue };
    },
    formattedValue() {
      const num = Number(this.value);
      if (isNaN(num)) return this.value;
      return this.prefix + num.toFixed(this.decimals) + this.suffix;
    },
    trendClass() {
      return this.trend >= 0 ? 'el-icon-top trend-up' : 'el-icon-bottom trend-down';
    }
  }
};
</script>

<style scoped lang="scss">
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  &__icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    font-size: 24px;
    color: #fff;
  }
  
  &__content {
    flex: 1;
  }
  
  &__value {
    font-size: 30px;
    font-weight: 700;
    color: #303133;
    font-family: 'SF Mono', 'Consolas', monospace;
    line-height: 1.2;
  }
  
  &__label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }
  
  &__trend {
    display: flex;
    align-items: center;
    font-size: 13px;
    font-weight: 500;
    
    .trend-up {
      color: #67C23A;
    }
    
    .trend-down {
      color: #F56C6C;
    }
    
    i {
      margin-right: 4px;
    }
  }
}
</style>
```

#### 3.2 使用示例

```vue
<template>
  <el-row :gutter="16">
    <el-col :span="6">
      <stat-card
        :value="stats.totalParks"
        label="园区总数"
        icon="el-icon-office-building"
        color="blue"
        :trend="5.2"
      />
    </el-col>
    <el-col :span="6">
      <stat-card
        :value="stats.totalEnterprises"
        label="企业总数"
        icon="el-icon-s-shop"
        color="green"
        :trend="-2.1"
      />
    </el-col>
    <!-- ... -->
  </el-row>
</template>
```

---

## Phase 4: 数据看板优化（P1）

### 目标
优化三个角色的数据看板，提升数据可视化效果

### 步骤

#### 4.1 创建图表主题配置

**`src/utils/chartTheme.js`**
```javascript
export const chartTheme = {
  color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#9B59B6', '#1ABC9C'],
  
  title: {
    textStyle: {
      color: '#303133',
      fontSize: 16,
      fontWeight: 600
    }
  },
  
  legend: {
    textStyle: {
      color: '#606266',
      fontSize: 12
    }
  },
  
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  
  xAxis: {
    axisLine: { lineStyle: { color: '#DCDFE6' } },
    axisTick: { show: false },
    axisLabel: { color: '#606266', fontSize: 12 },
    splitLine: { show: false }
  },
  
  yAxis: {
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#606266', fontSize: 12 },
    splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } }
  },
  
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#E4E7ED',
    borderWidth: 1,
    textStyle: { color: '#303133', fontSize: 13 },
    extraCssText: 'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);'
  }
};

// 应用主题
export function applyChartTheme(echarts) {
  echarts.registerTheme('parkTheme', chartTheme);
}
```

#### 4.2 优化后的看板布局

```vue
<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :lg="6" v-for="card in statsCards" :key="card.key">
          <stat-card v-bind="card" />
        </el-col>
      </el-row>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="16">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <div slot="header" class="card-header">
              <span class="card-title">季度运营趋势</span>
              <el-select v-model="selectedYear" size="small" style="width: 100px;">
                <el-option label="2026年" :value="2026" />
                <el-option label="2025年" :value="2025" />
              </el-select>
            </div>
            <div ref="trendChart" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <div slot="header" class="card-header">
              <span class="card-title">园区排名 TOP 10</span>
              <el-tag type="success" size="small">按评价得分</el-tag>
            </div>
            <div ref="rankChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-section">
      <el-card>
        <div slot="header" class="card-header">
          <span class="card-title">最近评价记录</span>
          <el-button type="text" @click="$router.push('/evaluation')">查看全部</el-button>
        </div>
        <el-table :data="recentEvaluations" stripe>
          <!-- 表格列 -->
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 84px);
}

.stats-section {
  margin-bottom: 20px;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  height: 480px;
  
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  
  .chart-container {
    height: 400px;
  }
}

.table-section {
  // 表格区域样式
}
</style>
```

---

## Phase 5: 响应式适配（P2）

### 目标
确保在不同设备上都有良好的展示效果

### 步骤

#### 5.1 创建响应式 Mixin

**`src/styles/mixins/_responsive.scss`**
```scss
// 响应式断点
$breakpoints: (
  'xs': 0,
  'sm': 576px,
  'md': 768px,
  'lg': 1024px,
  'xl': 1440px,
  '2xl': 1920px
);

// 媒体查询 mixin
@mixin respond-to($breakpoint) {
  @if map-has-key($breakpoints, $breakpoint) {
    @media (min-width: map-get($breakpoints, $breakpoint)) {
      @content;
    }
  } @else {
    @warn "Unknown breakpoint: #{$breakpoint}";
  }
}

// 最大宽度
@mixin max-width($width) {
  @media (max-width: $width) {
    @content;
  }
}

// 容器
@mixin container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 20px;
  
  @include respond-to('xl') {
    padding: 0 40px;
  }
}
```

#### 5.2 响应式表格

```scss
// 移动端表格优化
@include max-width(768px) {
  .el-table {
    // 启用横向滚动
    overflow-x: auto;
    
    // 紧凑模式
    th, td {
      padding: 8px 0;
      font-size: 12px;
    }
    
    // 隐藏次要列
    .hide-mobile {
      display: none;
    }
  }
}
```

---

## Phase 6: 动画与交互（P2）

### 目标
添加流畅的微交互动画，提升用户体验

### 步骤

#### 6.1 全局过渡样式

**`src/styles/base/_animations.scss`**
```scss
// 页面切换
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

// 滑入
.slide-up-enter-active {
  transition: all 0.3s ease-out;
}

.slide-up-enter {
  opacity: 0;
  transform: translateY(20px);
}

// 列表项依次出现
.list-enter-active {
  transition: all 0.4s ease-out;
}

.list-enter {
  opacity: 0;
  transform: translateX(-30px);
}

// 数字增长动画
@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.count-up {
  animation: countUp 0.6s ease-out forwards;
}
```

#### 6.2 卡片悬停效果增强

```scss
.stat-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  &:active {
    transform: translateY(-2px) scale(1.01);
  }
}
```

---

## Phase 7: 可访问性优化（P3）

### 目标
确保平台对所有用户都可用

### 步骤

#### 7.1 焦点样式

```scss
// 焦点轮廓
:focus-visible {
  outline: 2px solid #409EFF;
  outline-offset: 2px;
}

// 按钮焦点
.el-button:focus-visible {
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3);
}

// 输入框焦点
.el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
```

#### 7.2 ARIA 标签

```vue
<!-- 图标按钮 -->
<el-button 
  icon="el-icon-edit" 
  aria-label="编辑园区信息"
>
  编辑
</el-button>

<!-- 图表 -->
<div 
  ref="chart" 
  class="chart-container"
  role="img" 
  aria-label="2026年各季度企业数量趋势图，第一季度120家，第二季度150家..."
>
</div>

<!-- 状态标签 -->
<el-tag 
  :type="statusType"
  role="status"
  :aria-label="`状态：${statusLabel}`"
>
  {{ statusLabel }}
</el-tag>
```

---

## 📝 实施顺序建议

```
Week 1:
├── Day 1-2: Phase 1 (基础变量)
├── Day 3-4: Phase 2 (ElementUI 主题)
└── Day 5: Phase 3 (全局组件)

Week 2:
├── Day 1-3: Phase 4 (数据看板)
├── Day 4: Phase 5 (响应式)
└── Day 5: Phase 6 & 7 (动画和可访问性)
```

---

## ✅ 验收标准

### 视觉一致性
- [ ] 所有页面使用统一的配色系统
- [ ] 字体层级清晰（标题 18-24px，正文 14px，辅助 12px）
- [ ] 间距规律（4px 倍数）
- [ ] 圆角统一（按钮 4px，卡片 8px，对话框 12px）

### 交互体验
- [ ] 按钮有 hover 和 active 状态
- [ ] 表格行有悬停效果
- [ ] 卡片有悬停动画
- [ ] 加载状态有明确提示

### 响应式
- [ ] 统计卡片支持 1/2/4 列切换
- [ ] 表格在小屏可横向滚动
- [ ] 图表自适应容器宽度

### 可访问性
- [ ] 文字对比度符合 WCAG AA 标准
- [ ] 可交互元素有焦点状态
- [ ] 图标按钮有 aria-label

---

*基于 UI/UX Pro Max 设计规范*
*最后更新：2026-06-12*
