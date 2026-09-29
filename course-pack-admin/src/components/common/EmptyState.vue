<template>
  <div class="empty-state" :class="{ compact }">
    <!-- 品牌插画：线性小芽（V3 插画轨，替代 Element 默认灰色空图） -->
    <svg class="empty-sprout" viewBox="0 0 64 64" fill="none" aria-hidden="true">
      <!-- 土壤弧线 -->
      <path d="M18 52h28" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <!-- 茎 -->
      <path d="M32 52V26" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <!-- 双子叶（一实一空，暗示"等待生长"） -->
      <path
        d="M32 28c-9 0-15-5-15-13 8 0 15 5 15 13Z"
        stroke="currentColor"
        stroke-width="2.5"
        stroke-linejoin="round"
        :fill="compact ? 'none' : 'var(--fill-brand-soft)'"
      />
      <path d="M32 28c9-1 15-6 15-14-8-1-15 4-15 14Z" stroke="currentColor" stroke-width="2.5" stroke-linejoin="round" />
    </svg>
    <p class="empty-text">{{ description }}</p>
    <p v-if="hint" class="empty-hint">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
/**
 * 品牌插画空态（V3）：统一替代 el-empty 的默认灰图
 * - 默认尺寸用于页面级空态（右详情面板 / 空列表）
 * - compact 用于卡片内嵌的小空态（审核意见列表等）
 */
defineProps<{
  description: string;
  hint?: string;
  compact?: boolean;
}>();
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-s);
  padding: var(--space-2xl) var(--space-l);
  color: var(--line-brand-muted);
}
.empty-sprout {
  width: 64px;
  height: 64px;
}
.empty-text {
  margin: 0;
  font-size: var(--font-size-s);
  color: var(--text-secondary);
}
.empty-hint {
  margin: 0;
  font-size: var(--font-size-xs);
  color: var(--text-weak);
}
/* 紧凑版：卡片内嵌 */
.empty-state.compact {
  padding: var(--space-m);
  gap: var(--space-xs);
}
.empty-state.compact .empty-sprout {
  width: 40px;
  height: 40px;
}
.empty-state.compact .empty-text {
  font-size: var(--font-size-xs);
}
</style>
