<template>
  <view v-if="home" class="page">
    <view class="masthead">
      <view class="brand-lockup">
        <image class="brand-logo" src="/static/logo.png" mode="aspectFill" />
        <view class="brand-copy"><text class="brand-name">伴芽小站</text><text class="brand-theme">田野手记 · 陪伴生长</text></view>
      </view>
      <text class="date-text">{{ home.dateText }}</text>
    </view>

    <view class="welcome-note">
      <text class="welcome-greeting">{{ home.greeting }}，{{ home.userName }}</text>
      <text class="welcome-sentence">又是为一堂好课认真准备的一天。</text>
    </view>

    <view class="cover-card pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goCourseDetail">
      <view class="cover-orbit" /><view class="cover-sun" />
      <view class="cover-head">
        <text class="cover-index">FIELD NOTES · DAY {{ home.stage.day }}</text>
        <view class="stage-chip" @tap.stop="switchStage"><text>{{ home.stage.recommend }}</text><view class="switch-ico" /></view>
      </view>
      <view class="lesson-block">
        <text class="lesson-title">{{ home.todayRecommend.title }}</text>
        <text class="lesson-meta">{{ home.todayRecommend.grade }} · 预计 {{ home.todayRecommend.duration }} 分钟</text>
        <view class="lesson-action"><text>开始备课</text><Arrow color="#176F66" :size="24" /></view>
      </view>
      <view class="cover-growth" aria-hidden="true">
        <view class="sun-ray ray-1" /><view class="sun-ray ray-2" /><view class="sun-ray ray-3" />
        <view class="leaf leaf-left" /><view class="leaf leaf-right" /><view class="stem" />
        <view class="book book-left" /><view class="book book-right" />
      </view>
      <view class="cover-accent" />
    </view>

    <view class="section">
      <view class="section-head">
        <view><text class="section-kicker">QUICK START</text><text class="section-title">常用工具</text></view>
        <text class="section-note">四个入口，一步到达</text>
      </view>
      <view class="tool-grid">
        <view v-for="(entry, index) in home.quickEntries" :key="entry.label" class="tool-card pressable" :class="`tool-card-${index + 1}`" hover-class="hv-press" :hover-stay-time="80" @tap="goEntry(entry)">
          <view class="tool-top"><text class="tool-number">0{{ index + 1 }}</text><view class="tool-icon" :class="entry.icon" /></view>
          <text class="tool-name">{{ entry.label }}</text><text class="tool-meta">{{ entry.meta }}</text>
          <view class="tool-arrow"><Arrow :color="index === 0 ? '#FFFFFF' : '#176F66'" :size="24" /></view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head"><view><text class="section-kicker">GROWTH</text><text class="section-title">培训进度</text></view></view>
      <view class="progress-card pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goTraining">
        <view class="progress-score"><text class="score-number">{{ percentDisplay }}</text><text class="score-unit">%</text><text class="score-label">本期进度</text></view>
        <view class="progress-body">
          <text class="progress-course">{{ home.continueLearning.courseName }}</text>
          <text class="progress-count">已完成 {{ completedDisplay }}/{{ home.continueLearning.total }} 节</text>
          <view class="progress-line"><GrowProgress :percent="home.continueLearning.percent" :height="8" /></view>
          <view class="continue-link"><text>继续学习</text><Arrow :color="brandColor" :size="24" /></view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view><text class="section-kicker">RECENT NOTES</text><text class="section-title">最近备课</text></view>
        <text class="section-link" @tap="goPrepare">查看全部</text>
      </view>
      <view v-if="!ready" class="notes-card"><view class="skeleton-line" /></view>
      <view v-else-if="home.recentDrafts.length" class="notes-card">
        <view v-for="(draft, index) in home.recentDrafts" :key="draft.id" class="note-row pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goPrepare">
          <view class="note-time"><text>{{ String(index + 1).padStart(2, '0') }}</text><view class="note-dot" /></view>
          <view class="note-copy"><text class="note-title">{{ draft.courseName }}</text><text class="note-meta">{{ draft.editTime }} 编辑</text></view>
          <Arrow :color="arrowGray" :size="24" />
        </view>
      </view>
      <view v-else class="empty-row" @tap="goPrepare">还没有草稿，去准备第一节课</view>
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
import Arrow from '@/components/common/Arrow.vue';
import GrowProgress from '@/components/common/GrowProgress.vue';

useTabBarSync(0);
const home = ref<HomeData | null>(null);
const ready = ref(false);
const arrowGray = '#82918D';
const brandColor = '#176F66';
const completedSrc = computed(() => home.value?.continueLearning.completed ?? 0);
const percentSrc = computed(() => home.value?.continueLearning.percent ?? 0);
const completedDisplay = useCountUp(completedSrc);
const percentDisplay = useCountUp(percentSrc);

onLoad(async () => {
  try {
    home.value = await fetchHomeData();
    setTimeout(() => { ready.value = true; }, 180);
  } catch (e) { console.error('首页数据加载失败', e); }
});

const ENTRY_TAB_PATHS = ['/pages/home/home', '/pages/course/list', '/pages/training/index', '/pages/tool/index', '/pages/mine/mine'];
function goEntry(entry: QuickEntry) {
  if (!entry.path) return;
  if (ENTRY_TAB_PATHS.includes(entry.path)) uni.switchTab({ url: entry.path });
  else uni.navigateTo({ url: entry.path });
}
function goCourseDetail() { if (home.value) uni.navigateTo({ url: `/pages/course/detail?id=${home.value.todayRecommend.id}` }); }
function goTraining() { uni.switchTab({ url: '/pages/training/index' }); }
function goPrepare() { uni.navigateTo({ url: '/subpkg/tools/prepare' }); }
function switchStage() { uni.showToast({ title: '阶段切换（演示）', icon: 'none' }); }
</script>

<style scoped lang="scss">
.page { min-height: 100vh; padding: 20rpx $space-m calc($tab-bar-height + env(safe-area-inset-bottom) + 52rpx); background: $bg-page; }
.masthead { display: flex; align-items: center; justify-content: space-between; gap: 16rpx; margin-bottom: 20rpx; }
.brand-lockup { display: flex; align-items: center; gap: 12rpx; min-width: 0; }
.brand-logo { width: 58rpx; height: 58rpx; flex-shrink: 0; border-radius: 17rpx; box-shadow: 0 4rpx 14rpx rgba(23,111,102,.14); }
.brand-copy { display: flex; flex-direction: column; min-width: 0; }
.brand-name { font-size: 25rpx; font-weight: 700; color: $text-primary; }.brand-theme { margin-top: 2rpx; font-size: 20rpx; color: $brand-primary; }
.date-text { flex-shrink: 0; font-size: 21rpx; color: $text-secondary; }
.welcome-note { display: flex; flex-direction: column; margin: 10rpx 8rpx 22rpx; transform: rotate(-1deg); transform-origin: 0 50%; }
.welcome-greeting { font-size: 34rpx; font-weight: 650; line-height: 1.25; color: $text-primary; }
.welcome-sentence { margin-top: 5rpx; font-size: 22rpx; font-style: italic; color: $text-secondary; letter-spacing: 1rpx; }

.cover-card { position: relative; min-height: 314rpx; overflow: hidden; padding: 28rpx 32rpx 30rpx; border-radius: 62rpx 22rpx 62rpx 22rpx; background: $bg-hero; box-shadow: 0 14rpx 32rpx rgba(23,111,102,.15); }
.cover-head { position: relative; z-index: 3; display: flex; align-items: center; justify-content: space-between; gap: 16rpx; }
.cover-index { font-size: 20rpx; font-weight: 700; color: rgba(255,255,255,.58); letter-spacing: 2rpx; }
.stage-chip { display: flex; align-items: center; gap: 4rpx; padding: 10rpx 14rpx; border: 1rpx solid rgba(255,255,255,.2); border-radius: 24rpx 8rpx 24rpx 24rpx; color: #fff; font-size: 22rpx; }
.switch-ico { width: 20rpx; height: 20rpx; background: center / contain no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='1.8' stroke-linecap='round'%3E%3Cpath d='M7 7h10l-2-2M17 17H7l2 2'/%3E%3C/svg%3E"); }
.lesson-block { position: relative; z-index: 3; width: 70%; margin-top: 30rpx; display: flex; flex-direction: column; align-items: flex-start; }
.lesson-title { display: -webkit-box; overflow: hidden; -webkit-box-orient: vertical; -webkit-line-clamp: 2; font-size: 34rpx; font-weight: 700; line-height: 1.28; color: #fff; }
.lesson-meta { margin-top: 5rpx; font-size: 22rpx; color: rgba(255,255,255,.68); }.lesson-action { display: flex; align-items: center; margin-top: 15rpx; padding: 11rpx 18rpx; border-radius: 26rpx 9rpx 26rpx 26rpx; background: #fff; color: $brand-primary; font-size: 24rpx; font-weight: 600; }
.cover-orbit { position: absolute; right: -76rpx; top: -28rpx; width: 286rpx; height: 286rpx; border: 2rpx solid rgba(255,255,255,.1); border-radius: 50%; }.cover-sun { position: absolute; right: 50rpx; top: 42rpx; width: 54rpx; height: 54rpx; border-radius: 50%; background: $brand-secondary; }
.cover-growth { position: absolute; z-index: 2; right: 5rpx; bottom: 3rpx; width: 184rpx; height: 188rpx; opacity: .46; transform: scale(.9); transform-origin: 100% 100%; }.leaf { position: absolute; z-index: 2; top: 34rpx; width: 76rpx; height: 48rpx; background: $brand-sprout; }.leaf-left { left: 12rpx; border-radius: 70rpx 8rpx 70rpx 8rpx; transform: rotate(26deg); }.leaf-right { right: 12rpx; border-radius: 8rpx 70rpx 8rpx 70rpx; transform: rotate(-26deg); }.stem { position: absolute; left: 89rpx; top: 76rpx; width: 6rpx; height: 56rpx; border-radius: 5rpx; background: $brand-sprout; }
.book { position: absolute; bottom: 6rpx; width: 96rpx; height: 62rpx; border: 4rpx solid rgba(255,255,255,.75); border-top: 0; }.book-left { left: 0; border-right: 0; border-radius: 6rpx 0 0 54rpx; transform: skewY(8deg); }.book-right { right: 0; border-left: 0; border-radius: 0 6rpx 54rpx 0; transform: skewY(-8deg); }
.cover-accent { position: absolute; right: 0; bottom: 0; width: 136rpx; height: 17rpx; border-radius: 17rpx 0 0 0; background: $brand-secondary; }

.section { margin-top: 32rpx; }.section-head { display: flex; align-items: flex-end; justify-content: space-between; gap: 16rpx; margin-bottom: 14rpx; }.section-kicker { display: block; margin-bottom: 3rpx; font-size: 18rpx; font-weight: 700; color: $brand-primary; letter-spacing: 3rpx; }.section-title { display: block; font-size: 34rpx; font-weight: 700; color: $text-primary; }.section-note { font-size: 22rpx; color: $text-secondary; }.section-link { padding-bottom: 4rpx; font-size: 24rpx; font-weight: 600; color: $brand-primary; }
.tool-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14rpx; }.tool-card { position: relative; min-height: 132rpx; overflow: hidden; padding: 18rpx 20rpx; border: $border-card; border-radius: 28rpx 13rpx 28rpx 13rpx; background: #fff; }.tool-card-1 { color: #fff; border-color: transparent; background: $brand-primary; }.tool-card-2 { background: $brand-sky-light; }.tool-card-3 { background: $brand-secondary-light; }.tool-card-4 { background: #E9F1E4; }
.tool-top { display: flex; align-items: center; justify-content: space-between; }.tool-number { font-size: 18rpx; font-weight: 700; opacity: .5; letter-spacing: 2rpx; }.tool-icon { width: 38rpx; height: 38rpx; background: center / contain no-repeat; }.tool-name { display: block; margin-top: 9rpx; font-size: 28rpx; font-weight: 700; color: $text-primary; }.tool-card-1 .tool-name { color: #fff; }.tool-meta { display: block; margin-top: 2rpx; font-size: 20rpx; color: $text-secondary; }.tool-card-1 .tool-meta { color: rgba(255,255,255,.65); }.tool-arrow { position: absolute; right: 14rpx; bottom: 10rpx; }
.icon-course { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='4' width='18' height='13' rx='2'/%3E%3Cpath d='M7 8h8M7 12h5M8 17l-2 4M16 17l2 4'/%3E%3C/svg%3E"); }.icon-training { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2374B7D3' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m12 3-9 5 9 5 9-5-9-5Z'/%3E%3Cpath d='M6 11v4c0 2 3 3 6 3s6-1 6-3v-4'/%3E%3C/svg%3E"); }.icon-tool { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23E78A4F' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m4 20 4-1 10-10a2.1 2.1 0 0 0-3-3L5 16l-1 4Z'/%3E%3Cpath d='m13.5 7.5 3 3'/%3E%3C/svg%3E"); }.icon-qa { background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23176F66' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20 15a3 3 0 0 1-3 3H9l-5 3V7a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v8Z'/%3E%3Cpath d='M9.5 9a2.5 2.5 0 1 1 4 2c-.9.6-1.5 1-1.5 2M12 15h.01'/%3E%3C/svg%3E"); }

.progress-card { display: flex; align-items: stretch; gap: 26rpx; padding: 26rpx; border: $border-card; border-radius: 48rpx 18rpx 30rpx 18rpx; background: #fff; }.progress-score { width: 150rpx; flex-shrink: 0; display: flex; flex-wrap: wrap; align-content: center; align-items: baseline; padding-right: 24rpx; border-right: 1rpx solid rgba(37,60,55,.1); }.score-number { font-size: 68rpx; font-weight: 700; line-height: 1; color: $brand-primary; }.score-unit { font-size: 25rpx; font-weight: 700; color: $brand-secondary; }.score-label { width: 100%; margin-top: 9rpx; font-size: 21rpx; color: $text-secondary; }.progress-body { flex: 1; min-width: 0; }.progress-course { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 29rpx; font-weight: 700; color: $text-primary; }.progress-count { display: block; margin-top: 4rpx; font-size: 23rpx; color: $text-secondary; }.progress-line { margin: 18rpx 8rpx 12rpx 0; }.continue-link { display: flex; align-items: center; justify-content: flex-end; font-size: 23rpx; font-weight: 600; color: $brand-primary; }
.notes-card { overflow: hidden; border: $border-card; border-radius: 30rpx 14rpx 30rpx 14rpx; background: #fff; }.note-row { min-height: 98rpx; display: flex; align-items: center; gap: 18rpx; padding: 16rpx 22rpx; }.note-time { position: relative; width: 42rpx; align-self: stretch; display: flex; flex-direction: column; align-items: center; justify-content: center; font-size: 18rpx; font-weight: 700; color: $brand-secondary; }.note-time::after { content: ''; position: absolute; top: 0; bottom: 0; left: 50%; z-index: 0; width: 1rpx; background: rgba(162,203,169,.65); }.note-dot { position: relative; z-index: 1; width: 11rpx; height: 11rpx; margin-top: 6rpx; border: 4rpx solid #fff; border-radius: 50%; background: $brand-secondary; }.note-time text { position: relative; z-index: 1; padding: 3rpx 0; background: #fff; }.note-copy { flex: 1; min-width: 0; }.note-title { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 28rpx; font-weight: 600; color: $text-primary; }.note-meta { display: block; margin-top: 4rpx; font-size: 23rpx; color: $text-secondary; }.empty-row { padding: 28rpx; border: 1rpx dashed rgba(23,111,102,.26); border-radius: 24rpx; text-align: center; font-size: 24rpx; color: $brand-primary; }.skeleton-line { height: 94rpx; margin: 22rpx; border-radius: 8rpx; background: $bg-oat; }

@media (max-width: 350px) {
  .page { padding-right: 24rpx; padding-left: 24rpx; }.date-text { display: none; }.welcome-greeting { font-size: 31rpx; }.cover-card { min-height: 306rpx; padding-right: 26rpx; padding-left: 26rpx; }.lesson-block { width: 76%; }.lesson-title { font-size: 32rpx; }.cover-growth { right: -18rpx; transform: scale(.78); transform-origin: 100% 100%; }.tool-card { min-height: 128rpx; padding: 17rpx; }.tool-meta { font-size: 19rpx; }.section-note { display: none; }.progress-card { gap: 18rpx; padding: 22rpx; }.progress-score { width: 130rpx; padding-right: 18rpx; }.score-number { font-size: 60rpx; }
}
</style>
