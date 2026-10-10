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
      <text class="name">{{ user.name }}</text>
      <text class="org">{{ user.org }} · {{ user.team }}</text>
      <text class="stage">{{ user.stage }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { UserProfile } from '@/types/mock';

interface Props {
  user: UserProfile;
}

const props = defineProps<Props>();

const isSeed = computed(() => Number(props.user.is_seed || 0) === 1);
</script>

<style scoped lang="scss">
/* 实墨绿主题用户卡（全页唯一主角色卡，无描边无投影） */
.profile-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: $space-m;
  padding: 36rpx $space-m;
  border-radius: $radius-card-lg;
  border-top-left-radius: $radius-card-tl;
  background: $bg-hero;
}

.avatar-wrap {
  flex-shrink: 0;
  position: relative;
}

.avatar {
  width: 112rpx;
  height: 112rpx;
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
</style>
