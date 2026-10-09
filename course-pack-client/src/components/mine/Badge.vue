<template>
  <view class="badge">
    <view class="badge-icon"></view>
    <text class="badge-text">{{ text }}</text>
  </view>
</template>

<script setup lang="ts">
interface Props {
  text: string;
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.badge {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  padding: 6rpx 16rpx;
  border-radius: $radius-s;
  background: rgba(217, 154, 98, 0.16);
}

.badge-icon {
  width: 24rpx;
  height: 24rpx;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 20v-7'/%3E%3Cpath d='M12 14c-3.5 0-6-2-6-5.5C9.5 8.5 12 10.5 12 14Z'/%3E%3Cpath d='M12 14c3.5 0 6-2 6-5.5C14.5 8.5 12 10.5 12 14Z'/%3E%3C/svg%3E");
}

.badge-text {
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  color: $brand-secondary;
}
</style>
