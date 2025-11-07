<template>
  <div class="dashboard-container">
    <h1 class="page-title">仪表盘</h1>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card" style="border-left-color: #409EFF;">
          <div class="stat-icon" style="background-color: rgba(64, 158, 255, 0.1);">
            <el-icon :size="32" color="#409EFF"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">1,234</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card" style="border-left-color: #67C23A;">
          <div class="stat-icon" style="background-color: rgba(103, 194, 58, 0.1);">
            <el-icon :size="32" color="#67C23A"><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">567</div>
            <div class="stat-label">工单总数</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card" style="border-left-color: #E6A23C;">
          <div class="stat-icon" style="background-color: rgba(230, 162, 60, 0.1);">
            <el-icon :size="32" color="#E6A23C"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">89</div>
            <div class="stat-label">待处理工单</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stat-card" style="border-left-color: #F56C6C;">
          <div class="stat-icon" style="background-color: rgba(245, 108, 108, 0.1);">
            <el-icon :size="32" color="#F56C6C"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">23</div>
            <div class="stat-label">在线客服</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">工单趋势</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="64" color="#DCDFE6"><TrendCharts /></el-icon>
            <p class="placeholder-text">图表功能开发中...</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">最新工单</span>
            </div>
          </template>
          <div class="recent-tickets">
            <div v-for="i in 5" :key="i" class="ticket-item">
              <div class="ticket-title">工单标题 {{ i }}</div>
              <div class="ticket-meta">
                <el-tag size="small" type="warning">待处理</el-tag>
                <span class="ticket-time">2小时前</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { gsap } from 'gsap'

onMounted(() => {
  // 统计卡片动画
  gsap.from('.stat-card', {
    duration: 0.6,
    y: 20,
    opacity: 0,
    stagger: 0.1,
    ease: 'power2.out'
  })

  // 内容卡片动画
  gsap.from('.content-card', {
    duration: 0.6,
    y: 30,
    opacity: 0,
    stagger: 0.2,
    delay: 0.4,
    ease: 'power2.out'
  })
})
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border-left: 4px solid;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 12px;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.content-row {
  margin-top: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.content-card :deep(.el-card__header) {
  padding: 16px 20px;
  background-color: #fafafa;
}

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

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #909399;
}

.placeholder-text {
  margin-top: 16px;
  font-size: 14px;
}

.recent-tickets {
  max-height: 400px;
  overflow-y: auto;
}

.ticket-item {
  padding: 16px 0;
  border-bottom: 1px solid #EBEEF5;
}

.ticket-item:last-child {
  border-bottom: none;
}

.ticket-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 500;
}

.ticket-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ticket-time {
  font-size: 12px;
  color: #909399;
}
</style>
