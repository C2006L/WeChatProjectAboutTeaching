<template>
  <view class="page">
    <!-- 搜索 + 类型筛选 -->
    <view class="filter-panel">
      <view class="search-box">
        <view class="search-ico"></view>
        <input
          class="search-input"
          :value="keyword"
          placeholder="搜索活动名称"
          confirm-type="search"
          @input="onKeyword"
        />
      </view>
      <view class="type-tabs">
        <view
          v-for="t in typeOptions"
          :key="t"
          class="type-tab"
          :class="{ active: type === t }"
          @tap="type = t"
        >
          {{ t }}
        </view>
      </view>
    </view>

    <!-- 活动列表 -->
    <view v-if="filtered.length" class="list">
      <view v-for="a in filtered" :key="a.id" class="activity-card" @tap="showDetail(a.id)">
        <view class="tag-row">
          <text class="tag" :class="'tag-' + a.type">{{ a.type }}活动</text>
          <text class="tag-meta">{{ a.minutes }}分钟 · {{ a.grade }}</text>
        </view>
        <text class="act-title">{{ a.title }}</text>
        <text class="act-desc">{{ a.desc }}</text>
      </view>
    </view>

    <EmptyState v-else class="empty" text="没有匹配的活动" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchActivities } from '@/api/tool';
import EmptyState from '@/components/common/EmptyState.vue';
import type { Activity, ActivityType } from '@/types/mock';

const all = ref<Activity[]>([]);
const keyword = ref('');
const type = ref<'全部' | ActivityType>('全部');
const typeOptions = ['全部', '破冰', '主题', '收尾'] as const;

const filtered = computed(() =>
  all.value.filter((a) => {
    if (type.value !== '全部' && a.type !== type.value) return false;
    if (keyword.value && !a.title.includes(keyword.value)) return false;
    return true;
  }),
);

onLoad(async () => {
  try {
    all.value = await fetchActivities();
  } catch (e) {
    console.error('活动库加载失败', e);
  }
});

function onKeyword(e: Event) {
  const inputEvent = e as Event & { detail?: { value?: string } };
  const target = e.target as HTMLInputElement | null;
  keyword.value = inputEvent.detail?.value ?? target?.value ?? '';
}

// 只读详情（无收藏/上传/评论）
function showDetail(id: number) {
  const a = all.value.find((x) => x.id === id);
  if (!a) return;
  uni.showModal({
    title: a.title,
    content: `${a.type}活动 · ${a.minutes}分钟 · ${a.grade}\n\n${a.desc}`,
    showCancel: false,
    confirmText: '知道了',
  });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: $bg-page;
  padding-bottom: 40rpx;
}

/* 筛选区：燕麦底横条卡 */
.filter-panel {
  margin: $space-s $space-m;
  padding: 24rpx 0;
  background: $bg-oat;
  border-radius: $radius-card;
}
.search-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin: 0 24rpx;
  padding: 0 24rpx;
  height: 72rpx;
  border-radius: 36rpx;
  background: $bg-card;
}
.search-ico {
  width: 34rpx;
  height: 34rpx;
  flex-shrink: 0;
}
.search-input {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}
.type-tabs {
  display: flex;
  gap: 16rpx;
  margin: 24rpx 24rpx 0;
}
.type-tab {
  flex: 1;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 28rpx;
  font-size: $font-size-xs;
  color: $text-regular;
  background: $bg-card;
  &.active {
    background: $brand-primary;
    color: #FFFFFF;
    font-weight: $font-weight-bold;
  }
}

/* 活动卡：白底，间距 12px */
.list {
  padding: 0 $space-m;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.activity-card {
  padding: $space-m;
  background: $bg-card;
  border-radius: $radius-card;
}
.tag-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.tag {
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: $font-weight-bold;
}
.tag-icebreaker {
  background: $brand-primary-light;
  color: $brand-primary;
}
.tag-theme {
  background: $brand-secondary-light;
  color: $brand-secondary;
}
.tag-closing {
  background: $brand-primary-light;
  color: $succ;
}
.tag-meta {
  font-size: 22rpx;
  color: $text-secondary;
}
.act-title {
  display: block;
  margin-top: 14rpx;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
  color: $text-primary;
}
.act-desc {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-top: 8rpx;
  font-size: $font-size-xs;
  color: $text-regular;
  line-height: 1.6;
}

.empty {
  margin-top: $space-l;
}
</style>
