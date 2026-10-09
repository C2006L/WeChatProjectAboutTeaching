<template>
  <div class="chart-card">
    <h4 class="title">{{ title }}</h4>
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import type { ECharts } from 'echarts/core';
import type { GrowthTrend } from '@/types/dashboard';
import { cssVar, cssRgba } from '@/utils/theme';

interface Props {
  title: string;
  data: GrowthTrend;
}

const props = defineProps<Props>();

const chartRef = ref<HTMLDivElement>();
let chart: ECharts | null = null;

async function renderChart() {
  const echarts = (await import('@/utils/echarts')).default;
  if (!chartRef.value) return;
  if (!chart) {
    chart = echarts.init(chartRef.value);
  }
  // 图表色统一取自设计 token（Canvas 渲染无法直接使用 CSS 变量）
  const C = {
    textRegular: cssVar('--text-regular', '#4E5969'),
    textSecondary: cssVar('--text-secondary', '#86909C'),
    axisLine: cssVar('--axis-line', 'rgba(54, 82, 77, 0.24)'),
    splitLine: cssVar('--divider-soft', 'rgba(54, 82, 77, 0.08)'),
    info: cssVar('--info', '#8FB7C5'),
    infoFade: cssRgba('--rgb-info', 0.18, '143, 183, 197'),
    infoClear: cssRgba('--rgb-info', 0, '143, 183, 197'),
    ink: cssVar('--brand-ink', '#36524D'),
  };
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['服务教师', '学习次数'], bottom: 0, textStyle: { color: C.textRegular } },
    grid: { left: 48, right: 24, top: 24, bottom: 48 },
    xAxis: {
      type: 'category',
      data: props.data.months,
      // 轴线/网格线统一墨绿低透明度，禁中性灰
      axisLine: { lineStyle: { color: C.axisLine } },
      axisLabel: { color: C.textSecondary },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: C.splitLine } },
      axisLabel: { color: C.textSecondary },
    },
    series: [
      {
        name: '服务教师',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: props.data.teachers,
        // 辅线：浅青，与主墨绿形成数据主次对比
        itemStyle: { color: C.info },
        lineStyle: { color: C.info, width: 2 },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: C.infoFade },
              { offset: 1, color: C.infoClear },
            ],
          },
        },
      },
      {
        name: '学习次数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: props.data.studies,
        // 主线：墨绿主色 + 加粗，强化视觉锚点
        itemStyle: { color: C.ink },
        lineStyle: { color: C.ink, width: 3 },
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
  padding: var(--space-m);
  background: var(--bg-card);
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
