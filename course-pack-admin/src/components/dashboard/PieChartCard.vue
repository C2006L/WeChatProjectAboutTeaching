<template>
  <div class="chart-card">
    <h4 class="title">{{ title }}</h4>
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue';
import type { ECharts } from 'echarts/core';
import type { ThemeSlice } from '@/types/dashboard';
import { cssVar } from '@/utils/theme';

interface Props {
  title: string;
  data: ThemeSlice[];
}

const props = defineProps<Props>();

const chartRef = ref<HTMLDivElement>();
let chart: ECharts | null = null;

const total = computed(() => props.data.reduce((sum, d) => sum + d.value, 0));

/** 主题 → 全局 token 色值（V3：数据层不背颜色，图表侧查表；Canvas 需解析为实际值） */
const THEME_TOKEN: Record<string, string> = {
  身体与安全: '--theme-body',
  家庭与合作: '--theme-family',
  人际与尊重: '--theme-respect',
  成长与变化: '--theme-growth',
  综合主题: '--theme-general',
};

function themeColor(theme: string): string {
  const token = THEME_TOKEN[theme] ?? '--theme-body';
  return cssVar(token, '#4F8F83');
}

async function renderChart() {
  const echarts = (await import('@/utils/echarts')).default;
  if (!chartRef.value) return;
  if (!chart) {
    chart = echarts.init(chartRef.value);
  }
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (p: { name: string; value: number; percent: number }) =>
        `${p.name}<br/>${p.value} 次（${p.percent}%）`,
    },
    legend: { bottom: 0, textStyle: { color: cssVar('--text-regular', '#4E5969') } },
    series: [
      {
        name: '课程主题',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: cssVar('--on-brand', '#FFFFFF'), borderWidth: 2 },
        label: { show: false },
        data: props.data.map((d) => ({ name: d.theme, value: d.value, itemStyle: { color: themeColor(d.theme) } })),
      },
    ],
  });
}

function onResize() {
  chart?.resize();
}

onMounted(() => {
  renderChart();
  window.addEventListener('resize', onResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize);
  chart?.dispose();
  chart = null;
});

watch(() => props.data, renderChart);
</script>

<style scoped>
.chart-card {
  padding: 24px;                        /* 卡片内边距统一 24px */
  background: var(--bg-card);           /* #FBF9F4 */
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
}
.title {
  margin: 0 0 var(--space-s);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.chart {
  width: 100%;
  height: 300px;
}
</style>
