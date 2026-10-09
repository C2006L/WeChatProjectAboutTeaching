<template>
  <div class="stat-card">
    <div class="stat-head">
      <span class="label">{{ label }}</span>
      <span class="trend" :class="trendClass">
      <span class="arrow">{{ trend.includes('↑') ? '↑' : trend.includes('↓') ? '↓' : '' }}</span>
      {{ trend.replace(/较上月\s*[↑↓]?\s*/, '较上月 ') }}
      </span>
    </div>
    <span class="value">{{ displayValue }}<span class="unit">{{ unit }}</span></span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useCountUp } from '@/utils/useCountUp';

interface Props {
  label: string;
  value: number | string;
  unit: string;
  trend: string;
}

const props = defineProps<Props>();

// 数值增长动画（count-up，800ms ease-out cubic）；字符串值原样展示
const numericSrc = computed(() => (typeof props.value === 'number' ? props.value : 0));
const counted = useCountUp(numericSrc, 800);
const displayValue = computed(() =>
  typeof props.value === 'number' ? String(counted.value) : props.value,
);

// 上升用浅绿，下降用暖橙（V2：趋势箭头 12px 彩色）
const trendClass = computed(() =>
  props.trend.includes('↓') ? 'down' : 'up',
);
</script>

<style scoped>
.stat-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  background: var(--bg-card);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  min-height: 112px;
}
.stat-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
/* V2：数字为大号衬线墨绿主视觉，卡内禁图标；精修：34px 强化视觉锚点 */
.value {
  font-size: 30px;
  font-weight: 700;
  color: var(--brand-deep);
  line-height: 1.1;
}
.unit {
  font-family: var(--font-family);
  font-size: var(--font-size-s);
  font-weight: 400;
  color: var(--text-secondary);
  margin-left: 4px;
}
.label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-regular);
}
.trend {
  font-size: 12px;
  color: var(--succ);
}
.trend.down {
  color: var(--warn);
}
.arrow {
  font-size: 12px;
}
</style>
