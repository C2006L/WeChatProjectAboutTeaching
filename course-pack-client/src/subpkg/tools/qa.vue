<template>
  <view class="page">
    <!-- Tab 切换：问答库（标准应答） / 我的提问（工单状态） -->
    <view class="tab-row">
      <view
        class="tab pressable"
        :class="{ active: tab === 'library' }"
        hover-class="hv-press"
        :hover-stay-time="80"
        @tap="tab = 'library'"
      >
        <text class="tab-text">问答库</text>
      </view>
      <view
        class="tab pressable"
        :class="{ active: tab === 'tickets' }"
        hover-class="hv-press"
        :hover-stay-time="80"
        @tap="switchTickets"
      >
        <text class="tab-text">我的提问</text>
        <view v-if="pendingCount" class="tab-badge">{{ pendingCount }}</view>
      </view>
    </view>

    <!-- ── Tab 1：敏感问答库（原有搜索/筛选/列表，逻辑不变） ── -->
    <view v-if="tab === 'library'">
      <!-- 搜索 -->
      <view class="search-box">
        <view class="search-icon"></view>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索问题关键词"
          confirm-type="search"
        />
      </view>

      <!-- 分类 -->
      <scroll-view scroll-x :show-scrollbar="false" class="chip-scroll">
        <view class="chip-row">
          <view
            v-for="c in categories"
            :key="c"
            class="chip"
            :class="{ active: category === c }"
            @tap="category = c"
          >
            {{ c }}
          </view>
        </view>
      </scroll-view>

      <!-- 问题列表 -->
      <view v-if="list.length" class="list">
        <QAListItem v-for="qa in list" :key="qa.id" :qa="qa" />
      </view>
      <EmptyState
        v-else
        class="empty"
        text="没有找到相关问题，换个关键词试试"
      />
    </view>

    <!-- ── Tab 2：我的提问工单 ── -->
    <view v-else class="tickets">
      <view v-if="tickets.length">
        <view v-for="t in tickets" :key="t.id" class="ticket-card">
          <view class="ticket-head">
            <text class="ticket-title">{{ t.title }}</text>
            <text class="ticket-status" :class="t.status === 'PENDING' ? 'st-pending' : 'st-replied'">
              {{ t.status === 'PENDING' ? '待回复' : '已回复' }}
            </text>
          </view>
          <text class="ticket-meta">{{ t.category }} · {{ t.time }}</text>
          <text class="ticket-content">{{ t.content }}</text>

          <!-- 管理员回复 -->
          <view v-if="t.status === 'REPLIED' && t.reply" class="reply-box">
            <view class="reply-head">
              <text class="reply-badge">回复</text>
              <text class="reply-meta">{{ t.reply.admin }} · {{ t.reply.time }}</text>
            </view>
            <text class="reply-content">{{ t.reply.content }}</text>
          </view>
        </view>
      </view>
      <EmptyState v-else class="empty" text="还没有提问，点击下方按钮向项目组提问" />
    </view>

    <!-- 去提问 -->
    <view class="ask-bar">
      <view class="ask-btn pressable" hover-class="hv-press" :hover-stay-time="80" @tap="onAsk">
        <text class="ask-text">去提问</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import QAListItem from '@/components/qa/QAListItem.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchQaList } from '@/api/qa';
import { fetchMyTickets } from '@/api/feedback';
import type { QA, FeedbackTicket } from '@/types/mock';

const categories = ['全部', '身体与安全', '人际关系', '成长变化'] as const;

const keyword = ref('');
const category = ref<string>('全部');
const all = ref<QA[]>([]);

/** 当前 Tab：library 问答库 / tickets 我的提问 */
const tab = ref<'library' | 'tickets'>('library');
const tickets = ref<FeedbackTicket[]>([]);
const pendingCount = computed(() => tickets.value.filter((t) => t.status === 'PENDING').length);

onLoad(async (options) => {
  all.value = await fetchQaList();
  // 支持「我的-我的问题」直达工单 Tab
  if (options?.tab === 'tickets') tab.value = 'tickets';
});

// 从提问页返回时刷新工单状态
onShow(() => {
  if (tab.value === 'tickets') loadTickets();
});

async function loadTickets() {
  tickets.value = await fetchMyTickets();
}

function switchTickets() {
  tab.value = 'tickets';
  loadTickets();
}

const list = computed(() =>
  all.value.filter((q) => {
    if (category.value !== '全部' && q.category !== category.value) return false;
    if (keyword.value) {
      const kw = keyword.value.trim();
      if (!q.question.includes(kw) && !q.scene.includes(kw)) return false;
    }
    return true;
  }),
);

function onAsk() {
  uni.navigateTo({ url: '/subpkg/tools/ask' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc(env(safe-area-inset-bottom) + 140rpx);
  background: $bg-page;
}

/* ── 顶部 Tab ── */
.tab-row {
  display: flex;
  gap: $space-s;
  margin-bottom: $space-m;
}

.tab {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 72rpx;
  border-radius: 36rpx;
  background: $bg-card;
  box-shadow: $shadow-card;
}

.tab.active {
  background: $brand-primary;
  box-shadow: none;
}

.tab-text {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-regular;
}

.tab.active .tab-text {
  color: #FFFFFF;
}

.tab-badge {
  margin-left: 8rpx;
  min-width: 32rpx;
  height: 32rpx;
  line-height: 32rpx;
  padding: 0 8rpx;
  border-radius: 16rpx;
  background: $brand-secondary;
  color: #FFFFFF;
  font-size: 20rpx;
  text-align: center;
}

.tab.active .tab-badge {
  background: rgba(255, 255, 255, 0.25);
}

.search-box {
  display: flex;
  align-items: center;
  gap: $space-s;
  padding: 0 $space-m;
  height: 72rpx;
  border-radius: 36rpx;
  background: $bg-card;
  box-shadow: $shadow-card;
}

/* 搜索图标：1.5px 线性墨绿（data-uri，禁 emoji） */
.search-icon {
  flex-shrink: 0;
  width: 34rpx;
  height: 34rpx;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none'%3E%3Ccircle cx='11' cy='11' r='6.5' stroke='%2336524D' stroke-width='1.5'/%3E%3Cpath d='m16 16 4.5 4.5' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round'/%3E%3C/svg%3E") center / contain no-repeat;
}

.search-input {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}

.chip-scroll {
  margin-top: $space-s;
  white-space: nowrap;
}

.chip-row {
  display: inline-flex;
  gap: $space-s;
}

.chip {
  display: inline-flex;
  align-items: center;
  height: 56rpx;
  padding: 0 $space-m;
  border-radius: 28rpx;
  font-size: $font-size-xs;
  color: $text-regular;
  background: $bg-card;
  box-shadow: $shadow-card;

  &.active {
    background: $brand-primary-light;
    color: $brand-primary;
    font-weight: $font-weight-bold;
  }
}

.list {
  margin-top: $space-m;
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.empty {
  margin-top: $space-l;
}

/* ── 我的提问工单 ── */
.tickets {
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.ticket-card {
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;
  padding: $card-padding;
}

.ticket-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: $space-s;
}

.ticket-title {
  flex: 1;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
  line-height: 1.5;
}

.ticket-status {
  flex-shrink: 0;
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.st-pending {
  color: $brand-secondary;
  background: $brand-secondary-light;
}

.st-replied {
  color: $brand-primary;
  background: $brand-primary-light;
}

.ticket-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $text-secondary;
}

.ticket-content {
  display: block;
  margin-top: $space-s;
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.6;
}

.reply-box {
  margin-top: $space-m;
  padding: $space-s $space-m;
  border-radius: $radius-s;
  background: $brand-primary-light;
}

.reply-head {
  display: flex;
  align-items: center;
  gap: $space-s;
  margin-bottom: 8rpx;
}

.reply-badge {
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  color: #FFFFFF;
  background: $brand-primary;
  border-radius: 6rpx;
  padding: 2rpx 12rpx;
}

.reply-meta {
  font-size: 20rpx;
  color: $text-secondary;
}

.reply-content {
  font-size: $font-size-xs;
  color: $text-primary;
  line-height: 1.6;
}

.ask-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $space-s $space-m calc(env(safe-area-inset-bottom) + $space-s);
  background: $bg-card;
  box-shadow: $shadow-float;
}

.ask-btn {
  height: 88rpx;
  border-radius: 44rpx;
  background: $brand-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  --press-shadow: none;
}

.ask-text {
  color: #FFFFFF;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
}
</style>
