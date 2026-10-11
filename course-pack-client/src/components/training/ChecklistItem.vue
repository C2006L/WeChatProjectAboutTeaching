<template>
  <view
    class="checklist-item pressable"
    :class="{ locked: status === 'lock' }"
    hover-class="hv-press"
    :hover-stay-time="80"
    @tap="onTap"
  >
    <!-- 垂直生长线 + 节点（done=实线实心墨绿 / current=虚线空心 / lock=浅灰） -->
    <view class="grow-track">
      <view class="track-line" :class="'ln-' + status"></view>
      <view class="track-node" :class="'nd-' + status">
        <view v-if="status === 'done'" class="node-dot"></view>
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

/* ── 左侧垂直生长线 ── */
.grow-track {
  align-self: stretch;
  width: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

.track-line {
  position: absolute;
  top: -22rpx;
  bottom: -22rpx;
  width: 4rpx;
  border-radius: 2rpx;

  &.ln-done {
    background: $brand-primary;      /* 实线：墨绿 */
  }
  &.ln-current {
    border-left: 4rpx dashed $brand-primary;   /* 虚线：墨绿 */
    background: transparent;
  }
  &.ln-lock {
    background: rgba(54, 82, 77, 0.1);  /* 浅灰 */
  }
}

.track-node {
  position: relative;
  z-index: 2;
  width: 28rpx;
  height: 28rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.nd-done {
    background: $brand-primary;   /* 实心墨绿 */
    .node-dot {
      width: 10rpx;
      height: 10rpx;
      border-radius: 50%;
      background: #FFFFFF;
    }
  }
  &.nd-current {
    border: 4rpx solid $brand-primary;   /* 空心墨绿 */
    background: #FFFFFF;
  }
  &.nd-lock {
    border: 4rpx solid rgba(54, 82, 77, 0.2);
    background: #FFFFFF;
  }
}

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
  height: 10rpx;
  border-radius: 5rpx;
  background: $brand-primary-light;
  overflow: hidden;
}

.mini-inner {
  width: 100%;
  height: 100%;
  border-radius: 5rpx;
  background: $brand-primary;
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
.status-current { color: $brand-primary-dark; background: $brand-primary-light; }
.status-done { color: $succ; background: rgba(93,155,120,.12); }
.status-lock { color: $text-secondary; background: $bg-oat; }
</style>
