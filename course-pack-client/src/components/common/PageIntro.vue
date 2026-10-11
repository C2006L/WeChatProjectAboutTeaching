<template>
  <view class="page-intro">
    <view class="kicker-line"><view class="kicker-mark"></view><text class="kicker">伴芽小站 / {{ kicker }}</text></view>
    <text class="title">{{ title }}</text>
    <text v-if="description" class="description">{{ description }}</text>
  </view>
</template>

<script setup lang="ts">
defineProps<{
  kicker: string;
  title: string;
  description?: string;
}>();
</script>

<style scoped lang="scss">
.page-intro { display: flex; flex-direction: column; padding: 20rpx $space-m 24rpx; }
.kicker-line { display: flex; align-items: center; gap: 10rpx; }
.kicker-mark { width: 16rpx; height: 16rpx; border-radius: 9rpx 3rpx 9rpx 3rpx; background: $brand-secondary; transform: rotate(-16deg); }
.kicker { font-size: 22rpx; font-weight: 600; color: $brand-primary; letter-spacing: 2rpx; }
.title { margin-top: 6rpx; font-size: 42rpx; line-height: 1.3; font-weight: 700; color: $text-primary; letter-spacing: 1rpx; }
.description { margin-top: 6rpx; font-size: 25rpx; line-height: 1.5; color: $text-secondary; }
</style>
