<template>
  <svg class="stage-ico" viewBox="0 0 40 40" fill="none" aria-hidden="true" :style="{ color: strokeC }">
    <!-- 种子：教育审核 -->
    <g v-if="stage === 'education'">
      <ellipse cx="20" cy="25" rx="9" ry="10" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
      <ellipse cx="20" cy="25" rx="3" ry="4.5" stroke="currentColor" fill="none" stroke-width="1.4" />
    </g>
    <!-- 发芽：心理审核 -->
    <g v-else-if="stage === 'psychology'">
      <path d="M20 34V17" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <path d="M20 21c-7 0-11-4-11-10 7 0 11 4 11 10Z" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
    </g>
    <!-- 长叶：社会审核 -->
    <g v-else-if="stage === 'sociology'">
      <path d="M20 36V16" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <path d="M20 20c-8-1-13-6-13-13 8 1 13 6 13 13Z" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
      <path d="M20 20c8 0 13-5 13-13-8 0-13 5-13 13Z" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
    </g>
    <!-- 小树：校长终审 -->
    <g v-else>
      <path d="M20 36V24" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <path d="M20 16c-7 0-13-4-13-11 7 0 13 4 13 11Z" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
      <path d="M20 16c7 0 13-4 13-11-7 0-13 4-13 11Z" stroke="currentColor" :style="{ fill: done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
      <circle cx="20" cy="27" r="2.6" stroke="currentColor" :style="{ fill: done ? 'var(--brand-primary)' : 'none' }" stroke-width="1.6" />
    </g>
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { AuditStageKey } from '@/types/dashboard';

/** 四环节植物小图标（审核意见前缀 / 流转记录，审核中心与案例管理共用） */
const props = defineProps<{
  stage: AuditStageKey;
  done?: boolean;
}>();

/** 完成态走品牌墨绿，未完成走弱化墨绿（SVG 属性不支持 var()，颜色经 :style 注入） */
const strokeC = computed(() => (props.done ? 'var(--line-brand)' : 'var(--line-brand-muted)'));
</script>

<style scoped>
.stage-ico {
  width: 26px;
  height: 26px;
  flex-shrink: 0;
}
</style>
