<template>
  <view class="course-card pressable" hover-class="hv-press" :hover-stay-time="80" @tap="onTap">
    <view class="course-main">
      <view class="course-meta">
        <text class="theme-tag" :style="{ background: themeColorLight, color: themeColor }">{{ course.themeLabel }}</text>
        <text class="course-grade">{{ course.grade }} · {{ course.duration }}分钟 · {{ course.version }}</text>
        <!-- 收藏：贴右对齐，点击阻止冒泡不触发卡片跳转 -->
        <view class="course-fav">
          <FavButton :course-id="course.id" :box="48" :icon="28" />
        </view>
      </view>
      <text class="course-title">{{ course.title }}</text>
      <text class="course-summary">{{ course.summary }}</text>
      <slot name="action">
        <view class="course-action" @tap.stop="onAction">
          <text class="action-text">去备课</text>
        </view>
      </slot>
    </view>
    <view class="course-cover">
      <view class="cover-ico" :class="'cover-' + course.theme"></view>
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
  margin: 0 $space-m;
  padding: 28rpx 0;
  border-radius: $radius-card;
  border-bottom: 1rpx solid rgba(31, 52, 49, 0.1);
  background: transparent;
  display: flex;
  justify-content: space-between;
  overflow: hidden;
}

.course-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: $space-xs;
}

.theme-tag {
  padding: 4rpx 12rpx;
  border-radius: $radius-s;
  font-size: 20rpx;
  font-weight: $font-weight-bold;
}

.course-grade {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.course-fav {
  margin-left: auto;
  align-self: center;
}

.course-title {
  margin-top: $space-s;
  font-size: 30rpx;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.course-summary {
  margin-top: $space-xs;
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-action {
  margin-top: 18rpx;
  align-self: flex-start;
  padding: 0;
}

.action-text {
  font-size: $font-size-xs;
  color: $brand-primary; // 描边钮文字用墨绿，不填实底
  font-weight: $font-weight-bold;
}

.course-cover {
  width: 104rpx;
  height: 104rpx;
  margin-left: $space-m;
  border-radius: 18rpx;
  background: $brand-primary-light;
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
  width: 48rpx;
  height: 48rpx;
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
