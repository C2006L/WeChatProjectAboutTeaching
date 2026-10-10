<template>
  <view class="menu-grid">
    <text v-if="title" class="grid-title">{{ title }}</text>
    <view class="grid">
      <view
        v-for="item in items"
        :key="item.label"
        class="grid-item pressable"
        hover-class="hv-press"
        :hover-stay-time="80"
        @tap="onTap(item)"
      >
        <view class="item-icon" :class="'i-' + item.icon"></view>
        <view class="item-copy">
          <text class="item-label">{{ item.label }}</text>
          <text v-if="item.meta" class="item-meta">{{ item.meta }}</text>
        </view>
        <view class="item-arrow"></view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { MineMenuItem } from '@/types/mock';

interface Props {
  items: MineMenuItem[];
  title?: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'navigate', item: MineMenuItem): void }>();

function onTap(item: MineMenuItem) {
  emit('navigate', item);
}
</script>

<style scoped lang="scss">
.menu-grid {
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  padding: 28rpx $space-m 4rpx;
}

.grid-title {
  display: block;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: 12rpx;
}

.grid { display: flex; flex-direction: column; }

.grid-item {
  width: 100%;
  min-height: 104rpx;
  display: flex;
  align-items: center;
  gap: $space-s;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $border;
}
.grid-item:last-child { border-bottom: 0; }

/* 图标方砖：56rpx 方形容器（墨绿单色线性图标） */
.item-icon {
  flex-shrink: 0;
  width: 56rpx;
  height: 56rpx;
  border-radius: $radius-s;
  background-color: $bg-oat;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 32rpx 32rpx;
}

.item-copy { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4rpx; }

.item-label {
  font-size: $font-size-s;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.item-meta { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: $font-size-xs; color: $text-secondary; }

.item-arrow {
  flex-shrink: 0;
  width: 28rpx;
  height: 28rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237B8986' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E");
}

/* 线稿 icon（CSS background-data-uri SVG，禁用 emoji）。描边色为文本二级等价值 */
.item-icon.i-book {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M5 5h12a2 2 0 0 1 2 2v13h-14a2 2 0 0 1-2-2Z'/%3E%3Cpath d='M19 17H5'/%3E%3C/svg%3E");
}
.item-icon.i-star {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m12 4 2.4 4.9 5.4.8-3.9 3.8.9 5.4-4.8-2.5-4.8 2.5.9-5.4L4.2 9.7l5.4-.8Z'/%3E%3C/svg%3E");
}
.item-icon.i-download {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 4v11M7.5 11.5 12 16l4.5-4.5'/%3E%3Cpath d='M5 20h14'/%3E%3C/svg%3E");
}
.item-icon.i-case {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M3.5 6.5h6.5l2 2.5h8.5v11h-17Z'/%3E%3C/svg%3E");
}
.item-icon.i-question {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 6a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H10.5L5 20Z'/%3E%3Ccircle cx='12' cy='11' r='.7'/%3E%3C/svg%3E");
}
.item-icon.i-setting {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 8h14M4 16h14'/%3E%3Cpath d='M8 5v6M15 13v6'/%3E%3C/svg%3E");
}
.item-icon.i-about {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 20v-7'/%3E%3Cpath d='M12 14c-3.5 0-6-2-6-5.5C9.5 8.5 12 10.5 12 14Z'/%3E%3Cpath d='M12 14c3.5 0 6-2 6-5.5C14.5 8.5 12 10.5 12 14Z'/%3E%3C/svg%3E");
}
.item-icon.i-help {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='8.5'/%3E%3Cpath d='M9.6 9.3a2.4 2.4 0 1 1 3.3 2.2c-.8.3-1.1.8-1.1 1.6'/%3E%3Ccircle cx='11.9' cy='16.3' r='.6' fill='%2336524D' stroke='none'/%3E%3C/svg%3E");
}
</style>
