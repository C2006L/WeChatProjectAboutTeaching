<template>
  <view class="qa-item" @tap="toggle">
    <view class="qa-head">
      <text class="qa-category">{{ qa.category }}</text>
      <text class="qa-question">{{ qa.question }}</text>
      <view class="qa-arrow" :class="{ open: expanded }"></view>
    </view>

    <view v-if="expanded" class="qa-body" @tap.stop>
      <text class="qa-scene">情景：{{ qa.scene }}</text>
      <view class="qa-response">
        <text class="qa-label">推荐回应</text>
        <text class="qa-text">{{ qa.response }}</text>
      </view>
      <view class="qa-tips">
        <view v-for="(tip, i) in qa.tips" :key="i" class="tip-row">
          <view class="tip-icon" :class="tip.type"></view>
          <text class="tip-text">{{ tip.content }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { QA } from '@/types/mock';

interface Props {
  qa: QA;
}

defineProps<Props>();

const expanded = ref(false);

function toggle() {
  expanded.value = !expanded.value;
}
</script>

<style scoped lang="scss">
.qa-item {
  background: $bg-card;
  border: $border-card;
  border-radius: 30rpx 14rpx 30rpx 14rpx;
  padding: $space-m;
}

.qa-head {
  display: flex;
  align-items: center;
  gap: $space-s;
}

.qa-category {
  flex-shrink: 0;
  padding: 4rpx 12rpx;
  border-radius: $radius-s;
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  background: $brand-primary-light;
  color: $brand-primary;
}

.qa-question {
  flex: 1;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
  line-height: 1.4;
}

/* 展开箭头：SVG 组件（禁裸写字符），展开时旋转 180° */
.qa-arrow {
  flex-shrink: 0;
  width: 32rpx;
  height: 32rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2382918D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m6 9 6 6 6-6'/%3E%3C/svg%3E");
  transition: transform $motion-base $motion-ease;
}

.qa-arrow.open {
  transform: rotate(180deg);
}

.qa-body {
  margin-top: $space-s;
  padding-top: $space-s;
  border-top: 1rpx solid $border;
  display: flex;
  flex-direction: column;
  gap: $space-s;
}

.qa-scene {
  font-size: $font-size-xs;
  color: $text-secondary;
  line-height: 1.5;
}

.qa-response {
  background: $brand-primary-light;
  border-radius: $radius-m;
  padding: $space-s $space-m;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.qa-label {
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  color: $brand-primary;
}

.qa-text {
  font-size: $font-size-xs;
  color: $text-primary;
  line-height: 1.6;
}

.tip-row {
  display: flex;
  align-items: flex-start;
  gap: $space-s;
}

/* 建议/注意图标：1.5px 线性墨绿单色（对勾 / 警示三角） */
.tip-icon {
  flex-shrink: 0;
  width: 28rpx;
  height: 28rpx;
  margin-top: 2rpx;
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}

.tip-icon.do {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23253C37' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m5 12.5 4.5 4.5L19 7.5'/%3E%3C/svg%3E");
}

.tip-icon.caution {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23253C37' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 4 21 19.5H3Z'/%3E%3Cpath d='M12 10.5v4'/%3E%3Cpath d='M12 17.2v.1'/%3E%3C/svg%3E");
}

.tip-text {
  flex: 1;
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.5;
}
</style>
