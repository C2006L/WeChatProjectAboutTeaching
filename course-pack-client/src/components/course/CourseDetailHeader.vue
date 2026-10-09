<template>
  <view class="detail-header">
    <view class="theme-tag" :style="{ background: themeColorLight, color: themeColor }">
      {{ course.themeLabel }}
    </view>
    <text class="detail-title">{{ course.title }}</text>
    <view class="detail-meta">
      <text class="meta-item">{{ course.grade }}</text>
      <text class="meta-divider">·</text>
      <text class="meta-item">{{ course.duration }}分钟</text>
      <text class="meta-divider">·</text>
      <text class="meta-item">{{ course.version }}</text>
    </view>
    <text class="detail-summary">{{ course.summary }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { CourseSummary, CourseTheme } from '@/types/mock';

interface Props {
  course: CourseSummary;
}

const props = defineProps<Props>();

// 主题色映射（对应 tokens --theme-*）
const themeColorMap: Record<CourseTheme, string> = {
  body: '#4F8F83',
  family: '#D99A62',
  respect: '#6B9BD0',
  growth: '#7BB57D',
  general: '#A78BCB',
};

const themeColor = computed(() => themeColorMap[props.course.theme]);
const themeColorLight = computed(() => themeColorMap[props.course.theme] + '1A');
</script>

<style scoped lang="scss">
.detail-header {
  padding: $space-m;
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
}

.theme-tag {
  display: inline-flex;
  padding: 6rpx 16rpx;
  border-radius: $radius-s;
  font-size: $font-size-xs;
  font-weight: $font-weight-bold;
}

.detail-title {
  display: block;
  margin-top: $space-s;
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  line-height: 1.5;
}

.detail-meta {
  margin-top: $space-s;
  display: flex;
  align-items: center;
  gap: $space-xs;
}

.meta-item {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.meta-divider {
  font-size: $font-size-xs;
  color: $text-disabled;
}

.detail-summary {
  display: block;
  margin-top: $space-s;
  font-size: $font-size-s;
  color: $text-regular;
  line-height: 1.6;
}
</style>
