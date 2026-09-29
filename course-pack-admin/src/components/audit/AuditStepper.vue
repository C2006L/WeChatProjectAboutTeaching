<template>
  <div class="stepper">
    <div class="steps">
      <div
        v-for="(s, i) in stages"
        :key="s.key"
        class="step"
      >
        <!-- 植物节点 SVG：完成态填充品牌绿，未完成为线框 -->
        <span class="node" :class="{ done: s.done }">
          <svg class="node-svg" viewBox="0 0 48 48" fill="none" :style="{ color: s.done ? 'var(--line-brand)' : 'var(--line-brand-muted)' }">
            <g stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <!-- 种子 -->
              <g v-if="s.key === 'education'">
                <ellipse cx="24" cy="30" rx="10" ry="11" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
                <ellipse cx="24" cy="30" rx="4" ry="5" fill="none" stroke-width="1.4" />
              </g>
              <!-- 发芽 -->
              <g v-else-if="s.key === 'psychology'">
                <path d="M24 38V22" stroke-width="2.5" />
                <path d="M24 26c-8 0-12-5-12-11 8 0 12 5 12 11Z" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
              </g>
              <!-- 长叶 -->
              <g v-else-if="s.key === 'sociology'">
                <path d="M24 40V20" stroke-width="2.5" />
                <path d="M24 24c-9-1-15-7-15-15 9 1 15 7 15 15Z" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
                <path d="M24 24c9 0 15-5 15-14-9 0-15 5-15 14Z" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
              </g>
              <!-- 小树 -->
              <g v-else-if="s.key === 'principal'">
                <path d="M24 40V28" stroke-width="2.5" />
                <path d="M24 20c-8 0-15-5-15-13 8 0 15 5 15 13Z" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
                <path d="M24 20c8 0 15-5 15-13-8 0-15 5-15 13Z" :style="{ fill: s.done ? 'var(--fill-brand-soft)' : 'none' }" stroke-width="2" />
                <circle cx="24" cy="32" r="3" :style="{ fill: s.done ? 'var(--brand-primary)' : 'none' }" stroke-width="1.6" />
              </g>
            </g>
          </svg>
        </span>
        <span class="label" :class="{ active: s.done }">{{ s.label }}</span>
        <!-- 虚线连接（非最后节点） -->
        <span v-if="i < stages.length - 1" class="connector"></span>
      </div>
    </div>
    <!-- 进度百分比：衬线体 -->
    <div class="percent">{{ progress }}%</div>
  </div>
</template>

<script setup lang="ts">
import type { AuditStage } from '@/types/dashboard';

interface Props {
  stages: AuditStage[];
  progress: number;
}

defineProps<Props>();
</script>

<style scoped>
.stepper {
  display: flex;
  align-items: center;
  gap: var(--space-l);
}
.steps {
  flex: 1;
  display: flex;
  align-items: flex-start;
}
.step {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 88px;
}
.node {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
}
.node.done {
  border-color: var(--brand-primary);
}
.node-svg {
  width: 44px;
  height: 44px;
}
.label {
  font-size: 13px;
  color: var(--text-secondary);
  white-space: nowrap;
}
.label.active {
  color: var(--brand-primary-dark);
  font-weight: 600;
}
/* 节点间虚线连接 */
.connector {
  position: absolute;
  top: 28px;
  left: 100%;
  width: 100%;
  height: 0;
  border-top: 2px dashed var(--divider-soft);
  z-index: -1;
}
.percent {
  font-family: var(--font-serif);
  font-size: var(--font-size-xxl);
  font-weight: 650;
  color: var(--brand-deep);
  flex-shrink: 0;
}
</style>