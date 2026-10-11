<template>
  <view class="course-card pressable" hover-class="hv-press" :hover-stay-time="80" @tap="onTap">
    <view class="course-head">
      <view class="course-cover" :style="{ background: themeColorLight }"><view class="cover-ico" :class="'cover-' + course.theme"></view></view>
      <view class="course-meta">
        <text class="theme-tag" :style="{ color: themeColor }">{{ course.themeLabel }}</text>
        <text class="course-grade">{{ course.grade }} · {{ course.duration }}分钟</text>
      </view>
      <view class="course-fav"><FavButton :course-id="course.id" :box="52" :icon="27" /></view>
    </view>
    <text class="course-title">{{ course.title }}</text>
    <text class="course-summary">{{ course.summary }}</text>
    <view class="course-foot">
      <text class="version-tag">{{ course.version }}</text>
      <slot name="action">
        <view class="course-action" @tap.stop="onAction"><text class="action-text">开始备课</text><view class="action-arrow"></view></view>
      </slot>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import FavButton from '@/components/common/FavButton.vue';
import type { CourseSummary, CourseTheme } from '@/types/mock';

interface Props {
  course: CourseSummary;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'click'): void; (e: 'action'): void }>();

// 主题色映射（V2 对齐 tokens，综合主题禁紫）
const themeColorMap: Record<CourseTheme, string> = {
  body: '#4F8F83',
  family: '#D99A62',
  respect: '#8FB7C5',
  growth: '#5D9B78',
  general: '#A8B5A0',
};

const themeColor = computed(() => themeColorMap[props.course.theme]);
const themeColorLight = computed(() => themeColorMap[props.course.theme] + '1A');

function onTap() {
  emit('click');
}

function onAction() {
  emit('action');
}
</script>

<style scoped lang="scss">
.course-card {
  position: relative;
  padding: 22rpx 24rpx 20rpx;
  border: $border-card;
  border-radius: 32rpx 14rpx 32rpx 14rpx;
  background: $bg-card;
  overflow: hidden;
}
.course-card::before { content: ''; position: absolute; top: 0; bottom: 0; left: 0; width: 6rpx; background: $brand-sprout; }
.course-head { display: flex; align-items: center; gap: 14rpx; }
.course-meta { flex: 1; min-width: 0; display: flex; align-items: center; gap: 10rpx; }

.theme-tag {
  font-size: 23rpx;
  font-weight: $font-weight-bold;
}

.course-grade {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.course-fav { flex-shrink: 0; }

.course-title {
  display: block;
  overflow: hidden;
  margin-top: 16rpx;
  font-size: 32rpx;
  font-weight: $font-weight-bold;
  color: $text-primary;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-summary {
  margin-top: 5rpx;
  font-size: 24rpx;
  color: $text-regular;
  line-height: 1.45;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-foot { display: flex; align-items: center; justify-content: space-between; margin-top: 14rpx; }
.version-tag { padding: 4rpx 12rpx; border-radius: 18rpx; background: $bg-oat; font-size: 21rpx; color: $text-secondary; }
.course-action { display: flex; align-items: center; gap: 2rpx; padding: 9rpx 14rpx; border-radius: 24rpx 9rpx 24rpx 24rpx; background: $brand-primary; }

.action-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: $font-weight-bold;
}
.action-arrow { width: 22rpx; height: 22rpx; background: center / contain no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='1.7' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E"); }

.course-cover {
  width: 66rpx;
  height: 66rpx;
  border-radius: 24rpx 9rpx 18rpx 9rpx;
  border: 0;
  box-sizing: border-box;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.cover-ico {
  width: 34rpx;
  height: 34rpx;
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}

/* 线性主题图标：墨绿 #36524D 单色线条、1.5px、透明底（盾牌/房子/爱心/小芽/罗盘） */
.cover-body {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 3 4.5 6v6c0 4.5 3 8 7.5 9 4.5-1 7.5-4.5 7.5-9V6L12 3Z'/%3E%3Cpath d='M9 12l2 2 4-4.5'/%3E%3C/svg%3E");
}
.cover-family {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 11 12 4l8 7'/%3E%3Cpath d='M6 9.8V20h12V9.8'/%3E%3Cpath d='M9.5 20v-5.5h5V20'/%3E%3C/svg%3E");
}
.cover-respect {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 5.5c-2-2.4-6-1.5-6 1.8 0 2.5 2.4 4 6 7.2 3.6-3.2 6-4.7 6-7.2 0-3.3-4-4.2-6-1.8Z'/%3E%3C/svg%3E");
}
.cover-growth {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 20V10'/%3E%3Cpath d='M12 10c0-3.5 2.5-6 6-6 0 3.5-2.5 6-6 6Z'/%3E%3Cpath d='M12 14c0-2.8-2-4.8-4.8-4.8 0 2.8 2 4.8 4.8 4.8Z'/%3E%3C/svg%3E");
}
.cover-general {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 3v4M12 21v-4M3 12h4M21 12h-4'/%3E%3Cpath d='M12 8.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7Z'/%3E%3C/svg%3E");
}
</style>
