<template>
  <view class="progress-card">
    <view class="head">
      <text class="title">我的培训进度</text>
      <text class="count">已完成 {{ completed }}/{{ total }} 节</text>
    </view>

    <view class="body">
      <view class="percent-wrap">
        <text class="percent-num">{{ percentDisplay }}</text>
        <text class="percent-unit">%</text>
      </view>
      <view class="bar-area">
        <!-- 进度条：与首页统一复用 GrowProgress（scaleX 生长 600ms + 末端小芽淡入） -->
        <GrowProgress :percent="percent" :height="16" />
        <view
          class="btn-continue pressable"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="emit('continue')"
        >
          <text class="btn-text">继续学习</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import GrowProgress from '@/components/common/GrowProgress.vue';
import { useCountUp } from '@/utils/useCountUp';

interface Props {
  percent: number;
  completed: number;
  total: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'continue'): void }>();

/** 完成率数字滚动（600ms count-up） */
const percentDisplay = useCountUp(computed(() => props.percent));
</script>

<style scoped lang="scss">
.progress-card {
  position: relative;
  overflow: hidden;
  background: $bg-hero;
  border-radius: $radius-card-lg;
  border-top-left-radius: 56rpx;
  padding: 32rpx;
}
.progress-card::after { content: ''; position: absolute; right: 0; bottom: 0; width: 92rpx; height: 12rpx; border-radius: 12rpx 0 0 0; background: $brand-secondary; }

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  font-size: 30rpx;
  font-weight: $font-weight-bold;
  color: #fff;
}

.count {
  font-size: $font-size-xs;
  color: rgba(255,255,255,.7);
}

.body {
  margin-top: $space-m;
  display: flex;
  align-items: center;
  gap: $space-m;
}

/* 完成率数字：衬线 + 增大字号 + 暖橙点缀 */
.percent-wrap {
  display: flex;
  align-items: baseline;
}

.percent-num {
  font-family: $font-family-serif;
  font-size: 88rpx;
  font-weight: $font-weight-bold;
  color: #fff;
  line-height: 1;
}

.percent-unit {
  margin-left: 6rpx;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $brand-secondary;
}

.bar-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $space-s;
}

.btn-continue {
  align-self: flex-end;
  padding: 10rpx 28rpx;
  border-radius: 26rpx;
  background: #fff;
  box-shadow: 0 8rpx 18rpx rgba(8,55,50,.18);
}

.btn-text {
  font-size: $font-size-xs;
  color: $brand-primary-dark;
  font-weight: $font-weight-bold;
}
</style>
