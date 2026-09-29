<template>
  <div class="growth" :title="`${stageLabel} · ${percent}%`">
    <!-- 拟物图标：种子→发芽→长叶→小树，随完成率变档 -->
    <svg class="g-ico" viewBox="0 0 24 24" fill="none" aria-hidden="true" :style="{ color: stageColor }">
      <!-- 种子（0-25%）：斜置种粒 -->
      <g v-if="stage === 0">
        <ellipse cx="12" cy="14.5" rx="3.6" ry="5" transform="rotate(-18 12 14.5)" stroke="currentColor" stroke-width="1.8" />
        <path d="M12 9.5c.8-.9 1.9-1.4 3-1.5-.2 1.2-.8 2.2-1.7 2.9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
      </g>
      <!-- 发芽（26-50%）：茎 + 双子叶 -->
      <g v-else-if="stage === 1">
        <path d="M12 20.5v-7.3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
        <path d="M12 13.4c-3.1 0-5-1.7-5-4.3 2.7 0 5 1.7 5 4.3Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
        <path d="M12 13.4c3.1-.3 5-2 5-4.5-2.7-.3-5 1.8-5 4.5Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
      </g>
      <!-- 长叶（51-75%）：茎 + 两层对生叶 -->
      <g v-else-if="stage === 2">
        <path d="M12 20.5V8.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
        <path d="M12 15.6c-2.5 0-4-1.3-4-3.4 2.2 0 4 1.3 4 3.4Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
        <path d="M12 15.6c2.5-.2 4-1.6 4-3.6-2.2-.2-4 1.4-4 3.6Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
        <path d="M12 10.4c-2 0-3.2-1-3.2-2.7 1.8 0 3.2 1 3.2 2.7Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
        <path d="M12 10.4c2-.2 3.2-1.3 3.2-2.9-1.8-.2-3.2 1.2-3.2 2.9Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
      </g>
      <!-- 小树（76-100%）：树冠 + 树干 + 地面 -->
      <g v-else>
        <circle cx="12" cy="9" r="5.2" stroke="currentColor" stroke-width="1.8" />
        <path d="M12 20.5v-6.3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
        <path d="M12 16.5 9.8 14.3M12 14.8l1.8-1.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
        <path d="M5.5 20.5h13" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
      </g>
    </svg>
    <div class="track">
      <div class="fill" :style="{ width: percent + '%', background: stageColor }"></div>
    </div>
    <span class="num">{{ percent }}%</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

/**
 * 拟物完成率进度条：完成率映射生长阶段
 * 0-25% 种子 / 26-50% 发芽 / 51-75% 长叶 / 76-100% 小树
 */
const props = defineProps<{ percent: number }>();

const stage = computed(() => {
  const p = props.percent;
  if (p <= 25) return 0;
  if (p <= 50) return 1;
  if (p <= 75) return 2;
  return 3;
});

const STAGE_META = [
  { label: '种子', color: 'var(--theme-general)' },
  { label: '发芽', color: 'var(--theme-growth)' },
  { label: '长叶', color: 'var(--brand-primary)' },
  { label: '小树', color: 'var(--brand-primary-dark)' },
] as const;

const stageLabel = computed(() => STAGE_META[stage.value].label);
const stageColor = computed(() => STAGE_META[stage.value].color);
</script>

<style scoped>
.growth {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 150px;
}
.g-ico {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}
.track {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: var(--divider-soft); /* 墨绿 8% 底轨，与看板线条色一致 */
  overflow: hidden;
}
.fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}
.num {
  flex-shrink: 0;
  width: 42px;
  font-size: 13px;
  color: var(--text-secondary);
  text-align: right;
  font-variant-numeric: tabular-nums;
}
</style>
