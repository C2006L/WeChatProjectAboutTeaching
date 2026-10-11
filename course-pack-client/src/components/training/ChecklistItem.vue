<template>
  <view
    class="checklist-item pressable"
    :class="{ locked: status === 'lock' }"
    hover-class="hv-press"
    :hover-stay-time="80"
    @tap="onTap"
  >
    <!-- 连续轴由父列表绘制；此处只负责三个状态的成长节点。 -->
    <view class="grow-track">
      <view class="track-node" :class="'nd-' + status">
        <view v-if="status === 'done'" class="state-check" />
        <view v-else-if="status === 'current'" class="state-sprout">
          <view class="sprout-stem" />
          <view class="sprout-leaf leaf-left" />
          <view class="sprout-leaf leaf-right" />
        </view>
      </view>
    </view>

    <view class="content">
      <view class="line">
        <text class="index">{{ index }}</text>
        <text class="title">{{ title }}</text>
        <text class="duration">{{ duration }}分钟</text>
      </view>
      <view v-if="status === 'current'" class="current-row">
        <view class="mini-bar">
          <!-- 生长动画：scaleX 0→目标（600ms，与 GrowProgress 同参数，不用 width） -->
          <view class="mini-inner" :style="{ transform: 'scaleX(' + (progress || 0) / 100 + ')' }"></view>
        </view>
        <text class="status-label status-current">学习中 {{ progress }}%</text>
      </view>
      <text v-else-if="status === 'done'" class="status-label status-done">已完成</text>
      <text v-else class="status-label status-lock">完成上一节后解锁</text>
    </view>
  </view>
</template>

<script setup lang="ts">
interface Props {
  index: number;
  title: string;
  duration: number;
  status: 'done' | 'current' | 'lock';
  progress?: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'click'): void }>();

function onTap() {
  if (props.status === 'lock') {
    uni.showToast({ title: '完成前置课程后解锁', icon: 'none' });
    return;
  }
  emit('click');
}
</script>

<style scoped lang="scss">
.checklist-item {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: $space-m;
  min-height: 108rpx;
  padding: 20rpx 22rpx;
  background: $bg-card;
  border: $border-card;
  border-radius: 30rpx 14rpx 30rpx 14rpx;

  &.locked {
    opacity: 0.6;
  }
}
.checklist-item:not(.locked):active { background: $brand-primary-light; }

/* ── 左侧成长节点；主轴仅由列表绘制，避免卡片之间出现断裂和双线 ── */
.grow-track {
  align-self: stretch;
  width: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

.track-node {
  position: relative;
  z-index: 2;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;

  &.nd-done {
    width: 36rpx;
    height: 36rpx;
    border-radius: 50%;
    background: $brand-primary-light;
  }
  &.nd-current {
    width: 38rpx;
    height: 38rpx;
    border: 3rpx solid $brand-secondary;
    border-radius: 50%;
    background: $brand-secondary-light;
  }
  &.nd-lock {
    width: 30rpx;
    height: 30rpx;
    border: 3rpx solid rgba(37, 60, 55, 0.28);
    border-radius: 50%;
    background: #FFFFFF;
  }
}

.state-check {
  width: 15rpx;
  height: 8rpx;
  margin-top: -3rpx;
  border-bottom: 3rpx solid $brand-primary;
  border-left: 3rpx solid $brand-primary;
  transform: rotate(-45deg);
}

.state-sprout { position: relative; width: 22rpx; height: 24rpx; }
.sprout-stem { position: absolute; left: 10rpx; top: 9rpx; width: 2rpx; height: 14rpx; border-radius: 2rpx; background: $brand-secondary; }
.sprout-leaf { position: absolute; top: 5rpx; width: 10rpx; height: 7rpx; border: 2rpx solid $brand-secondary; background: transparent; }
.state-sprout .leaf-left { left: 0; border-radius: 10rpx 2rpx 10rpx 2rpx; transform: rotate(24deg); }
.state-sprout .leaf-right { right: 0; border-radius: 2rpx 10rpx 2rpx 10rpx; transform: rotate(-24deg); }

/* ── 内容 ── */
.content {
  flex: 1;
  min-width: 0;
}

.line {
  display: flex;
  align-items: center;
}

.index {
  flex-shrink: 0;
  margin-right: $space-s;
  font-family: $font-family-serif;
  font-size: 30rpx;
  font-weight: $font-weight-bold;
  color: $brand-primary;
}

.title {
  flex: 1;
  min-width: 0;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.duration {
  flex-shrink: 0;
  margin-left: $space-s;
  font-size: 24rpx;
  color: $text-secondary;
}

.current-row {
  margin-top: $space-xs;
  display: flex;
  align-items: center;
  gap: $space-s;
}

.mini-bar {
  flex: 1;
  height: 6rpx;
  border-radius: 3rpx;
  background: $brand-sky-light;
  overflow: hidden;
}

.mini-inner {
  width: 100%;
  height: 100%;
  border-radius: 3rpx;
  background: $brand-sky;
  transform-origin: 0 50%;
  animation: mini-grow $motion-slow $motion-ease both;
}

@keyframes mini-grow {
  from {
    transform: scaleX(0);
  }
}

.status-label {
  display: inline-flex;
  margin-top: 8rpx;
  padding: 4rpx 12rpx;
  border-radius: 18rpx;
  flex-shrink: 0;
  font-size: 22rpx;
}
.status-current { color: $brand-primary-dark; background: $brand-sky-light; }
.status-done { color: $succ; background: rgba(111,159,120,.12); }
.status-lock { color: $text-secondary; background: $bg-oat; }
</style>
