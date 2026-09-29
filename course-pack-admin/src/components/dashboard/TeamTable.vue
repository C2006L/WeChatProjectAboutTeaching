<template>
  <div class="team-table card-leaf">
    <h4 class="title">活跃支教队</h4>
    <el-table :data="teams" size="default" :show-header="true">
      <el-table-column prop="name" label="队伍名称" min-width="140" />
      <el-table-column prop="org" label="所属高校" min-width="130" />
      <el-table-column prop="area" label="服务地区" min-width="120" />
      <el-table-column prop="teachers" label="教师数" width="80" align="center" />
      <el-table-column label="资源使用" width="110" align="center">
        <template #default="{ row }">
          <span class="usage">{{ row.usage }} 次</span>
        </template>
      </el-table-column>
      <el-table-column label="完成率" min-width="140">
        <template #default="{ row }">
          <el-progress
            :percentage="row.completion"
            :stroke-width="8"
            :color="'var(--brand-primary)'"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import type { TeamRow } from '@/types/dashboard';

interface Props {
  teams: TeamRow[];
}

defineProps<Props>();
</script>

<style scoped>
.team-table {
  padding: 24px;                        /* 卡片内边距统一 24px */
  background: var(--bg-card);           /* #FBF9F4 */
  border: 1px solid var(--card-border);
  box-shadow: var(--shadow-card);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* 表格行分隔线/表头下划线统一墨绿低透明度（覆盖 Element Plus 中性灰） */
  --el-table-border-color: var(--divider-soft);
}
.title {
  margin: 0 0 var(--space-m);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.usage {
  color: var(--brand-primary);
  font-weight: 600;
}
</style>
