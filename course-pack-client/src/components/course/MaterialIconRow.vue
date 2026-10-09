<template>
  <view class="material-row">
    <view
      v-for="m in materialItems"
      :key="m.type"
      class="material-item"
      @tap="onTap(m.label)"
    >
      <view class="material-icon" :class="'mi-' + m.type"></view>
      <text class="material-name">{{ m.label }}</text>
      <text class="material-count">{{ m.count }}份</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { CourseMaterials } from '@/types/mock';

interface Props {
  materials: CourseMaterials;
}

const props = defineProps<Props>();

const materialItems = computed(() => [
  { type: 'plan', label: '教案', count: props.materials.plan },
  { type: 'slides', label: '课件', count: props.materials.slides },
  { type: 'toolkit', label: '教具清单', count: props.materials.toolkit },
  { type: 'video', label: '课程视频', count: props.materials.video },
]);

function onTap(label: string) {
  uni.showToast({ title: `${label}下载功能即将开放`, icon: 'none' });
}
</script>

<style scoped lang="scss">
.material-row {
  display: flex;
  justify-content: space-between;
}

.material-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
}

/* 1.5px 线性墨绿单色（B 轨图标规范，替代彩色 emoji） */
.material-icon {
  width: 44rpx;
  height: 44rpx;
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}

.mi-plan {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 3h8l4 4v14H6Z'/%3E%3Cpath d='M14 3v4h4'/%3E%3Cpath d='M9 12h6M9 16h6'/%3E%3C/svg%3E");
}

.mi-slides {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3.5' y='4.5' width='17' height='11' rx='1.5'/%3E%3Cpath d='M12 15.5V19'/%3E%3Cpath d='M8 21l4-2 4 2'/%3E%3C/svg%3E");
}

.mi-toolkit {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='4' y='8.5' width='16' height='11' rx='2'/%3E%3Cpath d='M9.5 8.5V6.5A1.5 1.5 0 0 1 11 5h2a1.5 1.5 0 0 1 1.5 1.5v2'/%3E%3Cpath d='M4 13h16'/%3E%3C/svg%3E");
}

.mi-video {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='8.5'/%3E%3Cpath d='M10 8.8v6.4l5.2-3.2Z'/%3E%3C/svg%3E");
}

.material-name {
  font-size: $font-size-xs;
  color: $text-regular;
}

.material-count {
  font-size: 20rpx;
  color: $text-secondary;
}
</style>
