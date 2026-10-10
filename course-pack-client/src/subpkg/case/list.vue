<template>
  <view class="page">
    <view class="intro-card">
      <text class="eyebrow">来自一线课堂</text>
      <text class="intro-title">把一次实践，变成下一位老师的底气</text>
      <text class="intro-desc">案例均经过审核与脱敏处理，不展示儿童真实姓名和可识别信息。</text>
    </view>

    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab pressable"
        :class="{ active: activeTab === tab.key }"
        hover-class="hv-press"
        @tap="activeTab = tab.key"
      >
        <text>{{ tab.label }}</text>
        <text v-if="tab.key === 'mine' && pendingCount" class="count">{{ pendingCount }}</text>
      </view>
    </view>

    <view class="filter-card">
      <view class="search-box">
        <view class="search-icon"></view>
        <input v-model="keyword" class="search-input" placeholder="搜索标题、课程或关键词" />
      </view>
      <scroll-view scroll-x :show-scrollbar="false" class="filter-scroll">
        <view class="chip-row">
          <view
            v-for="grade in gradeOptions"
            :key="grade"
            class="chip"
            :class="{ active: activeGrade === grade }"
            @tap="activeGrade = grade"
          >{{ grade }}</view>
        </view>
      </scroll-view>
      <scroll-view scroll-x :show-scrollbar="false" class="filter-scroll theme-scroll">
        <view class="chip-row">
          <view
            v-for="theme in themeOptions"
            :key="theme"
            class="chip"
            :class="{ active: activeTheme === theme }"
            @tap="activeTheme = theme"
          >{{ theme }}</view>
        </view>
      </scroll-view>
    </view>

    <view v-if="!loaded" class="loading-list">
      <view v-for="item in 3" :key="item" class="skeleton"></view>
    </view>

    <view v-else-if="filtered.length" class="case-list">
      <view
        v-for="item in filtered"
        :key="item.id"
        class="case-card pressable"
        hover-class="hv-press"
        :hover-stay-time="80"
        @tap="goDetail(item.id)"
      >
        <view class="card-head">
          <view class="meta-tags">
            <text class="theme-tag">{{ item.theme }}</text>
            <text class="grade-tag">{{ item.grade }}</text>
          </view>
          <text v-if="activeTab === 'mine'" class="status" :class="'status-' + item.status.toLowerCase()">
            {{ statusText[item.status] }}
          </text>
          <text v-else-if="item.featured" class="featured">精选</text>
        </view>
        <text class="case-title">{{ item.title }}</text>
        <text class="case-summary">{{ item.summary }}</text>
        <view class="course-row">
          <text class="course-label">关联课程</text>
          <text class="course-name">{{ item.courseTitle }}</text>
        </view>
        <view class="card-foot">
          <text>{{ item.author }} · {{ item.team }}</text>
          <text>{{ item.updatedAt }}</text>
        </view>
        <view v-if="item.status === 'REJECTED' && item.reviewComment" class="review-note">
          <text class="review-label">修改建议</text>
          <text class="review-text">{{ item.reviewComment }}</text>
        </view>
      </view>
    </view>

    <EmptyState v-else class="empty" :text="emptyText" />

    <view class="submit-bar">
      <view class="submit-btn pressable" hover-class="hv-press" @tap="goSubmit">
        <text class="plus">＋</text>
        <text>提交我的教学案例</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchMyTeachingCases, fetchTeachingCases } from '@/api/case';
import type { TeachingCase, TeachingCaseStatus } from '@/types/mock';

type TabKey = 'all' | 'featured' | 'mine';

const tabs: { key: TabKey; label: string }[] = [
  { key: 'all', label: '全部案例' },
  { key: 'featured', label: '精选' },
  { key: 'mine', label: '我的' },
];
const gradeOptions = ['全部年级', '小学1-3年级', '小学4-6年级'] as const;
const themeOptions = ['全部主题', '身体与安全', '家庭与合作', '人际与尊重', '成长与变化', '综合主题'] as const;
const statusText: Record<TeachingCaseStatus, string> = {
  DRAFT: '草稿',
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '需修改',
};

const activeTab = ref<TabKey>('all');
const activeGrade = ref<(typeof gradeOptions)[number]>('全部年级');
const activeTheme = ref<(typeof themeOptions)[number]>('全部主题');
const keyword = ref('');
const publicCases = ref<TeachingCase[]>([]);
const myCases = ref<TeachingCase[]>([]);
const loaded = ref(false);

onLoad((options) => {
  if (options?.tab === 'mine') activeTab.value = 'mine';
});

onShow(async () => {
  loaded.value = false;
  try {
    [publicCases.value, myCases.value] = await Promise.all([
      fetchTeachingCases(),
      fetchMyTeachingCases(),
    ]);
  } catch {
    uni.showToast({ title: '案例加载失败，请重试', icon: 'none' });
  } finally {
    loaded.value = true;
  }
});

const pendingCount = computed(() => myCases.value.filter((item) => item.status === 'PENDING').length);

const filtered = computed(() => {
  let source = activeTab.value === 'mine' ? myCases.value : publicCases.value;
  if (activeTab.value === 'featured') source = source.filter((item) => item.featured);
  const term = keyword.value.trim();
  return source.filter((item) => {
    if (activeGrade.value !== '全部年级' && item.grade !== activeGrade.value) return false;
    if (activeTheme.value !== '全部主题' && item.theme !== activeTheme.value) return false;
    if (term && !`${item.title}${item.summary}${item.courseTitle}`.includes(term)) return false;
    return true;
  });
});

const emptyText = computed(() => {
  if (activeTab.value === 'mine') return '还没有提交案例，记录一次课堂实践吧';
  if (activeTab.value === 'featured') return '暂时没有符合筛选条件的精选案例';
  return '没有找到匹配的案例，换个筛选试试';
});

function goDetail(id: number) {
  uni.navigateTo({ url: `/subpkg/case/detail?id=${id}` });
}

function goSubmit() {
  uni.navigateTo({ url: '/subpkg/case/submit' });
}
</script>

<style scoped lang="scss">
.page { min-height: 100vh; padding: $space-m $space-m calc(env(safe-area-inset-bottom) + 144rpx); background: $bg-page; }
.intro-card { padding: $space-l; border-radius: $radius-card; border-top-left-radius: $radius-card-tl; background: $bg-hero; display: flex; flex-direction: column; }
.eyebrow { font-size: 22rpx; color: rgba(255,255,255,.68); letter-spacing: 2rpx; }
.intro-title { margin-top: $space-s; font-size: $font-size-l; line-height: 1.45; font-weight: $font-weight-bold; color: #fff; }
.intro-desc { margin-top: $space-s; font-size: $font-size-xs; line-height: 1.6; color: rgba(255,255,255,.78); }
.tabs { display: flex; gap: $space-s; margin-top: $space-m; }
.tab { flex: 1; height: 68rpx; display: flex; align-items: center; justify-content: center; gap: 6rpx; border-radius: 34rpx; background: $bg-card; color: $text-regular; font-size: $font-size-xs; }
.tab.active { background: $brand-primary; color: #fff; font-weight: $font-weight-bold; }
.count { min-width: 28rpx; height: 28rpx; padding: 0 6rpx; border-radius: 14rpx; background: $brand-secondary; color: #fff; font-size: 18rpx; line-height: 28rpx; text-align: center; }
.filter-card { margin-top: $space-m; padding: $space-m; border: $border-card; border-radius: $radius-card; background: $bg-card; }
.search-box { height: 72rpx; padding: 0 $space-m; display: flex; align-items: center; gap: $space-s; border-radius: 36rpx; background: $bg-page; }
.search-icon { width: 32rpx; height: 32rpx; background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5'%3E%3Ccircle cx='10.8' cy='10.8' r='6.5'/%3E%3Cpath d='m16 16 4.2 4.2'/%3E%3C/svg%3E") center / contain no-repeat; }
.search-input { flex: 1; font-size: $font-size-s; color: $text-primary; }
.filter-scroll { margin-top: $space-s; white-space: nowrap; }
.theme-scroll { margin-top: 10rpx; }
.chip-row { display: inline-flex; gap: $space-s; }
.chip { height: 52rpx; padding: 0 20rpx; display: inline-flex; align-items: center; border-radius: 26rpx; background: $bg-oat; color: $text-regular; font-size: 22rpx; }
.chip.active { background: $brand-primary-light; color: $brand-primary-dark; font-weight: $font-weight-bold; }
.case-list, .loading-list { margin-top: $space-m; display: flex; flex-direction: column; gap: $space-m; }
.case-card { padding: $space-m; border: $border-card; border-radius: $radius-card; border-top-left-radius: $radius-card-tl; background: $bg-card; }
.card-head, .meta-tags, .card-foot, .course-row { display: flex; align-items: center; }
.card-head { justify-content: space-between; gap: $space-s; }
.meta-tags { gap: $space-s; min-width: 0; }
.theme-tag, .grade-tag, .featured, .status { padding: 5rpx 14rpx; border-radius: 18rpx; font-size: 20rpx; }
.theme-tag { background: $brand-primary-light; color: $brand-primary-dark; }
.grade-tag { background: $bg-oat; color: $text-secondary; }
.featured { background: $brand-secondary-light; color: $brand-secondary; font-weight: $font-weight-bold; }
.status-draft { background: $bg-oat; color: $text-secondary; }
.status-pending { background: $brand-secondary-light; color: $warn; }
.status-approved { background: $brand-primary-light; color: $succ; }
.status-rejected { background: rgba(201,109,104,.12); color: $danger; }
.case-title { display: block; margin-top: $space-m; font-size: $font-size-m; line-height: 1.45; font-weight: $font-weight-bold; color: $text-primary; }
.case-summary { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-top: $space-s; font-size: $font-size-xs; line-height: 1.65; color: $text-regular; }
.course-row { margin-top: $space-m; gap: $space-s; }
.course-label { font-size: 20rpx; color: $text-secondary; }
.course-name { font-size: $font-size-xs; color: $brand-primary-dark; }
.card-foot { justify-content: space-between; margin-top: $space-m; padding-top: $space-s; border-top: 1rpx solid $border; font-size: 21rpx; color: $text-secondary; }
.review-note { margin-top: $space-m; padding: $space-s; border-radius: $radius-s; background: rgba(201,109,104,.08); display: flex; flex-direction: column; gap: 6rpx; }
.review-label { font-size: 20rpx; font-weight: $font-weight-bold; color: $danger; }
.review-text { font-size: 22rpx; line-height: 1.55; color: $text-regular; }
.skeleton { height: 300rpx; border-radius: $radius-card; background: $bg-oat; }
.empty { margin-top: $space-l; }
.submit-bar { position: fixed; left: 0; right: 0; bottom: 0; padding: $space-s $space-m calc(env(safe-area-inset-bottom) + $space-s); background: $bg-card; box-shadow: $shadow-float; }
.submit-btn { height: 88rpx; border-radius: 44rpx; background: $brand-primary; display: flex; align-items: center; justify-content: center; gap: $space-s; color: #fff; font-size: $font-size-m; font-weight: $font-weight-bold; }
.plus { font-size: 38rpx; line-height: 1; font-weight: 400; }
</style>
