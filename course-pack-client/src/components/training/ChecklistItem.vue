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
        <text class="current-label">学习中 {{ progress }}%</text>
      </view>
      <text v-else-if="status === 'done'" class="done-label">已完成</text>
      <text v-else class="lock-label">完成上一节后解锁</text>
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
/* 卡片材质 token：圆角24/左上40裁角/墨绿8%描边/墨绿6%投影/暖米白卡底 */
.checklist-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: $space-m;
  padding: $space-m;
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;

  &.locked {
    opacity: 0.6;
  }
}

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
  top: 0;
  bottom: 0;
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
    background: $bg-card;
  }
  &.nd-lock {
    border: 4rpx solid rgba(54, 82, 77, 0.2);
    background: $bg-card;
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
  font-size: 20rpx;
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

.current-label {
  flex-shrink: 0;
  font-size: 20rpx;
  color: $brand-primary;
}

.done-label {
  display: block;
  margin-top: 4rpx;
  font-size: 20rpx;
  color: $text-secondary;
}

.lock-label {
  display: block;
  margin-top: 4rpx;
  font-size: 20rpx;
  color: $text-disabled;
}
</style>