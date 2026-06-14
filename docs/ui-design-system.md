# 杭州市小微园区评价数据分析平台 - UI 设计系统

> 基于 UI/UX Pro Max 设计规范，专为 Vue 2 + ElementUI + ECharts 企业级管理平台定制

---

## 1. 设计原则

| 原则 | 说明 | 应用 |
|------|------|------|
| **专业可信** | 政府级数据平台，传递权威感和信任感 | 使用稳重配色、清晰排版 |
| **高效清晰** | 数据密集型，信息一目了然 | 合理信息层级、表格优化 |
| **一致统一** | 三种角色共享视觉语言 | 统一组件样式、交互模式 |
| **易于使用** | 降低学习成本 | 符合直觉的操作反馈 |

---

## 2. 配色系统

### 2.1 主色调（Primary）

```scss
// 品牌蓝 - 专业、可信赖
$primary-50:  #EBF5FF;
$primary-100: #D6EBFF;
$primary-200: #ADD6FF;
$primary-300: #85C1FF;
$primary-400: #5CACFF;
$primary-500: #409EFF;  // ElementUI 默认主色
$primary-600: #337ECC;
$primary-700: #265F99;
$primary-800: #1A3F66;
$primary-900: #0D2033;
```

### 2.2 语义色（Semantic）

```scss
// 成功/通过 - 绿色
$success-light: #E8F5E9;
$success: #67C23A;
$success-dark: #4CAF50;

// 警告/待处理 - 橙色
$warning-light: #FFF3E0;
$warning: #E6A23C;
$warning-dark: #FF9800;

// 错误/驳回 - 红色
$error-light: #FFEBEE;
$error: #F56C6C;
$error-dark: #E53935;

// 信息/提示 - 蓝色
$info-light: #E3F2FD;
$info: #909399;
$info-dark: #607D8B;
```

### 2.3 中性色（Neutral）

```scss
// 文字层级
$text-primary: #303133;    // 主要文字
$text-regular: #606266;    // 常规文字
$text-secondary: #909399;  // 次要文字
$text-placeholder: #C0C4CC; // 占位文字

// 背景层级
$bg-page: #F5F7FA;         // 页面背景
$bg-card: #FFFFFF;         // 卡片背景
$bg-hover: #F5F7FA;        // 悬停背景
$bg-selected: #ECF5FF;     // 选中背景

// 边框层级
$border-lighter: #EBEEF5;
$border-light: #E4E7ED;
$border-base: #DCDFE6;
$border-dark: #D4D7DE;
```

### 2.4 数据可视化色板（ECharts）

```scss
// 图表色板 - 8色系，确保可访问性
$chart-colors: [
  '#409EFF',  // 蓝 - 主要数据
  '#67C23A',  // 绿 - 成功/增长
  '#E6A23C',  // 橙 - 警告/中等
  '#F56C6C',  // 红 - 错误/下降
  '#9B59B6',  // 紫 - 辅助数据
  '#1ABC9C',  // 青 - 补充数据
  '#34495E',  // 深灰 - 基准线
  '#95A5A6'   // 浅灰 - 参考线
];

// 渐变色（用于面积图、进度条）
$gradient-blue: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
$gradient-green: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
$gradient-orange: linear-gradient(135deg, #E6A23C 0%, #EBB563 100%);
```

---

## 3. 字体系统

### 3.1 字体栈

```scss
// 中文优先，系统字体兜底
$font-family-base: 
  -apple-system,
  BlinkMacSystemFont,
  'Segoe UI',
  'PingFang SC',      // macOS/iOS 中文
  'Hiragino Sans GB',  // macOS 中文
  'Microsoft YaHei',   // Windows 中文
  'Helvetica Neue',
  Helvetica,
  Arial,
  sans-serif;

// 数字/数据 - 等宽字体，对齐更好
$font-family-mono:
  'SF Mono',
  'Fira Code',
  'Consolas',
  'Monaco',
  monospace;
```

### 3.2 字号规范

```scss
// 字号层级（基于 16px 基准）
$font-size-xs: 12px;     // 辅助文字、标签
$font-size-sm: 13px;     // 次要文字
$font-size-base: 14px;   // 正文（ElementUI 默认）
$font-size-md: 16px;     // 小标题
$font-size-lg: 18px;     // 卡片标题
$font-size-xl: 20px;     // 页面标题
$font-size-2xl: 24px;    // 大标题
$font-size-3xl: 30px;    // 统计数字

// 行高
$line-height-tight: 1.25;   // 标题
$line-height-normal: 1.5;   // 正文
$line-height-relaxed: 1.75; // 长文本
```

### 3.3 字重规范

```scss
$font-weight-normal: 400;   // 正文
$font-weight-medium: 500;   // 强调
$font-weight-semibold: 600; // 小标题
$font-weight-bold: 700;     // 大标题
```

---

## 4. 间距系统

### 4.1 基础间距（4px 倍数）

```scss
$space-1: 4px;    // 最小间距
$space-2: 8px;    // 紧凑间距
$space-3: 12px;   // 表单元素间距
$space-4: 16px;   // 常规间距
$space-5: 20px;   // 卡片内间距
$space-6: 24px;   // 区块间距
$space-8: 32px;   // 大区块间距
$space-10: 40px;  // 页面级间距
$space-12: 48px;  // 特大间距
```

### 4.2 应用场景

| 场景 | 间距 | 说明 |
|------|------|------|
| 表单元素之间 | 12px | el-form-item 间距 |
| 卡片内边距 | 20px | el-card body padding |
| 卡片之间 | 16px | 卡片外边距 |
| 表格行高 | 48px | 紧凑模式 40px |
| 按钮组间距 | 12px | 按钮之间 |
| 页面边距 | 20px | 内容区 padding |

---

## 5. 圆角系统

```scss
$radius-none: 0;
$radius-sm: 2px;     // 小元素（标签、徽章）
$radius-base: 4px;   // 按钮、输入框（ElementUI 默认）
$radius-md: 8px;     // 卡片、对话框
$radius-lg: 12px;    // 大卡片、统计卡片
$radius-xl: 16px;    // 特殊卡片
$radius-full: 9999px; // 胶囊形、头像
```

---

## 6. 阴影系统

```scss
// 层级阴影
$shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
$shadow-base: 0 2px 4px rgba(0, 0, 0, 0.08);    // 卡片默认
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.1);      // 悬停状态
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);     // 弹出层
$shadow-xl: 0 12px 48px rgba(0, 0, 0, 0.15);    // 对话框

// 特殊阴影
$shadow-inset: inset 0 2px 4px rgba(0, 0, 0, 0.06);  // 内凹
$shadow-blue: 0 4px 12px rgba(64, 158, 255, 0.3);    // 主色阴影
```

---

## 7. 组件规范

### 7.1 按钮（Button）

```scss
// 主要按钮
.el-button--primary {
  font-weight: 500;
  border-radius: $radius-base;
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: $shadow-blue;
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 按钮尺寸
$btn-height-sm: 28px;   // 小按钮
$btn-height-base: 36px;  // 默认
$btn-height-lg: 44px;    // 大按钮
```

**使用规范：**
- 主要操作：`type="primary"`（提交、新增、确认）
- 次要操作：`type="default"`（取消、返回）
- 危险操作：`type="danger"`（删除、驳回）
- 文字操作：`type="text"`（表格内编辑、删除）

### 7.2 表格（Table）

```scss
// 表格优化
.el-table {
  // 表头
  th {
    background-color: #F5F7FA !important;
    font-weight: 600;
    color: $text-primary;
    font-size: $font-size-sm;
  }
  
  // 斑马纹
  tr:nth-child(even) {
    background-color: #FAFAFA;
  }
  
  // 悬停行
  tbody tr:hover > td {
    background-color: $bg-hover;
  }
  
  // 紧凑模式
  &--mini {
    th, td {
      padding: 6px 0;
    }
  }
}
```

**使用规范：**
- 数据量大时启用 `stripe` 斑马纹
- 操作列固定右侧 `fixed="right"`
- 超长文字启用 `show-overflow-tooltip`
- 数字列右对齐 `align="right"`

### 7.3 表单（Form）

```scss
// 表单标签
.el-form-item__label {
  font-weight: 500;
  color: $text-primary;
}

// 输入框
.el-input__inner {
  border-radius: $radius-base;
  transition: border-color 0.2s, box-shadow 0.2s;
  
  &:focus {
    border-color: $primary-500;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}

// 必填标记
.el-form-item.is-required:not(.is-no-asterisk) {
  .el-form-item__label::before {
    color: $error;
  }
}
```

**使用规范：**
- 标签文字简洁，4字以内
- 必填项标记 `*`
- 错误提示在输入框下方
- 复杂表单分组展示

### 7.4 卡片（Card）

```scss
// 统计卡片
.stat-card {
  background: $bg-card;
  border-radius: $radius-lg;
  padding: $space-5;
  box-shadow: $shadow-base;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }
  
  &__icon {
    width: 48px;
    height: 48px;
    border-radius: $radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
  }
  
  &__value {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    font-family: $font-family-mono;  // 数字等宽
  }
  
  &__label {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin-top: $space-1;
  }
}

// 内容卡片
.el-card {
  border-radius: $radius-md;
  border: none;
  box-shadow: $shadow-base;
  
  .el-card__header {
    padding: $space-4 $space-5;
    border-bottom: 1px solid $border-lighter;
  }
  
  .el-card__body {
    padding: $space-5;
  }
}
```

### 7.5 标签（Tag）

```scss
// 状态标签
.el-tag {
  border-radius: $radius-sm;
  font-weight: 500;
  border: none;
}

// 状态色映射
$status-draft: #909399;      // 草稿 - 灰色
$status-pending: #E6A23C;    // 待审 - 橙色
$status-approved: #67C23A;   // 通过 - 绿色
$status-rejected: #F56C6C;   // 驳回 - 红色
$status-info: #409EFF;       // 信息 - 蓝色
```

**使用规范：**
- 草稿：`type="info"`
- 待审核：`type="warning"`
- 已通过：`type="success"`
- 已驳回：`type="danger"`

### 7.6 分页（Pagination）

```scss
.el-pagination {
  margin-top: $space-5;
  padding: $space-2 0;
  
  // 页码按钮
  .el-pager li {
    min-width: 32px;
    height: 32px;
    line-height: 32px;
    border-radius: $radius-base;
    
    &.active {
      background-color: $primary-500;
    }
  }
}
```

**使用规范：**
- 默认每页 10 条
- 可选：10 / 20 / 50 / 100
- 显示总数和跳转

---

## 8. 数据看板设计规范

### 8.1 布局结构

```
┌─────────────────────────────────────────────────────┐
│                    统计卡片区域                       │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │ 园区总数 │ │ 企业总数 │ │ 就业人数 │ │ 评价得分 │   │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │
├─────────────────────────────────────────────────────┤
│                    图表区域                          │
│  ┌──────────────────────┐ ┌──────────────────────┐  │
│  │                      │ │                      │  │
│  │    趋势图（折线图）    │ │    排名表（柱状图）    │  │
│  │                      │ │                      │  │
│  └──────────────────────┘ └──────────────────────┘  │
├─────────────────────────────────────────────────────┤
│                    详情区域                          │
│  ┌──────────────────────────────────────────────┐   │
│  │              最近评价记录表格                  │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### 8.2 统计卡片规范

```vue
<template>
  <el-row :gutter="16">
    <el-col :span="6" v-for="item in statsCards" :key="item.key">
      <div class="stat-card" :class="'stat-card--' + item.color">
        <div class="stat-card__icon">
          <i :class="item.icon"></i>
        </div>
        <div class="stat-card__content">
          <div class="stat-card__value">{{ item.value }}</div>
          <div class="stat-card__label">{{ item.label }}</div>
        </div>
        <div class="stat-card__trend" v-if="item.trend">
          <i :class="item.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
          <span>{{ Math.abs(item.trend) }}%</span>
        </div>
      </div>
    </el-col>
  </el-row>
</template>
```

### 8.3 图表规范

```javascript
// ECharts 全局配置
const chartTheme = {
  // 颜色
  color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#9B59B6'],
  
  // 标题
  title: {
    textStyle: {
      color: '#303133',
      fontSize: 16,
      fontWeight: 600
    }
  },
  
  // 图例
  legend: {
    textStyle: {
      color: '#606266',
      fontSize: 12
    }
  },
  
  // 坐标轴
  xAxis: {
    axisLine: { lineStyle: { color: '#DCDFE6' } },
    axisTick: { show: false },
    axisLabel: { color: '#606266' },
    splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } }
  },
  
  // 提示框
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#E4E7ED',
    textStyle: { color: '#303133' }
  }
};
```

---

## 9. 响应式策略

### 9.1 断点定义

```scss
$breakpoint-sm: 576px;   // 手机
$breakpoint-md: 768px;   // 平板
$breakpoint-lg: 1024px;  // 小桌面
$breakpoint-xl: 1440px;  // 标准桌面
$breakpoint-2xl: 1920px; // 大桌面
```

### 9.2 响应式规则

| 屏幕 | 统计卡片 | 图表布局 | 表格 |
|------|----------|----------|------|
| < 768px | 1列 | 1列 | 横向滚动 |
| 768-1024px | 2列 | 1列 | 紧凑模式 |
| 1024-1440px | 4列 | 2列 | 标准模式 |
| > 1440px | 4列 | 2列 | 宽松模式 |

---

## 10. 可访问性规范

### 10.1 颜色对比度

- 正文文字：≥ 4.5:1（WCAG AA）
- 大文字：≥ 3:1
- 图标：≥ 3:1

### 10.2 键盘导航

- 所有交互元素可 Tab 聚焦
- 焦点状态清晰可见（蓝色轮廓）
- 支持 Enter/Space 激活

### 10.3 屏幕阅读器

```vue
<!-- 图标按钮需要 aria-label -->
<el-button icon="el-icon-edit" aria-label="编辑">
  编辑
</el-button>

<!-- 图表需要替代文本 -->
<div class="chart" role="img" aria-label="2026年各季度企业数量趋势图">
  <canvas ref="chart"></canvas>
</div>
```

---

## 11. 动画规范

### 11.1 时长定义

```scss
$duration-fast: 150ms;     // 微交互（hover、focus）
$duration-normal: 250ms;   // 状态切换
$duration-slow: 350ms;     // 页面过渡
$duration-slower: 500ms;   // 复杂动画
```

### 11.2 缓动函数

```scss
$ease-in: cubic-bezier(0.4, 0, 1, 1);      // 进入
$ease-out: cubic-bezier(0, 0, 0.2, 1);     // 退出
$ease-in-out: cubic-bezier(0.4, 0, 0.2, 1); // 进入退出
```

### 11.3 动画应用

```scss
// 卡片悬停
.stat-card {
  transition: transform 0.3s $ease-out, box-shadow 0.3s $ease-out;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }
}

// 按钮点击
.el-button {
  transition: all 0.2s $ease-out;
  
  &:active {
    transform: scale(0.98);
  }
}

// 表格行悬停
.el-table {
  tbody tr {
    transition: background-color 0.2s;
  }
}
```

---

## 12. 暗黑模式预留

```scss
// 暗黑模式变量（未来扩展）
$dark-bg-page: #141414;
$dark-bg-card: #1F1F1F;
$dark-text-primary: #E5EAF3;
$dark-text-regular: #CFD3DC;
$dark-border: #414248;

// ElementUI 暗黑模式
// 需要引入 element-theme-dark
```

---

## 13. 快速参考清单

### ✅ 设计交付前检查

**视觉质量**
- [ ] 无 emoji 作为图标
- [ ] 图标风格统一（ElementUI 内置）
- [ ] 配色符合语义（蓝=主、绿=成功、红=错误、橙=警告）
- [ ] 数字使用等宽字体

**交互体验**
- [ ] 所有可点击元素有 hover 状态
- [ ] 按钮有 disabled 状态
- [ ] 加载状态有 loading 提示
- [ ] 操作有成功/失败反馈

**响应式**
- [ ] 统计卡片支持 1/2/4 列切换
- [ ] 表格在小屏可横向滚动
- [ ] 图表自适应容器宽度

**可访问性**
- [ ] 文字对比度 ≥ 4.5:1
- [ ] 图标按钮有 aria-label
- [ ] 表单有 label 关联

---

## 14. 代码实现示例

### 14.1 全局样式变量

创建 `src/styles/variables.scss`：

```scss
// 配色
@import './colors';

// 字体
@import './typography';

// 间距
@import './spacing';

// 圆角
@import './radius';

// 阴影
@import './shadow';

// 动画
@import './animation';
```

### 14.2 ElementUI 主题覆盖

创建 `src/styles/element-override.scss`：

```scss
// 覆盖 ElementUI 默认变量
$--color-primary: #409EFF;
$--color-success: #67C23A;
$--color-warning: #E6A23C;
$--color-danger: #F56C6C;
$--color-info: #909399;

$--font-size-base: 14px;
$--border-radius-base: 4px;

// 引入 ElementUI 主题
@import "~element-ui/packages/theme-chalk/src/index";
```

---

*本设计系统基于 UI/UX Pro Max 规范，适用于 Vue 2 + ElementUI 技术栈*
*最后更新：2026-06-12*
