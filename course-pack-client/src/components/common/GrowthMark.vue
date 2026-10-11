<template>
  <view class="growth-mark" :class="[`stage-${stage}`, `tone-${tone}`, { compact }]" aria-hidden="true">
    <view class="mark-stem" />
    <view class="mark-leaf mark-leaf-left" />
    <view class="mark-leaf mark-leaf-right" />
    <view v-if="stage === 'grow'" class="mark-leaf mark-leaf-top" />
    <view v-if="!compact" class="mark-page mark-page-left" />
    <view v-if="!compact" class="mark-page mark-page-right" />
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  stage?: 'seed' | 'sprout' | 'grow';
  tone?: 'brand' | 'light' | 'muted';
  compact?: boolean;
}>(), {
  stage: 'grow',
  tone: 'brand',
  compact: false,
});
</script>

<style scoped lang="scss">
.growth-mark { position: relative; width: 100%; height: 100%; color: $brand-primary; }
.tone-light { color: rgba(255, 255, 255, .7); }
.tone-muted { color: rgba(23, 111, 102, .2); }
.mark-stem { position: absolute; left: 50%; top: 30%; width: 0; height: 42%; border-left: 3rpx solid currentColor; transform: translateX(-50%) rotate(-2deg); transform-origin: 50% 100%; }
.mark-leaf { position: absolute; width: 28%; height: 18%; border: 3rpx solid currentColor; background: transparent; }
.mark-leaf-left { left: 22%; top: 28%; border-radius: 90% 12% 90% 12%; transform: rotate(24deg); }
.mark-leaf-right { right: 20%; top: 20%; border-radius: 12% 90% 12% 90%; transform: rotate(-28deg); }
.mark-leaf-top { right: 32%; top: 4%; width: 22%; height: 16%; border-radius: 12% 90% 12% 90%; transform: rotate(-44deg); }
.mark-page { position: absolute; bottom: 6%; width: 51%; height: 25%; border: 3rpx solid currentColor; border-top: 0; }
.mark-page-left { left: 0; border-right: 0; border-radius: 8% 0 0 75%; transform: skewY(7deg); }
.mark-page-right { right: 0; border-left: 0; border-radius: 0 8% 75% 0; transform: skewY(-7deg); }
.stage-seed .mark-stem { top: 42%; height: 26%; }
.stage-seed .mark-leaf { display: none; }
.stage-sprout .mark-leaf-top { display: none; }
.compact .mark-stem { top: 20%; height: 70%; border-left-width: 2rpx; }
.compact .mark-leaf { width: 38%; height: 28%; border-width: 2rpx; }
.compact .mark-leaf-left { left: 8%; top: 34%; }
.compact .mark-leaf-right { right: 4%; top: 18%; }
.compact .mark-leaf-top { display: none; }
</style>
