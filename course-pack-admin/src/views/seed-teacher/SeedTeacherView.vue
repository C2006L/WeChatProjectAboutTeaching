<template>
  <div class="page">
    <p class="page-sub">已完成全部必修并通过案例共创认证的种子教师 · 案例数据与「案例管理」实时联动</p>

    <div class="manage-bar card-leaf-sm">
      <el-input v-model="keyword" class="name-search" clearable placeholder="搜索教师姓名" />
      <el-select v-model="selectedTeam" class="team-filter" placeholder="所属队伍" clearable>
        <el-option v-for="team in teamOptions" :key="team" :label="team" :value="team" />
      </el-select>
      <el-select v-model="sortOrder" class="date-sort" aria-label="认证时间排序">
        <el-option label="认证时间：由新到旧" value="newest" />
        <el-option label="认证时间：由旧到新" value="oldest" />
      </el-select>
      <span class="result-count">共 {{ filteredHonors.length }} 位</span>
    </div>

    <!-- 荣誉墙卡片 -->
    <div v-if="filteredHonors.length" class="honor-grid">
      <div v-for="h in filteredHonors" :key="h.id" class="honor-card card-float card-leaf">
        <!-- 认证绶带 -->
        <div class="ribbon">
          <svg viewBox="0 0 24 24" fill="none" class="medal-ico">
            <circle cx="12" cy="9" r="5.5" style="stroke: var(--brand-secondary)" stroke-width="1.8" />
            <circle cx="12" cy="9" r="2.6" style="stroke: var(--brand-secondary)" stroke-width="1.4" />
            <path d="m9.5 13.5-2 6 4.5-2.4 4.5 2.4-2-6" style="stroke: var(--brand-secondary)" stroke-width="1.8" stroke-linejoin="round" />
          </svg>
          <span class="ribbon-text">种子教师</span>
        </div>

        <!-- 头像 + 姓名 -->
        <div class="who">
          <span class="avatar">{{ h.name.charAt(0) }}</span>
          <div class="who-text">
            <span class="h-name">{{ h.name }}</span>
            <span class="h-team">{{ h.team }}</span>
          </div>
        </div>

        <!-- 认证时间 -->
        <div class="cert-row card-leaf-sm">
          <span class="cert-label">认证时间</span>
          <span class="cert-time">{{ h.certifiedAt }}</span>
        </div>

        <!-- 案例联动数据 -->
        <div class="stat-row">
          <div class="stat">
            <span class="stat-num">{{ h.caseCount }}</span>
            <span class="stat-label">上传案例</span>
          </div>
          <div class="stat">
            <span class="stat-num">{{ h.approvedCount }}</span>
            <span class="stat-label">已通过</span>
          </div>
          <div class="stat">
            <span class="stat-num featured">{{ h.featuredCount }}</span>
            <span class="stat-label">精选</span>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="empty-state card-leaf">没有符合当前条件的种子教师</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { fetchSeedHonors, type SeedHonorWithCount } from '@/api/seedTeacher';

const honors = ref<SeedHonorWithCount[]>([]);
const keyword = ref('');
const selectedTeam = ref('');
const sortOrder = ref<'newest' | 'oldest'>('newest');

const teamOptions = computed(() => [...new Set(honors.value.map((item) => item.team))]);
const filteredHonors = computed(() => {
  const query = keyword.value.trim().toLocaleLowerCase();
  return honors.value
    .filter((item) => !query || item.name.toLocaleLowerCase().includes(query))
    .filter((item) => !selectedTeam.value || item.team === selectedTeam.value)
    .slice()
    .sort((a, b) => {
      const delta = b.certifiedAt.localeCompare(a.certifiedAt);
      return sortOrder.value === 'newest' ? delta : -delta;
    });
});

onMounted(async () => {
  honors.value = await fetchSeedHonors();
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
.manage-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 14px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
}
.name-search { width: 240px; }
.team-filter { width: 210px; }
.date-sort { width: 210px; }
.result-count {
  margin-left: auto;
  font-size: 13px;
  color: var(--text-secondary);
}
/* 荣誉墙网格 */
.honor-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-l);
}
.honor-card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* hover 上浮统一走全局 .card-float（180ms 缓动 token） */
}
/* 顶部绶带 */
.ribbon {
  display: flex;
  align-items: center;
  gap: 6px;
  align-self: flex-start;
  padding: 2px 12px 2px 8px;
  border-radius: 14px;
  background: var(--brand-secondary-light);
}
.medal-ico {
  width: 18px;
  height: 18px;
}
.ribbon-text {
  font-size: 12px;
  font-weight: 600;
  color: var(--warn-text);
}
/* 头像 + 姓名 */
.who {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: var(--space-l);
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 600;
  color: var(--on-brand);
  background: var(--brand-primary);
  flex-shrink: 0;
}
.who-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.h-name {
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.h-team {
  margin-top: 2px;
  font-size: 13px;
  color: var(--text-secondary);
}
/* 认证时间 */
.cert-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-m);
  padding: 8px 12px;
  background: var(--bg-page);
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf-sm 叶片裁角提供（V3） */
}
.cert-label {
  font-size: 13px;
  color: var(--text-secondary);
}
.cert-time {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-regular);
  font-variant-numeric: tabular-nums;
}
/* 案例统计 */
.stat-row {
  display: flex;
  margin-top: var(--space-m);
  padding-top: var(--space-m);
  border-top: 1px solid var(--divider-soft);
}
.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.stat-num {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 650;
  color: var(--brand-deep);
  font-variant-numeric: tabular-nums;
}
.stat-num.featured {
  color: var(--brand-secondary);
}
.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}
.empty-state {
  padding: 64px 24px;
  border: 1px dashed var(--border-brand);
  background: var(--bg-card);
  color: var(--text-secondary);
  text-align: center;
}
@media (max-width: 1200px) {
  .honor-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 760px) {
  .honor-grid { grid-template-columns: 1fr; }
  .name-search,
  .team-filter,
  .date-sort { width: 100%; }
  .result-count { margin-left: 0; }
}
</style>
