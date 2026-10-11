<template>
  <view class="tool-card pressable" :class="{ featured }" hover-class="hv-press" :hover-stay-time="80" @tap="onTap">
    <view class="icon-wrap" :class="'i-' + entry.icon + ' ' + (entry.tile === 'g' ? 'tile-g' : 'tile-m')"></view>
    <view class="copy">
      <text class="title">{{ entry.title }}</text>
      <text class="desc">{{ entry.desc }}</text>
      <text v-if="featured" class="case-meta">公开案例 · 我的草稿 · 审核进度</text>
    </view>
    <view class="arrow"></view>
  </view>
</template>

<script setup lang="ts">
import type { ToolEntry } from '@/types/mock';

interface Props {
  entry: ToolEntry;
  featured?: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'navigate', entry: ToolEntry): void }>();

function onTap() {
  emit('navigate', props.entry);
}
</script>

<style scoped lang="scss">
/* 卡片材质 token */
.tool-card {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 18rpx;
  padding: 22rpx 24rpx;
  background: $bg-card;
  border: $border-card;
  border-radius: 30rpx 14rpx 30rpx 14rpx;
  min-height: 112rpx;
}
.tool-card.featured { min-height: 152rpx; border-top-left-radius: $radius-card-tl; background: $brand-primary-light; }

/* 图标方砖：72rpx 方形容器，米/浅绿交替 */
.icon-wrap {
  flex-shrink: 0;
  width: 60rpx;
  height: 60rpx;
  border-radius: $radius-m;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 32rpx 32rpx;

  &.tile-m {
    background-color: $tile-m;
  }
  &.tile-g {
    background-color: $tile-g;
  }
}

.copy { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 6rpx; }
.featured .title { color: $brand-primary-dark; font-size: 31rpx; }

.title {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.desc {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.5;
}

.case-meta { margin-top: 6rpx; font-size: 22rpx; color: $brand-primary-dark; }
.arrow { flex-shrink: 0; width: 26rpx; height: 26rpx; opacity: .7; background: center / contain no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237B8986' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E"); }

/* 线稿 icon（CSS background-data-uri SVG，禁用 emoji）。描边色为品牌绿等价值（data-uri 内无法引用变量） */
.icon-wrap.i-qa {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 3a8 8 0 0 0-8 8 8 8 0 0 0 3 6.2V21l4.5-1A8 8 0 1 0 12 3Z'/%3E%3Cpath d='M9 9.5a3 3 0 1 1 5 2.3c-.9.7-1.5 1-1.5 2.2'/%3E%3Ccircle cx='12' cy='16.5' r='.6'/%3E%3C/svg%3E");
}
.icon-wrap.i-prepare {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M9 5h11M9 12h11M9 19h11'/%3E%3Cpath d='m4.5 4.5 1.5 1.5 2.5-2.5M4.5 11.5 6 13l2.5-2.5M4.5 18.5 6 20l2.5-2.5'/%3E%3C/svg%3E");
}
.icon-wrap.i-family {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 11 12 4l8 7'/%3E%3Cpath d='M6 9.8V20h12V9.8'/%3E%3Cpath d='M9.5 20v-5.5h5V20'/%3E%3C/svg%3E");
}
.icon-wrap.i-activity {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 9.5h6.5V5H4.5A.5.5 0 0 0 4 5.5v4Z'/%3E%3Cpath d='M14 9.5h6V5.5a.5.5 0 0 0-.5-.5H14v4.5Z'/%3E%3Cpath d='M4 19h6.5v-4.5H4.5a.5.5 0 0 0-.5.5v4Z'/%3E%3Cpath d='M14 19h6v-4.5h-6V19Z'/%3E%3C/svg%3E");
}
.icon-wrap.i-case {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 5.5A2.5 2.5 0 0 1 6.5 3H20v16H6.5A2.5 2.5 0 0 0 4 21.5v-16Z'/%3E%3Cpath d='M4 18.5A2.5 2.5 0 0 1 6.5 16H20M8 7h8M8 10.5h6'/%3E%3C/svg%3E");
}
</style>
