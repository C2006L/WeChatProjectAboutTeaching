<template>
  <view class="empty-state">
    <view class="illu-wrap">
      <!-- 线性小芽插画（B 轨单色，替代彩色 emoji） -->
      <view class="illu-sprout spray"></view>
    </view>
    <text class="empty-text">{{ text }}</text>
  </view>
</template>

<script setup lang="ts">
interface Props {
  text: string;
}

defineProps<Props>();
</script>

<style scoped lang="scss">
/* 空态插画卡：套用全局材质 token，线性单色小芽，替代彩色 emoji 空态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $space-m;
  padding: $space-xl $space-m;
  border-radius: $radius-card;
  background: $bg-card;
  border: $border-card;
}

.illu-wrap {
  width: 140rpx;
  height: 140rpx;
  border-radius: $radius-m;
  background: $bg-oat;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spray {
  width: 72rpx;
  height: 72rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M13 3 4.5 16.5a2.5 2.5 0 0 0 4 1.2L13 14'/%3E%3Cpath d='M13 3c3.8 1.6 6.2 4.7 6.7 9.1a3.5 3.5 0 0 1-5.6 2.7L13 14'/%3E%3Cpath d='M13 11V20'/%3E%3C/svg%3E");
}

.empty-text {
  font-size: $font-size-s;
  color: $text-secondary;
  text-align: center;
  line-height: 1.5;
}
</style>