<template>
  <view class="gp-track" :style="{ height: height + 'rpx', borderRadius: height / 2 + 'rpx' }">
    <!-- 生长动画：scaleX 0→目标（600ms 统一缓动，不用 width，不触发重排） -->
    <view class="gp-fill" :style="{ transform: 'scaleX(' + ratio + ')', borderRadius: height / 2 + 'rpx' }"></view>
    <!-- 末端小芽：进度长满后淡入 -->
    <view class="gp-sprout" :style="{ left: clamped + '%' }"></view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  percent: number;
  /** 轨道高度（rpx），首页 8 / 培训页 16 */
  height?: number;
}

const props = withDefaults(defineProps<Props>(), { height: 8 });

const clamped = computed(() => Math.min(100, Math.max(0, props.percent)));
const ratio = computed(() => clamped.value / 100);
</script>

<style scoped lang="scss">
/* 首页与培训页统一复用的生长进度条 */
.gp-track {
  position: relative;
  background: $brand-primary-light;
}

.gp-fill {
  width: 100%;
  height: 100%;
  background: $brand-primary;
  transform-origin: 0 50%;
  animation: gp-grow $motion-slow $motion-ease both;
}

@keyframes gp-grow {
  from {
    transform: scaleX(0);
  }
}

.gp-sprout {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 34rpx;
  height: 34rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23176F66' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M7 20h10'/%3E%3Cpath d='M10 20c5.5-2.5.8-6.4 3-10'/%3E%3Cpath d='M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z'/%3E%3Cpath d='M14.1 6a7 7 0 0 0-1.1 4c1.9-.1 3.3-.6 4.3-1.4 1-1 1.6-2.3 1.7-4.6-2.7.1-4 1-4.9 2z'/%3E%3C/svg%3E");
  animation: gp-fade $motion-base $motion-ease $motion-slow both;
}

@keyframes gp-fade {
  from {
    opacity: 0;
  }
}
</style>
