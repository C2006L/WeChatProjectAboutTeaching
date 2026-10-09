<template>
  <view class="page" v-if="loaded">
    <view class="head">
      <text class="head-title">教师工具箱</text>
      <text class="head-desc">为乡村课堂准备的实用工具</text>
    </view>

    <view class="grid" v-if="entries.length">
      <ToolCard
        v-for="entry in entries"
        :key="entry.title"
        :entry="entry"
        @navigate="onNavigate"
      />
    </view>
    <view v-else class="empty-wrap">
      <EmptyState text="工具箱暂无可用工具" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import ToolCard from '@/components/tool/ToolCard.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchToolEntries } from '@/api/tool';
import type { ToolEntry } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';

// 同步 tabBar 选中态：工具 = 3
useTabBarSync(3);

const entries = ref<ToolEntry[]>([]);
const loaded = ref(false);

onLoad(async () => {
  // try/finally 兜底：数据加载失败也要置 loaded，避免整页空白
  try {
    entries.value = await fetchToolEntries();
  } catch (e) {
    console.error('工具箱数据加载失败', e);
  } finally {
    loaded.value = true;
  }
});

function onNavigate(entry: ToolEntry) {
  if (!entry.path) {
    uni.showToast({ title: `${entry.title}功能建设中`, icon: 'none' });
    return;
  }
  uni.navigateTo({ url: entry.path });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc($tab-bar-height + 32rpx);
  background: $bg-page;
}

.head {
  padding: $space-s 0 $space-m;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.head-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.head-desc {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $space-m;
}

.empty-wrap {
  margin-top: $space-l;
}
</style>
