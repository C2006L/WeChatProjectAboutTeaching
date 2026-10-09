<template>
  <view class="page">
    <!-- 常见问题（折叠面板，单开互斥） -->
    <view class="card">
      <text class="card-title">常见问题</text>
      <view v-for="(item, i) in faqs" :key="i" class="faq-item">
        <view
          class="faq-q pressable"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="toggle(i)"
        >
          <text class="faq-q-text">{{ item.q }}</text>
          <view class="faq-arrow" :class="{ open: openIndex === i }"></view>
        </view>
        <view v-if="openIndex === i" class="faq-a">
          <text class="faq-a-text">{{ item.a }}</text>
        </view>
      </view>
    </view>

    <!-- 联系与反馈 -->
    <view class="card">
      <text class="card-title">没有找到答案？</text>
      <view class="action-row pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goAsk">
        <view class="action-ico ico-write"></view>
        <view class="action-main">
          <text class="action-label">向项目组提问</text>
          <text class="action-desc">工作日内回复，回复可在「我的提问」查看</text>
        </view>
        <view class="action-arrow"></view>
      </view>
      <view class="action-row pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goTickets">
        <view class="action-ico ico-ticket"></view>
        <view class="action-main">
          <text class="action-label">我的提问记录</text>
          <text class="action-desc">查看回复进度与历史问答</text>
        </view>
        <view class="action-arrow"></view>
      </view>
    </view>

    <text class="version">伴芽 V1.0.0</text>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Faq {
  q: string;
  a: string;
}

const faqs: Faq[] = [
  {
    q: '找不到适合我的课程怎么办？',
    a: '在「课程」页使用关键词搜索，并结合版本、学段、主题三个筛选条件组合查找；每门课程卡片上标注了适用学段、时长与精简/标准版，可按支教天数灵活选用。',
  },
  {
    q: '如何收藏课程？在哪里查看？',
    a: '在课程卡片右侧或课程详情页底部点击星形图标即可收藏，再次点击取消。收藏的课程可在「我的 → 收藏课程」中集中查看，收藏记录在本机长期保留。',
  },
  {
    q: '备课助手怎么用？',
    a: '进入「工具 → 备课助手」，选择学段与主题后即可生成教学流程、材料清单与注意事项。编辑内容会自动保存为草稿，下次进入可继续编辑，方便多次打磨一节课。',
  },
  {
    q: '培训学习进度会记录吗？',
    a: '会。「培训」页按必修、选修分类展示，已完成节数与百分比自动统计；首页「继续学习」会显示当前进度并可一键接续到培训页。',
  },
  {
    q: '课堂上遇到敏感问题怎么应对？',
    a: '进入「工具 → 问答支持」，按分类或关键词检索标准应答话术与注意事项。如果问答库没有覆盖你的情况，点击底部「去提问」提交，项目组回复后会在「我的提问」中展示。',
  },
  {
    q: '在哪里能看到管理员对我提问的回复？',
    a: '「工具 → 问答支持 → 我的提问」标签页，或「我的 → 我的问题」直达。状态为「已回复」的工单可直接看到回复内容，「待回复」请耐心等待。',
  },
  {
    q: '《致家长的一封信》在哪里？',
    a: '进入「工具 → 家校沟通」，内含常见沟通话题话术建议，以及可直接参考使用的《致家长的一封信》范文。',
  },
  {
    q: '网络不稳定时使用受影响吗？',
    a: '弱网环境下请求会自动重试一次，仍失败时会提示检查网络。建议在网络稳定时提前打开将要使用的课程与问答页面；正式上线版本将提供完整的离线缓存能力。',
  },
];

const openIndex = ref<number>(0);

function toggle(i: number) {
  openIndex.value = openIndex.value === i ? -1 : i;
}

function goAsk() {
  uni.navigateTo({ url: '/subpkg/tools/ask' });
}

function goTickets() {
  uni.navigateTo({ url: '/subpkg/tools/qa?tab=tickets' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m;
  background: $bg-page;
}

.card {
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;
  padding: $card-padding;

  & + .card {
    margin-top: $space-m;
  }
}

.card-title {
  display: block;
  margin-bottom: $space-s;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

/* FAQ 折叠 */
.faq-item {
  border-bottom: 1rpx solid rgba(54, 82, 77, 0.08);

  &:last-child {
    border-bottom: none;
  }
}

.faq-q {
  display: flex;
  align-items: center;
  gap: $space-s;
  padding: $space-s 0;
}

.faq-q-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
  line-height: 1.5;
}

/* 箭头：1.5px 线性墨绿，展开旋转 */
.faq-arrow {
  flex-shrink: 0;
  width: 24rpx;
  height: 24rpx;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%236B7A75' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E") center / contain no-repeat;
  transition: transform $motion-base $motion-ease;

  &.open {
    transform: rotate(90deg);
  }
}

.faq-a {
  padding: 0 0 $space-m;
  animation: faq-expand $motion-base $motion-ease both;
}

/* 展开：仅 transform/opacity，遵守动效纪律 */
@keyframes faq-expand {
  from {
    opacity: 0;
    transform: translateY(-8rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.faq-a-text {
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.7;
}

/* 反馈入口行 */
.action-row {
  display: flex;
  align-items: center;
  gap: $space-m;
  padding: $space-m 0;
  border-bottom: 1rpx solid rgba(54, 82, 77, 0.08);

  &:last-child {
    border-bottom: none;
  }
}

.action-ico {
  flex-shrink: 0;
  width: 64rpx;
  height: 64rpx;
  border-radius: $radius-s;
  background-color: $bg-oat;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 36rpx;
}

.ico-write {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M5 19h14'/%3E%3Cpath d='M15.5 5.5 18.5 8.5 8 19H5v-3Z'/%3E%3C/svg%3E");
}

.ico-ticket {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v3a2 2 0 0 0 0 4v3a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-3a2 2 0 0 0 0-4Z'/%3E%3Cpath d='M13 5v14' stroke-dasharray='2 2'/%3E%3C/svg%3E");
}

.action-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.action-label {
  font-size: $font-size-s;
  color: $text-primary;
}

.action-desc {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.action-arrow {
  flex-shrink: 0;
  width: 24rpx;
  height: 24rpx;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%239AA5A1' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E") center / contain no-repeat;
}

.version {
  display: block;
  margin-top: $space-l;
  text-align: center;
  font-size: $font-size-xs;
  color: $text-disabled;
}
</style>
