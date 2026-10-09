<template>
  <view class="page">
    <!-- 问候 -->
    <view class="hello">
      <text class="hello-sub">家校沟通 · 小助手</text>
    </view>

    <!-- ① 页头插画：老师与孩子对话（墨绿单色线条，约 120px） -->
    <view class="hero-illu">
      <view class="hero-svg"></view>
    </view>

    <!-- ② 话术卡片：左上角 "气泡 + 小芽" 角标 -->
    <view class="scripts">
      <view v-for="s in scripts" :key="s.id" class="script-card">
        <view class="script-badge">
          <view class="badge-svg"></view>
        </view>
        <text class="script-topic">{{ s.topic }}</text>
        <text class="script-scene">{{ s.scene }}</text>
        <view class="script-body">
          <text class="script-suggest">{{ s.suggestion }}</text>
        </view>
        <view class="script-tips">
          <view v-for="(t, i) in s.tips" :key="i" class="tip">
            <text class="tip-dot">·</text>
            <text class="tip-text">{{ t }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ③ 致家长的一封信：信纸质感 -->
    <view class="letter-section">
      <text class="letter-title">{{ letter.subject }}</text>

      <view class="letter-paper">
        <view class="letter-content">
          <text v-for="(p, i) in letter.paragraphs" :key="i" class="letter-para">{{ p }}</text>
          <view class="letter-sign">
            <text class="sign-name">{{ letter.signature }}</text>
            <text class="sign-date">{{ letter.date }}</text>
          </view>
          <!-- 右下角小芽印章 -->
          <view class="stamp"></view>
        </view>
      </view>

      <!-- 预览 / 导出（功能按钮，保持普通，禁用可爱元素） -->
      <view class="letter-actions">
        <button class="btn ghost" @tap="previewLetter">预览</button>
        <button class="btn primary" @tap="exportPdf">导出 PDF</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchFamilyScripts, fetchFamilyLetter } from '@/api/tool';
import type { FamilyScript, FamilyLetter } from '@/types/mock';

const scripts = ref<FamilyScript[]>([]);
const letter = ref<FamilyLetter>({ subject: '', paragraphs: [], signature: '', date: '' });

onLoad(async () => {
  try {
    const [s, l] = await Promise.all([fetchFamilyScripts(), fetchFamilyLetter()]);
    scripts.value = s;
    letter.value = l;
  } catch (e) {
    console.error('家校沟通数据加载失败', e);
  }
});

// 预览：信纸全文
function previewLetter() {
  uni.showModal({
    title: letter.value.subject,
    content: letter.value.paragraphs.join('\n\n'),
    showCancel: false,
    confirmText: '关闭',
  });
}

// 导出 PDF：V2 演示
function exportPdf() {
  uni.showToast({ title: '导出功能开发中', icon: 'none' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: $bg-page;
  padding: 0 $space-m 60rpx;
}

.hello-sub {
  display: block;
  padding: $space-s 0;
  font-size: 26rpx;
  color: $text-secondary;
}

/* ① 页头插画 */
.hero-illu {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 8rpx 0 8rpx;
}
.hero-svg {
  width: 100%;
  height: 150rpx;   /* 约 75px 高，居中展示 */
}

/* ② 话术卡 */
.scripts {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.script-card {
  position: relative;
  padding: $space-m;
  background: $bg-card;
  border-radius: $radius-card;
}
.script-badge {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: $brand-primary-light;
  display: flex;
  align-items: center;
  justify-content: center;
}
.badge-svg {
  width: 26rpx;
  height: 26rpx;
}
.script-topic {
  display: block;
  margin-left: 52rpx;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
  color: $text-primary;
}
.script-scene {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $text-secondary;
}
.script-body {
  margin-top: 16rpx;
  padding: 20rpx;
  background: $bg-page;
  border-radius: $radius-m;
}
.script-suggest {
  display: block;
  font-size: 28rpx;
  color: $text-regular;
  line-height: 1.7;
}
.script-tips {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-top: 16rpx;
}
.tip {
  display: flex;
  gap: 10rpx;
}
.tip-dot {
  color: $brand-primary;
  font-weight: $font-weight-bold;
}
.tip-text {
  font-size: 24rpx;
  color: $text-secondary;
  flex: 1;
}

/* ③ 信纸 */
.letter-section {
  margin-top: $space-l;
}
.letter-title {
  display: block;
  font-size: $font-size-l;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $space-s;
}
/* 信纸：浅燕麦底 + 横线纹理 + 右下小芽印章 */
.letter-paper {
  position: relative;
  background-color: $bg-oat;
  background-image: repeating-linear-gradient(
    transparent 0,
    transparent 46rpx,
    rgba(79, 143, 131, 0.12) 46rpx,
    rgba(79, 143, 131, 0.12) 47rpx
  );
  border-radius: $radius-card;
  border: 2rpx solid $border;
  padding: 48rpx 36rpx 56rpx;
}
.letter-content {
  position: relative;
}
.letter-para {
  display: block;
  margin-bottom: 24rpx;
  font-size: 28rpx;
  color: $text-regular;
  line-height: 1.6;
}
.letter-sign {
  margin-top: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
}
.sign-name {
  font-size: 28rpx;
  color: $text-primary;
  font-weight: 600;
}
.sign-date {
  font-size: 24rpx;
  color: $text-secondary;
}
.stamp {
  position: absolute;
  right: 8rpx;
  bottom: -24rpx;
  width: 64rpx;
  height: 64rpx;
  /* 小芽印章（data-uri，小程序不支持 svg 标签） */
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 64 64' fill='none'%3E%3Cellipse cx='32' cy='40' rx='16' ry='14' stroke='%2336524D' stroke-width='1.6' opacity='0.5'/%3E%3Cpath d='M34 46V28' stroke='%2336524D' stroke-width='1.6' stroke-linecap='round' opacity='0.55'/%3E%3Cpath d='M34 31c-7 0-11-4-11-10 7 0 11 4 11 10Z' fill='%2336524D' opacity='0.18'/%3E%3Cpath d='M34 31c7 0 11-4 11-10-7 0-11 4-11 10Z' fill='%2336524D' opacity='0.22'/%3E%3C/svg%3E") center / contain no-repeat;
}

.letter-actions {
  margin-top: $space-m;
  display: flex;
  gap: $space-m;
}
.btn {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
}
.btn.ghost {
  background: $bg-card;
  color: $brand-primary;
  border: 2rpx solid $brand-primary;
}
.btn.primary {
  background: $brand-primary;
  color: #FFFFFF;
}
</style>