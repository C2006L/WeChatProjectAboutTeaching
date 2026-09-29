<template>
  <div class="page">
    <p class="page-sub">教师上传的教学案例 · 四环节审核流转 · 精选案例将推荐给全体教师</p>

    <!-- 状态筛选条 -->
    <div class="filter-card card-leaf">
      <div
        v-for="f in filters"
        :key="f.key"
        class="filter-tab"
        :class="{ active: current === f.key }"
        @click="current = f.key"
      >
        {{ f.label }}
        <span class="count">{{ countOf(f.key) }}</span>
      </div>
    </div>

    <!-- 案例卡片三列网格 -->
    <div class="case-grid">
      <div
        v-for="c in filtered"
        :key="c.id"
        class="case-card card-float card-leaf"
        @click="openDetail(c)"
      >
        <div class="card-head">
          <span
            class="status-badge"
            :class="c.status === 'PENDING' ? 'pending' : 'approved'"
          >{{ c.status === 'PENDING' ? '待审核' : '已通过' }}</span>
          <span v-if="c.featured" class="featured-badge">精选</span>
        </div>
        <h3 class="card-title">{{ c.title }}</h3>
        <p class="card-summary">{{ c.summary }}</p>
        <div class="card-foot">
          <span class="foot-meta">{{ c.uploader }} · {{ c.team }}</span>
          <span class="foot-time">{{ c.uploadedAt }}</span>
        </div>
      </div>
    </div>

    <!-- 详情弹窗：四环节流转 + 精选标记 -->
    <el-dialog
      v-model="dialogVisible"
      :title="active?.title"
      width="720px"
      class="case-dialog"
    >
      <template v-if="active">
        <div class="dlg-meta">
          <span>上传人：{{ active.uploader }}</span>
          <span>所属队伍：{{ active.team }}</span>
          <span>上传时间：{{ active.uploadedAt }}</span>
          <span
            class="status-badge"
            :class="active.status === 'PENDING' ? 'pending' : 'approved'"
          >{{ active.status === 'PENDING' ? '待审核' : '已通过' }}</span>
        </div>

        <p class="dlg-summary card-leaf-sm">{{ active.summary }}</p>

        <!-- 精选开关 -->
        <div class="featured-row">
          <span class="featured-label">精选案例</span>
          <el-switch
            :model-value="active.featured"
            active-text="推荐给全体教师"
            @change="onToggleFeatured"
          />
        </div>

        <!-- 四环节审核流转 -->
        <div class="dlg-section">
          <h4 class="sub-title">四环节审核流转</h4>
          <AuditStepper :stages="active.stages" :progress="stageProgress" />
        </div>

        <!-- 流转记录 -->
        <div class="dlg-section">
          <h4 class="sub-title">审核意见记录</h4>
          <div v-for="(cm, i) in active.comments" :key="i" class="comment">
            <StageIcon :stage="cm.stageKey" :done="true" />
            <div class="comment-body">
              <div class="comment-head">
                <span class="stage-name">{{ cm.stageLabel }}</span>
                <span class="reviewer">{{ cm.reviewer }} · {{ cm.time }}</span>
              </div>
              <p class="comment-content">{{ cm.content }}</p>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { fetchCases, toggleFeatured } from '@/api/case';
import type { CaseItem } from '@/types/dashboard';
import AuditStepper from '@/components/audit/AuditStepper.vue';
import StageIcon from '@/components/audit/StageIcon.vue';

const filters = [
  { key: 'ALL', label: '全部' },
  { key: 'PENDING', label: '待审核' },
  { key: 'APPROVED', label: '已通过' },
  { key: 'FEATURED', label: '精选' },
] as const;

type FilterKey = (typeof filters)[number]['key'];
const current = ref<FilterKey>('ALL');
const cases = ref<CaseItem[]>([]);
const active = ref<CaseItem | null>(null);
const dialogVisible = ref(false);

const filtered = computed(() => {
  switch (current.value) {
    case 'PENDING':
      return cases.value.filter((c) => c.status === 'PENDING');
    case 'APPROVED':
      return cases.value.filter((c) => c.status === 'APPROVED');
    case 'FEATURED':
      return cases.value.filter((c) => c.featured);
    default:
      return cases.value;
  }
});

function countOf(key: FilterKey): number {
  switch (key) {
    case 'PENDING':
      return cases.value.filter((c) => c.status === 'PENDING').length;
    case 'APPROVED':
      return cases.value.filter((c) => c.status === 'APPROVED').length;
    case 'FEATURED':
      return cases.value.filter((c) => c.featured).length;
    default:
      return cases.value.length;
  }
}

/** 四环节完成度百分比（与审核中心口径一致） */
const stageProgress = computed(() => {
  if (!active.value) return 0;
  const done = active.value.stages.filter((s) => s.done).length;
  return Math.round((done / 4) * 100);
});

function openDetail(c: CaseItem) {
  active.value = c;
  dialogVisible.value = true;
}

async function onToggleFeatured() {
  if (!active.value) return;
  const featured = await toggleFeatured(active.value.id);
  ElMessage.success(featured ? '已标记为精选案例' : '已取消精选');
}

onMounted(async () => {
  cases.value = await fetchCases();
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
/* 筛选条：与教师管理页同款 */
.filter-card {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  background: var(--bg-oat);
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border-radius: 18px;
  font-size: var(--font-size-s);
  color: var(--text-regular);
  background: var(--bg-card);
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.15s ease;
}
.filter-tab:hover {
  border-color: var(--brand-primary-light);
}
.filter-tab.active {
  background: var(--brand-primary);
  border-color: var(--brand-primary);
  color: var(--on-brand);
  font-weight: 600;
}
.count {
  font-size: 13px;
  opacity: 0.75;
  font-variant-numeric: tabular-nums;
}
/* 三列网格（对齐课程管理 Bento 规范） */
.case-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-l);
}
.case-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* hover 上浮统一走全局 .card-float（180ms 缓动 token） */
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status-badge {
  padding: 1px 10px;
  height: 22px;
  line-height: 20px;
  border-radius: 11px;
  font-size: 12px;
  white-space: nowrap;
}
.status-badge.pending {
  color: var(--warn-text);
  background: var(--brand-secondary-light);
}
.status-badge.approved {
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
.featured-badge {
  padding: 1px 10px;
  height: 22px;
  line-height: 20px;
  border-radius: 11px;
  font-size: 12px;
  font-weight: 600;
  color: var(--on-brand);
  background: var(--brand-secondary);
}
.card-title {
  margin: 12px 0 0;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.5;
}
.card-summary {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.card-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-m);
  padding-top: var(--space-m);
  border-top: 1px solid var(--divider-soft);
}
.foot-meta {
  font-size: 13px;
  color: var(--text-regular);
}
.foot-time {
  font-size: 13px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}
/* 弹窗 */
.dlg-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-l);
  align-items: center;
  font-size: 14px;
  color: var(--text-secondary);
}
.dlg-summary {
  margin: var(--space-m) 0 0;
  font-size: 15px;
  color: var(--text-regular);
  line-height: 1.8;
  padding: var(--space-m);
  background: var(--bg-page);
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf-sm 叶片裁角提供（V3） */
}
.featured-row {
  display: flex;
  align-items: center;
  gap: var(--space-m);
  margin-top: var(--space-m);
  padding: 12px var(--space-m);
  background: var(--brand-primary-light);
  border-radius: var(--radius-s);
}
.featured-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-primary-dark);
}
.dlg-section {
  margin-top: var(--space-l);
}
.sub-title {
  margin: 0 0 var(--space-m);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.comment {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--divider-soft);
}
.comment:last-child {
  border-bottom: none;
}
.comment-body {
  min-width: 0;
}
.comment-head {
  display: flex;
  align-items: center;
  gap: var(--space-m);
}
.stage-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--brand-primary-dark);
}
.reviewer {
  font-size: 13px;
  color: var(--text-secondary);
}
.comment-content {
  margin: 6px 0 0;
  font-size: 15px;
  color: var(--text-regular);
  line-height: 1.7;
}
</style>
