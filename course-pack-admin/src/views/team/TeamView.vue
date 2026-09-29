<template>
  <div class="page">
    <p class="page-sub">全部在册支教队伍 · 必修完成率按成员学习进度均值计算 · 点开行查看教师名单</p>

    <div class="table-card card-leaf">
      <el-table :data="rows" style="width: 100%" row-key="id">
        <!-- 展开列：教师名单 + 个人必修进度 -->
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="member-panel">
              <h4 class="member-title">教师名单（{{ (row as TeamDetailRow).members.length }} 人）</h4>
              <div
                v-for="m in (row as TeamDetailRow).members"
                :key="m.name"
                class="member-row"
              >
                <span class="m-role" :class="{ leader: m.role === '队长' }">{{ m.role }}</span>
                <span class="m-name">{{ m.name }}</span>
                <span class="m-desc">必修 {{ m.requiredDone }}/{{ m.requiredTotal }}</span>
                <GrowthProgress :percent="Math.round((m.requiredDone / m.requiredTotal) * 100)" />
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="队伍" min-width="220">
          <template #default="{ row }">
            <div class="team-cell">
              <span class="t-name">{{ row.name }}</span>
              <span class="t-org">{{ row.org }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="服务驻地" min-width="150" />
        <el-table-column label="教师数" width="90" align="center">
          <template #default="{ row }">
            <span class="num">{{ (row as TeamDetailRow).members.length }}</span>
          </template>
        </el-table-column>
        <el-table-column label="资源使用次数" width="130" align="center">
          <template #default="{ row }">
            <span class="num usage">{{ (row as TeamDetailRow).usage }}</span>
          </template>
        </el-table-column>
        <el-table-column label="必修完成率" min-width="220">
          <template #default="{ row }">
            <GrowthProgress :percent="completionOf(row as TeamDetailRow)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { fetchTeams, teamCompletion } from '@/api/team';
import type { TeamDetailRow } from '@/types/dashboard';
import GrowthProgress from '@/components/training/GrowthProgress.vue';

const rows = ref<TeamDetailRow[]>([]);
const completionOf = teamCompletion;

onMounted(async () => {
  rows.value = await fetchTeams();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-l);
  padding-bottom: var(--space-l);
}
.page-sub {
  margin: 0;
  font-size: var(--font-size-s);
  color: var(--text-secondary);
}
.table-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: 8px;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* 表格分隔线统一墨绿低透明度（禁中性灰） */
  --el-table-border-color: var(--divider-soft);
}
.team-cell {
  display: flex;
  flex-direction: column;
}
.t-name {
  font-weight: 600;
  color: var(--text-primary);
}
.t-org {
  font-size: 13px;
  color: var(--text-secondary);
}
.num {
  font-variant-numeric: tabular-nums;
  color: var(--text-regular);
}
.usage {
  font-family: var(--font-serif);
  font-weight: 650;
  color: var(--brand-deep);
}
/* 展开区：教师名单 */
.member-panel {
  padding: 8px 16px 16px 56px;
}
.member-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.member-row {
  display: flex;
  align-items: center;
  gap: var(--space-m);
  padding: 10px 0;
  border-bottom: 1px dashed var(--divider-soft);
}
.member-row:last-child {
  border-bottom: none;
}
.m-role {
  flex-shrink: 0;
  width: 44px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  border-radius: 11px;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-oat);
}
.m-role.leader {
  color: var(--warn-text);
  background: var(--brand-secondary-light);
}
.m-name {
  flex-shrink: 0;
  width: 72px;
  font-weight: 600;
  color: var(--text-primary);
}
.m-desc {
  flex-shrink: 0;
  width: 96px;
  font-size: 13px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}
</style>
