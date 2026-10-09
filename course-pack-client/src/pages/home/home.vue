<template>
  <view v-if="home" class="page">
    <view class="head">
      <text class="head-name">{{ home.greeting }}，{{ home.userName }}</text>
      <view class="stage-line">
        <text>{{ home.stage.title }} · 第{{ home.stage.day }}天</text>
        <text class="stage-link" @tap="switchStage">{{ home.stage.recommend }}　切换</text>
      </view>
    </view>

    <view v-if="showGuide" class="guide-card">
      <view class="guide-copy">
        <text class="guide-title">第一次来到小站？</text>
        <text class="guide-sub">从选一节课程开始，培训、备课和课堂问答都在这里。</text>
      </view>
      <view class="guide-close" @tap="closeGuide">知道了</view>
    </view>

    <view class="hero pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goCourseDetail">
      <view class="hero-accent"></view>
      <view class="hero-content">
        <text class="hero-label">今天先准备这节课</text>
        <text class="hero-title">{{ home.todayRecommend.title }}</text>
        <text class="hero-meta">{{ home.todayRecommend.grade }} · {{ home.todayRecommend.duration }}分钟</text>
        <view class="hero-action">
          <text>开始备课</text>
          <Arrow color="#FFFFFF" :size="28" />
        </view>
      </view>
      <view class="hero-sprout" aria-hidden="true">
        <view class="leaf leaf-left"></view>
        <view class="leaf leaf-right"></view>
        <view class="stem"></view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <text class="block-title">常用工具</text>
        <text class="block-note">备课所需，直接到达</text>
      </view>
      <view class="quick-panel">
        <view
          v-for="e in home.quickEntries"
          :key="e.label"
          class="quick-item pressable"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="goEntry(e)"
        >
          <view class="quick-icon" :class="e.icon"></view>
          <view class="quick-copy">
            <text class="quick-name">{{ e.label }}</text>
            <text class="quick-meta">{{ e.meta }}</text>
          </view>
          <Arrow :color="arrowGray" :size="24" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <text class="block-title">培训进度</text>
        <text class="block-note">{{ percentDisplay }}% 已完成</text>
      </view>
      <view class="learning-row pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goTraining">
        <view class="learning-main">
          <text class="learning-course">{{ home.continueLearning.courseName }}</text>
          <text class="learning-count">已完成 {{ completedDisplay }}/{{ home.continueLearning.total }} 节</text>
          <view class="learning-progress">
            <GrowProgress :percent="home.continueLearning.percent" :height="7" />
          </view>
        </view>
        <view class="text-action">
          <text>继续学习</text>
          <Arrow :color="brandColor" :size="24" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <text class="block-title">最近备课</text>
        <text class="block-note">接着上次继续</text>
      </view>
      <view v-if="!ready" class="list-panel"><view class="skeleton-line"></view></view>
      <view v-else-if="home.recentDrafts.length" class="list-panel">
        <view
          v-for="d in home.recentDrafts"
          :key="d.id"
          class="list-row pressable"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="goPrepare"
        >
          <view class="list-copy">
            <text class="list-title">{{ d.courseName }}</text>
            <text class="list-meta">{{ d.editTime }} 编辑</text>
          </view>
          <Arrow :color="arrowGray" :size="24" />
        </view>
      </view>
      <view v-else class="empty-row" @tap="goPrepare">还没有草稿，去准备第一节课</view>
    </view>

    <view class="section news-section">
      <view class="section-head"><text class="block-title">小站动态</text></view>
      <view class="news-list">
        <view v-for="n in home.platformNews" :key="n.id" class="news-row">
          <view class="news-dot"></view>
          <text class="news-text">{{ n.text }}</text>
          <text class="news-time">{{ n.time }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchHomeData } from '@/api/home';
import type { HomeData, QuickEntry } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';
import { useCountUp } from '@/utils/useCountUp';
import { STORAGE_GUIDE_SEEN } from '@/config';
import Arrow from '@/components/common/Arrow.vue';
import GrowProgress from '@/components/common/GrowProgress.vue';

useTabBarSync(0);
const showGuide = ref(uni.getStorageSync(STORAGE_GUIDE_SEEN) !== '1');
const home = ref<HomeData | null>(null);
const ready = ref(false);
const arrowGray = '#7B8986';
const brandColor = '#238C82';
const completedSrc = computed(() => home.value?.continueLearning.completed ?? 0);
const percentSrc = computed(() => home.value?.continueLearning.percent ?? 0);
const completedDisplay = useCountUp(completedSrc);
const percentDisplay = useCountUp(percentSrc);

onLoad(async () => {
  try {
    home.value = await fetchHomeData();
    setTimeout(() => { ready.value = true; }, 180);
  } catch (e) {
    console.error('首页数据加载失败', e);
  }
});

function closeGuide() {
  showGuide.value = false;
  uni.setStorageSync(STORAGE_GUIDE_SEEN, '1');
}

const ENTRY_TAB_PATHS = ['/pages/home/home', '/pages/course/list', '/pages/training/index', '/pages/tool/index', '/pages/mine/mine'];

function goEntry(e: QuickEntry) {
  if (!e.path) return;
  if (ENTRY_TAB_PATHS.includes(e.path)) uni.switchTab({ url: e.path });
  else uni.navigateTo({ url: e.path });
}
function goCourseDetail() {
  if (home.value) uni.navigateTo({ url: `/pages/course/detail?id=${home.value.todayRecommend.id}` });
}
function goTraining() { uni.switchTab({ url: '/pages/training/index' }); }
function goPrepare() { uni.navigateTo({ url: '/subpkg/tools/prepare' }); }
function switchStage() { uni.showToast({ title: '阶段切换（演示）', icon: 'none' }); }
</script>

<style scoped lang="scss">
.page { min-height: 100vh; padding: 28rpx $space-m calc($tab-bar-height + 48rpx); background: $bg-page; }
.head { margin-bottom: 28rpx; }
.head-name { display: block; font-size: 42rpx; font-weight: 700; color: $text-primary; line-height: 1.35; }
.stage-line { display: flex; align-items: center; justify-content: space-between; gap: $space-s; margin-top: 10rpx; font-size: 24rpx; color: $text-secondary; }
.stage-link { flex-shrink: 0; color: $brand-primary; }

.guide-card { display: flex; align-items: center; gap: $space-m; margin-bottom: 24rpx; padding: 22rpx 24rpx; border: $border-card; border-radius: $radius-card; background: $bg-card; }
.guide-copy { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.guide-title { font-size: 26rpx; font-weight: 600; color: $text-primary; }
.guide-sub { font-size: 22rpx; color: $text-secondary; line-height: 1.5; }
.guide-close { flex-shrink: 0; font-size: 24rpx; font-weight: 600; color: $brand-primary; }

.hero { position: relative; min-height: 300rpx; margin-bottom: 44rpx; overflow: hidden; border-radius: $radius-card-lg; border-top-left-radius: 64rpx; background: $bg-hero; }
.hero-accent { position: absolute; right: 0; bottom: 0; width: 120rpx; height: 16rpx; border-radius: 16rpx 0 0 0; background: $brand-secondary; }
.hero-content { position: relative; z-index: 2; width: 72%; padding: 38rpx 36rpx 34rpx; display: flex; flex-direction: column; align-items: flex-start; }
.hero-label { font-size: 22rpx; font-weight: 500; color: rgba(255, 255, 255, 0.68); letter-spacing: 2rpx; }
.hero-title { margin-top: 14rpx; font-size: 38rpx; font-weight: 700; color: #fff; line-height: 1.35; }
.hero-meta { margin-top: 8rpx; font-size: 24rpx; color: rgba(255, 255, 255, 0.7); }
.hero-action { display: flex; align-items: center; gap: 4rpx; margin-top: 34rpx; font-size: 27rpx; font-weight: 600; color: #fff; }
.hero-sprout { position: absolute; right: 32rpx; top: 70rpx; width: 126rpx; height: 148rpx; opacity: 0.18; }
.leaf { position: absolute; top: 0; width: 70rpx; height: 48rpx; border: 5rpx solid #fff; border-bottom: 0; }
.leaf-left { left: 2rpx; border-radius: 60rpx 8rpx 0 8rpx; transform: rotate(28deg); }
.leaf-right { right: 2rpx; border-radius: 8rpx 60rpx 8rpx 0; transform: rotate(-28deg); }
.stem { position: absolute; left: 61rpx; top: 47rpx; width: 5rpx; height: 90rpx; border-radius: 5rpx; background: #fff; }

.section { margin-bottom: 42rpx; }
.section-head { display: flex; align-items: baseline; justify-content: space-between; gap: $space-s; margin-bottom: 16rpx; }
.block-title { font-size: 30rpx; font-weight: 700; color: $text-primary; }
.block-note { font-size: 22rpx; color: $text-secondary; }
.quick-panel, .list-panel, .learning-row { background: $bg-card; border: $border-card; border-radius: $radius-card; }
.quick-panel { display: grid; grid-template-columns: repeat(2, 1fr); overflow: hidden; }
.quick-item { min-width: 0; min-height: 112rpx; padding: 22rpx 20rpx; display: flex; align-items: center; gap: 14rpx; border-right: 1rpx solid rgba(31, 52, 49, 0.09); border-bottom: 1rpx solid rgba(31, 52, 49, 0.09); }
.quick-item:nth-child(2n) { border-right: 0; }
.quick-item:nth-last-child(-n + 2) { border-bottom: 0; }
.quick-icon { flex-shrink: 0; width: 38rpx; height: 38rpx; background: center / contain no-repeat; }
.icon-course { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23238C82' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='4' width='18' height='13' rx='2'/%3E%3Cpath d='M7 8h8M7 12h5M8 17l-2 4M16 17l2 4'/%3E%3C/svg%3E"); }
.icon-training { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23238C82' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m12 3-9 5 9 5 9-5-9-5Z'/%3E%3Cpath d='M6 11v4c0 2 3 3 6 3s6-1 6-3v-4'/%3E%3C/svg%3E"); }
.icon-tool { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23238C82' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m4 20 4-1 10-10a2.1 2.1 0 0 0-3-3L5 16l-1 4Z'/%3E%3Cpath d='m13.5 7.5 3 3'/%3E%3C/svg%3E"); }
.icon-qa { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23238C82' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20 15a3 3 0 0 1-3 3H9l-5 3V7a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v8Z'/%3E%3Cpath d='M9.5 9a2.5 2.5 0 1 1 4 2c-.9.6-1.5 1-1.5 2M12 15h.01'/%3E%3C/svg%3E"); }
.quick-copy { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.quick-name { font-size: 27rpx; font-weight: 600; color: $text-primary; }
.quick-meta { margin-top: 2rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 20rpx; color: $text-secondary; }

.learning-row { display: flex; align-items: center; gap: $space-m; padding: 26rpx 24rpx; }
.learning-main { flex: 1; min-width: 0; }
.learning-course { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 28rpx; font-weight: 600; color: $text-primary; }
.learning-count { display: block; margin-top: 4rpx; font-size: 22rpx; color: $text-secondary; }
.learning-progress { margin-top: 16rpx; }
.text-action { flex-shrink: 0; display: flex; align-items: center; color: $brand-primary; font-size: 24rpx; font-weight: 600; }

.list-panel { overflow: hidden; }
.list-row { display: flex; align-items: center; gap: $space-s; padding: 24rpx; border-bottom: 1rpx solid rgba(31, 52, 49, 0.09); }
.list-row:last-child { border-bottom: 0; }
.list-copy { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.list-title { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 27rpx; font-weight: 600; color: $text-primary; }
.list-meta { margin-top: 5rpx; font-size: 21rpx; color: $text-secondary; }
.empty-row { padding: 28rpx 24rpx; border: 1rpx dashed rgba(35, 140, 130, 0.28); border-radius: $radius-card; background: rgba(226, 241, 238, 0.38); text-align: center; font-size: 24rpx; color: $brand-primary; }
.skeleton-line { height: 92rpx; margin: 24rpx; border-radius: 8rpx; background: $bg-oat; }

.news-section { margin-bottom: 0; }
.news-list { border-top: 1rpx solid rgba(31, 52, 49, 0.12); }
.news-row { display: flex; align-items: center; gap: 12rpx; padding: 22rpx 4rpx; border-bottom: 1rpx solid rgba(31, 52, 49, 0.09); }
.news-dot { flex-shrink: 0; width: 8rpx; height: 8rpx; border-radius: 50%; background: $brand-secondary; }
.news-text { flex: 1; font-size: 24rpx; color: $text-regular; }
.news-time { flex-shrink: 0; font-size: 20rpx; color: $text-secondary; }
</style>
