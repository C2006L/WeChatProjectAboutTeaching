<template>
  <div v-if="data" class="dashboard">
    <!-- 欢迎横幅（燕麦底大卡） -->
    <WelcomeBanner :data="data.welcome" />

    <!-- 4 个统计卡：等宽 4 列，gap 24px；card-float = hover 上浮 180ms -->
    <div class="stat-row">
      <StatCard
        v-for="s in data.stats"
        :key="s.label"
        class="card-float"
        :label="s.label"
        :value="s.value"
        :unit="s.unit"
        :trend="s.trend"
      />
    </div>

    <!-- 折线 2/3 + 饼 1/3 -->
    <div class="chart-row">
      <div class="chart-col line-col">
        <LineChartCard class="card-float" title="近 6 个月增长趋势" :data="data.growthTrend" />
      </div>
      <div class="chart-col pie-col">
        <PieChartCard class="card-float" title="课程主题分布" :data="data.courseThemeDistribution" />
      </div>
    </div>

    <!-- 支教队 Top5 + 最新动态（activityStore 单例：教师管理等操作实时写入） -->
    <div class="bottom-row">
      <TeamTable class="card-float" :teams="data.activeTeams" />
      <ActivityFeed class="card-float" :items="activityStore.items" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import WelcomeBanner from '@/components/dashboard/WelcomeBanner.vue';
import StatCard from '@/components/dashboard/StatCard.vue';
import LineChartCard from '@/components/dashboard/LineChartCard.vue';
import PieChartCard from '@/components/dashboard/PieChartCard.vue';
import TeamTable from '@/components/dashboard/TeamTable.vue';
import ActivityFeed from '@/components/dashboard/ActivityFeed.vue';
import { fetchDashboard } from '@/api/dashboard';
import type { DashboardData } from '@/types/dashboard';
import { activityStore } from '@/store/activity';

const data = ref<DashboardData | null>(null);

onMounted(async () => {
  data.value = await fetchDashboard();
});
</script>

<style scoped>
.dashboard {
  /* 看板材质令牌覆盖：CSS 变量沿子树继承，仅作用于数据看板，不影响其他页面
   * 卡片底 #FBF9F4 暖米白（禁纯白+灰投影）· 墨绿低透明投影
   * V3：圆角统一交由全局 .card-leaf 叶片裁角体系，此处不再覆盖 12px */
  --bg-card: #FBF9F4;
  --shadow-card: 0 2px 8px rgba(54, 82, 77, 0.06);
  /* V3：--card-border 已在 :root 统一定义为墨绿 8%，此处不再重复覆盖 */
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);                 /* 区块间距统一 32px */
}
/* V2：4 列等宽 grid，gap 24px */
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-l);
}
.chart-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-l);
}
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-l);
}
@media (max-width: 1200px) {
  .chart-row,
  .bottom-row {
    grid-template-columns: 1fr;
  }
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>