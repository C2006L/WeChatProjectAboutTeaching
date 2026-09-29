<template>
  <div class="page">
    <p class="page-sub">面向支教教师的标准化教学素材 · 点击下载，下载次数实时累计</p>

    <!-- 类型筛选条 -->
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

    <!-- 素材网格 -->
    <div class="res-grid">
      <div v-for="r in filtered" :key="r.id" class="res-card card-float card-leaf">
        <div class="res-head">
          <span class="kind-ico" :style="{ color: kindMeta[r.kind].color, background: kindMeta[r.kind].bg }">
            <!-- 教案模板：文档 -->
            <svg v-if="r.kind === '教案模板'" viewBox="0 0 24 24" fill="none" class="ico-svg">
              <path d="M6 3.5h8.5L19 8v12.5H6V3.5Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
              <path d="M14 3.8V8.2h4.6" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
              <path d="M9 12.5h6M9 16h4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
            </svg>
            <!-- PPT模板：演示屏 -->
            <svg v-else-if="r.kind === 'PPT模板'" viewBox="0 0 24 24" fill="none" class="ico-svg">
              <rect x="3.5" y="4.5" width="17" height="11" rx="1.5" stroke="currentColor" stroke-width="1.8" />
              <path d="M12 15.5v3M8.5 20.5h7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
              <path d="M7 9.5h6M7 12h4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
            <!-- 评估量表：清单勾选 -->
            <svg v-else-if="r.kind === '评估量表'" viewBox="0 0 24 24" fill="none" class="ico-svg">
              <rect x="4.5" y="3.5" width="15" height="17" rx="1.5" stroke="currentColor" stroke-width="1.8" />
              <path d="m8 8.5 1.5 1.5L12.5 7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M15 9h1.5M8 14h8.5M8 17h5.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </svg>
            <!-- 绘本清单：翻开的书 -->
            <svg v-else viewBox="0 0 24 24" fill="none" class="ico-svg">
              <path d="M12 6c-1.6-1.4-4-2-6.5-2v14c2.5 0 4.9.6 6.5 2 1.6-1.4 4-2 6.5-2V4c-2.5 0-4.9.6-6.5 2Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
              <path d="M12 6v14" stroke="currentColor" stroke-width="1.8" />
            </svg>
          </span>
          <span class="kind-tag">{{ r.kind }}</span>
        </div>
        <h3 class="res-title">{{ r.title }}</h3>
        <div class="res-meta">
          <span>大小 {{ r.size }}</span>
          <span>更新 {{ r.updatedAt }}</span>
        </div>
        <div class="res-foot">
          <span class="dl-count">
            <svg viewBox="0 0 24 24" fill="none" class="dl-ico">
              <path d="M12 4v11m0 0 4-4m-4 4-4-4" style="stroke: var(--line-brand)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M5 19h14" style="stroke: var(--line-brand)" stroke-width="1.8" stroke-linecap="round" />
            </svg>
            {{ r.downloads }} 次下载
          </span>
          <el-button size="small" type="primary" plain @click="onDownload(r as ResourceItem)">下载</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { fetchResources, downloadResource } from '@/api/resource';
import type { ResourceItem, ResourceKind } from '@/types/dashboard';

const filters = [
  { key: '全部', label: '全部' },
  { key: '教案模板', label: '教案模板' },
  { key: 'PPT模板', label: 'PPT 模板' },
  { key: '评估量表', label: '评估量表' },
  { key: '绘本清单', label: '绘本清单' },
] as const;

type FilterKey = (typeof filters)[number]['key'];
const current = ref<FilterKey>('全部');
const rows = ref<ResourceItem[]>([]);

const filtered = computed(() =>
  current.value === '全部' ? rows.value : rows.value.filter((r) => r.kind === current.value),
);

function countOf(key: FilterKey): number {
  return key === '全部' ? rows.value.length : rows.value.filter((r) => r.kind === key).length;
}

/** 类型图标底色：品牌色系轮换（禁彩虹色） */
const kindMeta: Record<ResourceKind, { color: string; bg: string }> = {
  教案模板: { color: 'var(--brand-primary)', bg: 'var(--brand-primary-light)' },
  'PPT模板': { color: 'var(--brand-secondary)', bg: 'var(--brand-secondary-light)' },
  评估量表: { color: 'var(--succ)', bg: 'var(--succ-soft)' },
  绘本清单: { color: 'var(--info)', bg: 'var(--info-soft)' },
};

async function onDownload(r: ResourceItem) {
  await downloadResource(r.id);
  ElMessage.success(`已开始下载「${r.title}」`);
}

onMounted(async () => {
  rows.value = await fetchResources();
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
/* 素材网格 */
.res-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-l);
}
.res-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* hover 上浮统一走全局 .card-float（180ms 缓动 token） */
}
.res-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.kind-ico {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-s);
}
.ico-svg {
  width: 24px;
  height: 24px;
}
.kind-tag {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-oat);
  padding: 1px 10px;
  height: 22px;
  line-height: 22px;
  border-radius: 11px;
}
.res-title {
  margin: 14px 0 0;
  font-size: var(--font-size-s);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.5;
  min-height: 45px;
}
.res-meta {
  display: flex;
  gap: var(--space-m);
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}
.res-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--space-m);
  padding-top: var(--space-m);
  border-top: 1px solid var(--divider-soft);
}
.dl-count {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-regular);
  font-variant-numeric: tabular-nums;
}
.dl-ico {
  width: 14px;
  height: 14px;
}
</style>
