<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  height: 100%;
}

/* ========== 列表页面通用布局 ==========
   使用方式：根容器添加 page-list-flex 类
   结构要求：
     <div class="xxx page-list-flex">
       <div class="page-header">...</div>        ← 标题（flex-shrink: 0）
       <div class="filter-bar">...</div>          ← 搜索区（flex-shrink: 0）
       <div class="table-flex-wrapper">           ← 表格容器（flex: 1, 滚动）
         <el-table height="100%">...</el-table>
       </div>
       <div class="pagination-bar">...</div>      ← 分页（flex-shrink: 0）
     </div>
*/
/* 列表页面：绝对定位填充 app-main，内部 flex 列布局 */
.page-list-flex {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部固定区：标题、搜索栏等 */
.page-list-flex > .page-header,
.page-list-flex > .stats-cards,
.page-list-flex > .filter-bar,
.page-list-flex > .filter-container,
.page-list-flex > .search-card,
.page-list-flex > .el-card,
.page-list-flex > .top-bar {
  flex-shrink: 0;
}

/* 底部固定区：分页 */
.page-list-flex > .pagination-bar,
.page-list-flex > .pagination-container,
.page-list-flex > .el-pagination {
  flex-shrink: 0;
}

/* 表格滚动容器 */
.table-flex-wrapper {
  flex: 1;
  overflow: auto;
  min-height: 0;
}
</style>
