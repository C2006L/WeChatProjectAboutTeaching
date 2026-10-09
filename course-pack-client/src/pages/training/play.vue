<template>
  <view v-if="training" class="page">
    <!-- 视频区：videoUrl 为空时渲染静态封面（打样 P5） -->
    <view class="video-area">
      <video
        v-if="training.videoUrl"
        class="video"
        :src="training.videoUrl"
        controls
        object-fit="contain"
      />
      <view v-else class="video-cover" @tap="onPlayTap">
        <view class="cover-plant">
          <view class="leaf leaf-left"></view>
          <view class="leaf leaf-right"></view>
          <view class="stem"></view>
          <view class="soil"></view>
        </view>
        <view class="play-btn">
          <view class="play-triangle"></view>
        </view>
        <text class="cover-text">视频资源即将上线</text>
      </view>
    </view>

    <!-- 课程信息头 -->
    <view class="info-card">
      <view class="info-head">
        <text class="info-title">{{ training.title }}</text>
        <text class="info-type" :class="training.type">{{ training.type === 'required' ? '必修' : '选修' }}</text>
      </view>
      <text class="info-meta">时长 {{ training.duration }}分钟 · 进度 {{ training.progress }}%</text>
    </view>

    <!-- Tab 区 -->
    <view class="tab-card">
      <view class="tabs">
        <view
          v-for="t in tabs"
          :key="t.key"
          class="tab"
          :class="{ active: activeTab === t.key }"
          @tap="activeTab = t.key"
        >
          <text class="tab-text">{{ t.label }}</text>
        </view>
      </view>

      <!-- 课程介绍 -->
      <view v-if="activeTab === 'intro'" class="tab-panel">
        <text class="intro-text">{{ training.introduction }}</text>
      </view>

      <!-- 课程大纲 -->
      <view v-else-if="activeTab === 'outline'" class="tab-panel">
        <view v-for="(item, i) in training.outline" :key="i" class="outline-row">
          <view class="outline-dot">{{ i + 1 }}</view>
          <text class="outline-text">{{ item }}</text>
        </view>
      </view>

      <!-- 资料下载 -->
      <view v-else class="tab-panel">
        <view v-if="training.materials && training.materials.length">
          <view
            v-for="(m, i) in training.materials"
            :key="i"
            class="material-row"
            @tap="onDownload(m.name)"
          >
            <text class="material-type" :class="m.type">{{ m.type.toUpperCase() }}</text>
            <text class="material-name">{{ m.name }}</text>
            <text class="material-action">下载</text>
          </view>
        </view>
        <view v-else class="material-empty">
          <text class="empty-text">资料整理中，敬请期待</text>
        </view>
      </view>
    </view>
  </view>

  <view v-else class="empty">
    <text class="empty-text">课程不存在或已下线</text>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchTrainingById } from '@/api/training';
import type { Training } from '@/types/mock';

const tabs = [
  { key: 'intro', label: '课程介绍' },
  { key: 'outline', label: '课程大纲' },
  { key: 'materials', label: '资料下载' },
] as const;

const activeTab = ref<(typeof tabs)[number]['key']>('intro');
const training = ref<Training | null>(null);

onLoad(async (options) => {
  const id = Number(options?.id);
  if (!id) return;
  training.value = await fetchTrainingById(id);
  if (training.value) {
    uni.setNavigationBarTitle({ title: training.value.title });
  }
});

function onPlayTap() {
  uni.showToast({ title: '视频资源即将上线', icon: 'none' });
}

function onDownload(name: string) {
  uni.showToast({ title: `${name} 开始下载（演示）`, icon: 'none' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc(env(safe-area-inset-bottom) + $space-l);
  background: $bg-page;
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.video-area {
  border-radius: $radius-card;
  overflow: hidden;
  box-shadow: $shadow-card;
}

.video {
  width: 100%;
  height: 400rpx;
}

.video-cover {
  height: 400rpx;
  background: $brand-primary-light;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $space-m;
  position: relative;
}

.cover-plant {
  position: relative;
  width: 100rpx;
  height: 130rpx;
}

.stem {
  position: absolute;
  left: 50%;
  bottom: 16rpx;
  transform: translateX(-50%);
  width: 8rpx;
  height: 80rpx;
  background: $brand-primary;
  border-radius: 4rpx;
}

.leaf {
  position: absolute;
  width: 36rpx;
  height: 20rpx;
  border-radius: 50%;
  background: $brand-primary;
  opacity: 0.85;
}

.leaf-left {
  top: 26rpx;
  left: 10rpx;
  transform: rotate(-25deg);
}

.leaf-right {
  top: 14rpx;
  right: 10rpx;
  transform: rotate(25deg);
}

.soil {
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 56rpx;
  height: 12rpx;
  border-radius: 6rpx;
  background: $brand-secondary;
  opacity: 0.6;
}

.play-btn {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: $brand-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-float;
}

.play-triangle {
  width: 0;
  height: 0;
  margin-left: 10rpx;
  border-top: 18rpx solid transparent;
  border-bottom: 18rpx solid transparent;
  border-left: 30rpx solid #FFFFFF;
}

.cover-text {
  font-size: $font-size-xs;
  color: $brand-primary;
}

.info-card {
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  padding: $space-m;
}

.info-head {
  display: flex;
  align-items: center;
  gap: $space-s;
}

.info-title {
  flex: 1;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.info-type {
  flex-shrink: 0;
  padding: 4rpx 16rpx;
  border-radius: $radius-s;
  font-size: 20rpx;
  font-weight: $font-weight-bold;

  &.required {
    background: $brand-primary-light;
    color: $brand-primary;
  }

  &.elective {
    background: rgba(217, 154, 98, 0.16);
    color: $brand-secondary;
  }
}

.info-meta {
  display: block;
  margin-top: $space-xs;
  font-size: $font-size-xs;
  color: $text-secondary;
}

.tab-card {
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  padding: $space-m;
}

.tabs {
  display: flex;
  gap: $space-s;
}

.tab {
  flex: 1;
  padding: 12rpx 0;
  border-radius: $radius-m;
  background: $bg-page;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    background: $brand-primary-light;
  }
}

.tab-text {
  font-size: $font-size-xs;
  color: $text-regular;
}

.tab.active .tab-text {
  color: $brand-primary;
  font-weight: $font-weight-bold;
}

.tab-panel {
  margin-top: $space-m;
}

.intro-text {
  font-size: $font-size-s;
  color: $text-regular;
  line-height: 1.7;
}

.outline-row {
  display: flex;
  align-items: center;
  gap: $space-s;
  margin-bottom: $space-s;

  &:last-child {
    margin-bottom: 0;
  }
}

.outline-dot {
  flex-shrink: 0;
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  background: $brand-primary-light;
  color: $brand-primary;
  font-size: $font-size-xs;
  font-weight: $font-weight-bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.outline-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-regular;
}

.material-row {
  display: flex;
  align-items: center;
  gap: $space-s;
  padding: $space-s 0;

  &:not(:last-child) {
    border-bottom: 1rpx solid $border;
  }
}

.material-type {
  flex-shrink: 0;
  padding: 4rpx 12rpx;
  border-radius: $radius-s;
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  background: $bg-page;
  color: $text-secondary;
}

.material-name {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}

.material-action {
  flex-shrink: 0;
  font-size: $font-size-xs;
  color: $brand-primary;
  font-weight: $font-weight-bold;
}

.material-empty {
  padding: $space-l 0;
  display: flex;
  justify-content: center;
}

.empty {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-page;
}

.empty-text {
  font-size: $font-size-s;
  color: $text-secondary;
}
</style>
