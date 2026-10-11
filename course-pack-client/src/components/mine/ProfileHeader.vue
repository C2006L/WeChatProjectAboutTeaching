<template>
  <view class="profile-header">
    <!-- 头像（白描边）+ 种子徽章 -->
    <view class="avatar-wrap">
      <view class="avatar">
        <text class="avatar-text">{{ user.name?.slice(0, 1) || '伴' }}</text>
      </view>
      <view v-if="isSeed" class="seed-badge">
        <view class="seed-ico"></view>
        <text class="seed-text">种子教师</text>
      </view>
    </view>

    <view class="info">
      <text class="profile-kicker">教师工作台</text>
      <text class="name">{{ user.name }}</text>
      <text class="org">{{ user.org }} · {{ user.team }}</text>
      <text class="stage">{{ user.stage }}</text>
    </view>

    <view class="training" @tap="emit('continue')">
      <view class="training-head">
        <text class="training-label">培训进度</text>
        <text class="training-count">{{ completed }}/{{ total }} 节</text>
      </view>
      <view class="training-line">
        <view class="training-fill" :style="{ transform: 'scaleX(' + percent / 100 + ')' }"></view>
      </view>
      <view class="training-foot">
        <text class="training-percent">{{ percent }}%</text>
        <text class="training-action">继续学习</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { UserProfile } from '@/types/mock';

interface Props {
  user: UserProfile;
  percent: number;
  completed: number;
  total: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'continue'): void }>();

const isSeed = computed(() => Number(props.user.is_seed || 0) === 1);
</script>

<style scoped lang="scss">
/* 实墨绿主题用户卡（全页唯一主角色卡，无描边无投影） */
.profile-header {
  position: relative;
  display: grid;
  grid-template-columns: auto 1fr;
  align-items: center;
  gap: $space-m;
  padding: 28rpx $space-m;
  border-radius: 34rpx 16rpx 34rpx 16rpx;
  border-top-left-radius: $radius-card-tl;
  background: $bg-hero;
}

.avatar-wrap {
  flex-shrink: 0;
  position: relative;
}

.avatar {
  width: 98rpx;
  height: 98rpx;
  border-radius: 44rpx 44rpx 44rpx 18rpx;
  border: 4rpx solid rgba(255, 255, 255, .88);
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: #FFFFFF;
}

/* 种子教师：暖橙描边徽章 */
.seed-badge {
  position: absolute;
  left: 50%;
  bottom: -18rpx;
  transform: translateX(-50%);
  display: inline-flex;
  align-items: center;
  gap: 4rpx;
  white-space: nowrap;
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
  border: 2rpx solid rgba(192, 131, 82, 1); /* 暖橙描边 */
  background: $bg-hero;
}

.seed-ico {
  width: 24rpx;
  height: 24rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23C08352' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M13 4 4.5 17.5a2.5 2.5 0 0 0 4 1.2L13 15'/%3E%3Cpath d='M13 4c3.8 1.6 6.2 4.7 6.7 9.1a3.5 3.5 0 0 1-5.6 2.7L13 15'/%3E%3C/svg%3E");
}

.seed-text {
  font-size: 22rpx;
  font-weight: $font-weight-bold;
  color: $accent-warm;
}

.left-pad {
  width: 0;
}

.info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.profile-kicker { font-size: 21rpx; font-weight: 600; color: rgba(255,255,255,.58); letter-spacing: 2rpx; }

.name {
  font-size: 36rpx;
  font-weight: $font-weight-bold;
  color: #FFFFFF;
}

.org {
  font-size: $font-size-xs;
  color: rgba(255, 255, 255, 0.7);
}

.stage {
  font-size: $font-size-xs;
  color: rgba(255, 255, 255, 0.85);
}

.training { grid-column: 1 / -1; margin-top: 14rpx; padding-top: 16rpx; border-top: 1rpx solid rgba(255,255,255,.14); }
.training-head, .training-foot { display: flex; align-items: center; justify-content: space-between; }
.training-label, .training-count { font-size: 24rpx; color: rgba(255,255,255,.72); }
.training-line { height: 8rpx; margin-top: 12rpx; overflow: hidden; border-radius: 4rpx; background: rgba(255,255,255,.18); }
.training-fill { width: 100%; height: 100%; border-radius: 4rpx; background: $brand-secondary; transform-origin: 0 50%; }
.training-foot { margin-top: 10rpx; }
.training-percent { font-size: 28rpx; font-weight: 700; color: #fff; }
.training-action { padding: 8rpx 16rpx; border-radius: 22rpx; background: rgba(255,255,255,.12); font-size: 23rpx; color: #fff; }
</style>
