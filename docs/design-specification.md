# 杭州市小微园区评价平台 - 前端设计规范

> 版本：v1.0  
> 更新日期：2026-06-12  
> 设计风格：高级感 · 浅蓝调色 · 简洁大方

---

## 一、设计原则

| 原则 | 说明 |
|------|------|
| **高级感** | 不用毛玻璃、不用渐变，用纯色和微妙色差 |
| **浅蓝调色** | 浅蓝作为辅助色，增加层次感 |
| **克制配色** | 主色仅用于关键操作，不滥用 |
| **精致细节** | 1px 边框、4px 阴影、8-12px 圆角 |
| **大量留白** | 呼吸感强，不拥挤 |
| **微动效** | hover 时 translateY(-2px)，过渡顺滑 |

---

## 二、配色方案

### 主色（克制使用）

| 用途 | 色值 | 说明 |
|------|------|------|
| 主色 | `#1E40AF` | 深蓝，用于按钮、链接、重点数据 |
| 主色 Hover | `#1E3A8A` | 深蓝，hover 状态 |
| 主色 Light | `#EFF6FF` | 浅蓝，用于选中状态背景 |
| 主色 50 | `#F0F4FF` | 极浅蓝，用于背景 |

### 浅蓝调色（高级感）

| 用途 | 色值 | 说明 |
|------|------|------|
| 浅蓝背景 | `#F8FAFF` | 页面背景，高级感 |
| 浅蓝边框 | `#E8EDF5` | 卡片边框，微妙 |
| 浅蓝内容背景 | `#FAFCFF` | 主内容区背景 |

### 语义色

| 用途 | 色值 | 说明 |
|------|------|------|
| 成功 | `#059669` | 通过、增长 |
| 成功背景 | `#ECFDF5` | 成功状态背景 |
| 警告 | `#D97706` | 待审核、中等风险 |
| 警告背景 | `#FFFBEB` | 警告状态背景 |
| 错误 | `#DC2626` | 驳回、下降 |
| 错误背景 | `#FEF2F2` | 错误状态背景 |

### 中性色

| 用途 | 色值 | 说明 |
|------|------|------|
| 标题文字 | `#111827` | 主标题 |
| 正文文字 | `#1F2937` | 正文内容 |
| 次要文字 | `#6B7280` | 辅助说明 |
| 占位文字 | `#9CA3AF` | 输入框 placeholder |
| 边框 | `#E5E7EB` | 通用边框 |
| 分割线 | `#F3F4F6` | 表格分割线 |
| 背景 | `#F9FAFB` | 页面背景 |

---

## 三、字体规范

### 字体栈

```css
font-family: 'Inter', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
```

### 字号层级

| 用途 | 字号 | 字重 | 说明 |
|------|------|------|------|
| 大标题 | 32-36px | 700 | 登录页标题 |
| 页面标题 | 24px | 700 | 页面主标题 |
| 卡片标题 | 16-18px | 600 | 卡片标题 |
| 正文 | 14px | 400 | 正文内容 |
| 辅助文字 | 13px | 400 | 次要说明 |
| 小字 | 12px | 400 | 标签、提示 |
| 数据数字 | 28-36px | 700 | KPI 数字 |

### 数字字体

```css
font-variant-numeric: tabular-nums; /* 等宽数字，数据对齐 */
```

---

## 四、间距系统

### 基础间距（4px 倍数）

| 变量 | 值 | 用途 |
|------|------|------|
| `--space-1` | 4px | 最小间距 |
| `--space-2` | 8px | 紧凑间距 |
| `--space-3` | 12px | 表单元素间距 |
| `--space-4` | 16px | 常规间距 |
| `--space-5` | 20px | 卡片内间距 |
| `--space-6` | 24px | 区块间距 |
| `--space-8` | 32px | 大区块间距 |

### 应用场景

| 场景 | 间距 | 说明 |
|------|------|------|
| 表单元素之间 | 16-20px | `margin-bottom: 16px` |
| 卡片内边距 | 20-24px | `padding: 20px` |
| 卡片之间 | 16px | `gap: 16px` |
| 页面边距 | 24-48px | `padding: 24px` |

---

## 五、圆角规范

| 用途 | 圆角 | 说明 |
|------|------|------|
| 按钮 | 8px | `border-radius: 8px` |
| 输入框 | 8-10px | `border-radius: 10px` |
| 卡片 | 12px | `border-radius: 12px` |
| 大卡片 | 14-16px | `border-radius: 16px` |
| 标签 | 4-6px | `border-radius: 6px` |
| 胶囊 | 9999px | `border-radius: 9999px` |

---

## 六、阴影规范

| 用途 | 阴影 | 说明 |
|------|------|------|
| 卡片默认 | `0 1px 3px rgba(0,0,0,0.04)` | 轻微阴影 |
| 卡片 hover | `0 4px 12px rgba(0,0,0,0.06)` | hover 加深 |
| 弹出层 | `0 4px 24px rgba(0,0,0,0.08)` | 弹出层阴影 |
| 按钮 hover | `0 4px 12px rgba(30,64,175,0.3)` | 主色阴影 |

---

## 七、组件规范

### 7.1 按钮

```css
/* 主按钮 */
.btn-primary {
  height: 44px;
  padding: 0 20px;
  background: #1E40AF;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-primary:hover {
  background: #1E3A8A;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}
```

### 7.2 输入框

```css
.form-input {
  height: 44px;
  padding: 0 14px;
  border: 1px solid #E5E7EB;
  border-radius: 10px;
  font-size: 14px;
  color: #1F2937;
  background: white;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.form-input:focus {
  border-color: #1E40AF;
  box-shadow: 0 0 0 3px #EFF6FF;
}

.form-input::placeholder {
  color: #9CA3AF;
}
```

### 7.3 卡片

```css
.card {
  background: white;
  border-radius: 12px;
  border: 1px solid #E8EDF5;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border-color: #1E40AF;
}
```

### 7.4 表格

```css
/* 表格行 hover */
.el-table tbody tr:hover td {
  background: #F8FAFF;
}

/* 表头 */
.el-table th {
  background: #F9FAFB;
  font-size: 12px;
  font-weight: 600;
  color: #4B5563;
}
```

### 7.5 状态标签

```css
/* 成功 */
.status-success {
  background: #ECFDF5;
  color: #059669;
}

/* 警告 */
.status-warning {
  background: #FFFBEB;
  color: #D97706;
}

/* 错误 */
.status-danger {
  background: #FEF2F2;
  color: #DC2626;
}
```

---

## 八、布局规范

### 8.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│  顶部导航栏（白色背景，56px 高度）                            │
├──────────┬──────────────────────────────────────────────────┤
│          │                                                  │
│  侧边栏  │  主内容区（浅蓝背景 #FAFCFF）                      │
│  220px   │                                                  │
│  白色背景 │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐               │
│          │  │ KPI │ │ KPI │ │ KPI │ │ KPI │               │
│          │  └─────┘ └─────┘ └─────┘ └─────┘               │
│          │                                                  │
│          │  ┌──────────────┐ ┌──────────────┐              │
│          │  │   图表区域    │ │   图表区域   │              │
│          │  └──────────────┘ └──────────────┘              │
│          │                                                  │
│          │  ┌─────────────────────────────────────────┐   │
│          │  │              表格区域                    │   │
│          │  └─────────────────────────────────────────┘   │
│          │                                                  │
└──────────┴──────────────────────────────────────────────────┘
```

### 8.2 侧边栏

- 宽度：220px
- 背景：白色
- 边框：右侧 1px solid #E5E7EB
- 菜单项：无分组标题，直接显示
- 选中状态：左侧 3px 蓝色条 + 浅蓝背景

### 8.3 KPI 卡片

- 4 列等宽布局
- 间距：16px
- 边框：1px solid #E8EDF5
- hover：translateY(-2px) + 边框变蓝

### 8.4 图表区域

- 2 列等宽布局
- 间距：16px
- 标题栏：左侧 3px 蓝色条

### 8.5 表格

- 圆角：12px
- hover 行：背景 #F8FAFF
- 分割线：1px solid #F3F4F6

---

## 九、动效规范

### 9.1 过渡曲线

```css
--ease-smooth: cubic-bezier(0.4, 0, 0.2, 1);
```

### 9.2 过渡时间

| 场景 | 时间 | 说明 |
|------|------|------|
| hover | 200ms | 按钮、卡片 |
| 状态切换 | 200ms | 输入框 focus |
| 页面切换 | 300ms | 路由切换 |

### 9.3 hover 效果

```css
/* 通用 hover */
.hoverable {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.hoverable:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}
```

---

## 十、图标规范

### 10.1 图标库

使用 **Heroicons** 线性风格图标，stroke-width: 2

### 10.2 图标尺寸

| 用途 | 尺寸 | 说明 |
|------|------|------|
| 导航图标 | 18-20px | 侧边栏菜单 |
| 卡片图标 | 20px | KPI 卡片 |
| 表格图标 | 16px | 操作按钮 |
| 状态图标 | 14-16px | 状态标签 |

### 10.3 图标颜色

- 默认：`#6B7280`
- 激活：`#1E40AF`
- 禁用：`#D1D5DB`

---

## 十一、响应式规范

### 断点

| 断点 | 宽度 | 说明 |
|------|------|------|
| 手机 | < 768px | 单列布局 |
| 平板 | 768-1024px | 双列布局 |
| 桌面 | > 1024px | 完整布局 |

### 响应式规则

- 侧边栏：< 768px 时隐藏
- KPI 卡片：< 768px 时 2 列，< 480px 时 1 列
- 图表：< 768px 时单列
- 表格：< 768px 时横向滚动

---

## 十二、文件结构

```
src/
├── styles/
│   ├── variables.scss      # 变量定义
│   ├── mixins.scss         # 混入
│   ├── base.scss           # 基础样式
│   ├── element-override.scss  # ElementUI 覆盖
│   └── index.scss          # 统一入口
├── components/
│   ├── StatCard.vue        # KPI 卡片组件
│   ├── ChartCard.vue       # 图表卡片组件
│   └── ...
└── views/
    ├── login/
    ├── dashboard/
    └── ...
```

---

## 十三、快速参考

### 颜色速查

| 用途 | 色值 |
|------|------|
| 主色 | `#1E40AF` |
| 浅蓝背景 | `#F8FAFF` |
| 浅蓝边框 | `#E8EDF5` |
| 成功 | `#059669` |
| 警告 | `#D97706` |
| 错误 | `#DC2626` |
| 标题文字 | `#111827` |
| 正文文字 | `#1F2937` |
| 次要文字 | `#6B7280` |

### 尺寸速查

| 元素 | 尺寸 |
|------|------|
| 按钮高度 | 44px |
| 输入框高度 | 44px |
| 侧边栏宽度 | 220px |
| 顶栏高度 | 56px |
| 卡片圆角 | 12px |
| 按钮圆角 | 8px |

---

## 十四、注意事项

1. **不用毛玻璃**：纯色背景更干净
2. **不用渐变**：微妙色差更高级
3. **阴影要轻**：4px 以内，不压重
4. **边框要细**：1px 足够，不粗
5. **hover 微动**：translateY(-2px)，不过度
6. **图标要精致**：线性风格，不丑
7. **字体要现代**：Inter 首选
8. **数字要等宽**：font-variant-numeric: tabular-nums

---

*本设计规范供团队成员参考，确保前端开发的一致性*
