<template>
  <div class="activity-feed card-leaf">
    <h4 class="title">最新动态</h4>
    <div v-for="(item, i) in items" :key="i" class="activity-item">
      <span class="type-dot" :class="item.type"></span>
      <div class="activity-body">
        <p class="activity-title">{{ item.title }}</p>
        <span class="activity-time">{{ item.time }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ActivityItem } from '@/types/dashboard';

interface Props {
  items: ActivityItem[];
}

defineProps<Props>();
</script>

<style scoped>
.activity-feed {
  padding: var(--space-m);
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.title {
  margin: 0 0 var(--space-s);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.activity-item {
  display: flex;
  gap: var(--space-s);
  padding: var(--space-s) var(--space-xs);
  border-bottom: 1px solid var(--divider-soft);  /* 分隔线统一墨绿低透明（V3 token） */
}
.activity-item:last-child {
  border-bottom: none;
}
.type-icon {
  font-size: 18px;
  line-height: 1.4;
}
/* V2：Kawaii 边界——动态时间线用品牌色系圆点，禁 emoji 图标 */
.type-dot {
  flex-shrink: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 6px;
}
.type-dot.course { background: var(--brand-primary); }
.type-dot.team { background: var(--brand-secondary); }
.type-dot.user { background: var(--info); }
.type-dot.favorite { background: var(--succ); }
.activity-body {
  min-width: 0;
}
.activity-title {
  margin: 0;
  font-size: var(--font-size-s);
  color: var(--text-regular);
  line-height: 1.5;
}
.activity-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}
</style>
