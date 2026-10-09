<template>
  <div v-if="data" class="dashboard">
    <div class="priority-row">
      <WelcomeBanner :data="data.welcome" />
      <TodoList :items="data.todos" />
    </div>

    <div class="section-heading">
      <div>
        <span class="section-kicker">项目概况</span>
        <h2>关键数据</h2>
      </div>
      <span class="data-note">数据更新至今日 09:30</span>
    </div>
    <div class="stat-strip">
      <StatCard
        v-for="s in data.stats"
        :key="s.label"
        :label="s.label"
        :value="s.value"
        :unit="s.unit"
        :trend="s.trend"
      />
    </div>

    <div class="section-heading compact">
      <div>
        <span class="section-kicker">运营观察</span>
        <h2>使用趋势与内容结构</h2>
      </div>
    </div>
    <div class="chart-row">
      <div class="chart-col line-col">
        <LineChartCard title="近 6 个月增长趋势" :data="data.growthTrend" />
      </div>
      <div class="chart-col pie-col">
        <PieChartCard title="课程主题分布" :data="data.courseThemeDistribution" />
      </div>
    </div>

    <div class="section-heading compact">
      <div>
        <span class="section-kicker">一线动态</span>
        <h2>队伍表现与近期事件</h2>
      </div>
    </div>
    <div class="bottom-row">
      <TeamTable :teams="data.activeTeams" />
      <ActivityFeed :items="activityStore.items" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import WelcomeBanner from '@/components/dashboard/WelcomeBanner.vue';
import StatCard from '@/components/dashboard/StatCard.vue';
import TodoList from '@/components/dashboard/TodoList.vue';
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
  --bg-card: #FFFFFF;
  /* V3：--card-border 已在 :root 统一定义为墨绿 8%，此处不再重复覆盖 */
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.priority-row {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr);
  gap: var(--space-l);
}
.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-top: 8px;
}
.section-heading.compact {
  margin-top: 16px;
  margin-bottom: -4px;
}
.section-kicker {
  display: block;
  margin-bottom: 3px;
  font-size: 11px;
  font-weight: 600;
  color: var(--brand-primary);
  letter-spacing: 0.12em;
}
.section-heading h2 {
  margin: 0;
  font-size: 17px;
  color: var(--text-primary);
}
.data-note {
  font-size: 12px;
  color: var(--text-secondary);
}
.stat-strip {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  overflow: hidden;
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  background: var(--bg-card);
}
.stat-strip :deep(.stat-card) {
  border: 0;
  border-right: 1px solid var(--divider-soft);
  border-radius: 0;
}
.stat-strip :deep(.stat-card:last-child) {
  border-right: 0;
}
.chart-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-l);
}
.bottom-row {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(300px, 1fr);
  gap: var(--space-l);
}
@media (max-width: 1200px) {
  .priority-row,
  .chart-row,
  .bottom-row {
    grid-template-columns: 1fr;
  }
  .stat-strip {
    grid-template-columns: repeat(2, 1fr);
  }
  .stat-strip :deep(.stat-card:nth-child(2)) { border-right: 0; }
  .stat-strip :deep(.stat-card:nth-child(-n + 2)) { border-bottom: 1px solid var(--divider-soft); }
}
</style>
