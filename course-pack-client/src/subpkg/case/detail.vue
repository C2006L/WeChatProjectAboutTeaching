<template>
  <view v-if="item" class="page">
    <view class="head-card">
      <view class="tag-row">
        <text class="theme-tag">{{ item.theme }}</text>
        <text class="grade-tag">{{ item.grade }}</text>
        <text v-if="item.featured" class="featured">精选案例</text>
        <text v-if="item.isMine" class="status" :class="'status-' + item.status.toLowerCase()">
          {{ statusText[item.status] }}
        </text>
      </view>
      <text class="title">{{ item.title }}</text>
      <text class="summary">{{ item.summary }}</text>
      <view class="author-row">
        <view class="avatar">{{ item.author.slice(0, 1) }}</view>
        <view class="author-info">
          <text class="author-name">{{ item.author }}</text>
          <text class="author-meta">{{ item.team }} · 更新于 {{ item.updatedAt }}</text>
        </view>
      </view>
    </view>

    <view v-if="item.reviewComment" class="review-card">
      <text class="review-title">审核修改建议</text>
      <text class="review-content">{{ item.reviewComment }}</text>
    </view>

    <view class="section-card">
      <text class="section-label">关联课程</text>
      <text class="course-name">{{ item.courseTitle }}</text>
    </view>

    <view class="section-card">
      <text class="section-title">课堂实施</text>
      <text class="section-content">{{ item.content }}</text>
    </view>

    <view class="section-card reflection-card">
      <text class="section-title">教学反思</text>
      <text class="section-content">{{ item.reflection }}</text>
    </view>

    <view class="privacy-tip">
      <text class="privacy-title">隐私与伦理说明</text>
      <text class="privacy-text">案例内容仅用于教师专业交流，请勿填写儿童真实姓名、联系方式或其他可识别信息。</text>
    </view>

    <view v-if="editable" class="bottom-bar">
      <view class="edit-btn pressable" hover-class="hv-press" @tap="editCase">编辑并重新提交</view>
    </view>
  </view>

  <view v-else-if="loaded" class="not-found">
    <EmptyState text="案例不存在或已被移除" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchTeachingCaseDetail } from '@/api/case';
import type { TeachingCase, TeachingCaseStatus } from '@/types/mock';

const item = ref<TeachingCase | null>(null);
const caseId = ref(0);
const loaded = ref(false);
const statusText: Record<TeachingCaseStatus, string> = {
  DRAFT: '草稿',
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '需修改',
};
const editable = computed(() => item.value?.isMine && ['DRAFT', 'REJECTED'].includes(item.value.status));

async function loadDetail() {
  if (!caseId.value) return;
  item.value = await fetchTeachingCaseDetail(caseId.value);
  loaded.value = true;
  if (item.value) uni.setNavigationBarTitle({ title: item.value.title });
}

onLoad((options) => {
  caseId.value = Number(options?.id) || 0;
});

onShow(loadDetail);

function editCase() {
  uni.navigateTo({ url: `/subpkg/case/submit?id=${caseId.value}` });
}
</script>

<style scoped lang="scss">
.page { min-height: 100vh; padding: $space-m $space-m calc(env(safe-area-inset-bottom) + 132rpx); background: $bg-page; display: flex; flex-direction: column; gap: $space-m; }
.head-card, .section-card { padding: $space-m; border: $border-card; border-radius: $radius-card; border-top-left-radius: $radius-card-tl; background: $bg-card; }
.tag-row { display: flex; flex-wrap: wrap; gap: $space-s; }
.theme-tag, .grade-tag, .featured, .status { padding: 5rpx 14rpx; border-radius: 18rpx; font-size: 20rpx; }
.theme-tag { background: $brand-primary-light; color: $brand-primary-dark; }
.grade-tag { background: $bg-oat; color: $text-secondary; }
.featured { background: $brand-secondary-light; color: $brand-secondary; }
.status { margin-left: auto; }
.status-draft { background: $bg-oat; color: $text-secondary; }
.status-pending { background: $brand-secondary-light; color: $warn; }
.status-approved { background: $brand-primary-light; color: $succ; }
.status-rejected { background: rgba(201,109,104,.12); color: $danger; }
.title { display: block; margin-top: $space-m; font-size: $font-size-l; line-height: 1.45; font-weight: $font-weight-bold; color: $text-primary; }
.summary { display: block; margin-top: $space-s; font-size: $font-size-s; line-height: 1.7; color: $text-regular; }
.author-row { margin-top: $space-m; padding-top: $space-m; border-top: 1rpx solid $border; display: flex; align-items: center; gap: $space-s; }
.avatar { width: 64rpx; height: 64rpx; border-radius: 50%; background: $brand-primary-light; color: $brand-primary-dark; display: flex; align-items: center; justify-content: center; font-size: $font-size-s; font-weight: $font-weight-bold; }
.author-info { display: flex; flex-direction: column; gap: 4rpx; }
.author-name { font-size: $font-size-s; font-weight: $font-weight-bold; color: $text-primary; }
.author-meta { font-size: 21rpx; color: $text-secondary; }
.review-card { padding: $space-m; border-radius: $radius-card; background: rgba(201,109,104,.09); display: flex; flex-direction: column; gap: $space-s; }
.review-title { font-size: $font-size-s; font-weight: $font-weight-bold; color: $danger; }
.review-content { font-size: $font-size-xs; line-height: 1.65; color: $text-regular; }
.section-label { display: block; font-size: 21rpx; color: $text-secondary; }
.course-name { display: block; margin-top: 6rpx; font-size: $font-size-s; font-weight: $font-weight-bold; color: $brand-primary-dark; }
.section-title { display: block; font-size: $font-size-m; font-weight: $font-weight-bold; color: $text-primary; }
.section-content { display: block; margin-top: $space-s; font-size: $font-size-s; line-height: 1.85; color: $text-regular; white-space: pre-wrap; }
.reflection-card { background: $brand-primary-light; border-color: transparent; }
.privacy-tip { padding: $space-m; display: flex; flex-direction: column; gap: 6rpx; }
.privacy-title { font-size: 21rpx; font-weight: $font-weight-bold; color: $text-secondary; }
.privacy-text { font-size: 21rpx; line-height: 1.6; color: $text-secondary; }
.bottom-bar { position: fixed; left: 0; right: 0; bottom: 0; padding: $space-s $space-m calc(env(safe-area-inset-bottom) + $space-s); background: $bg-card; box-shadow: $shadow-float; }
.edit-btn { height: 88rpx; border-radius: 44rpx; background: $brand-primary; color: #fff; display: flex; align-items: center; justify-content: center; font-size: $font-size-m; font-weight: $font-weight-bold; }
.not-found { min-height: 100vh; padding-top: 180rpx; background: $bg-page; }
</style>
