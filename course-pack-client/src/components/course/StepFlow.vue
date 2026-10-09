<template>
  <view class="step-flow">
    <view v-for="(step, i) in steps" :key="step.index" class="step-item">
      <view class="step-rail">
        <view class="step-dot">{{ step.index }}</view>
        <view v-if="i < steps.length - 1" class="step-line"></view>
      </view>
      <view class="step-body">
        <view class="step-head">
          <text class="step-title">{{ step.title }}</text>
          <text class="step-duration">{{ step.duration }}分钟</text>
        </view>
        <text class="step-desc">{{ step.desc }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { TeachingStep } from '@/types/mock';

interface Props {
  steps: TeachingStep[];
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.step-flow {
  display: flex;
  flex-direction: column;
}

.step-item {
  display: flex;
}

.step-rail {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: $space-s;
}

.step-dot {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: $brand-primary;
  color: #FFFFFF;
  font-size: $font-size-xs;
  font-weight: $font-weight-bold;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-line {
  width: 2rpx;
  flex: 1;
  min-height: 32rpx;
  background: $border;
  margin: 8rpx 0;
}

.step-body {
  flex: 1;
  padding-bottom: $space-m;
  min-width: 0;
}

.step-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.step-title {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.step-duration {
  font-size: 20rpx;
  color: $text-secondary;
  background: $brand-primary-light;
  padding: 2rpx 12rpx;
  border-radius: $radius-s;
}

.step-desc {
  display: block;
  margin-top: 6rpx;
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.5;
}
</style>
